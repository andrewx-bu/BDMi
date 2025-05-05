package com.example.bdmi.ui.home.movie_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bdmi.data.api.APIError
import com.example.bdmi.data.api.models.Movie
import com.example.bdmi.data.api.toAPIError
import com.example.bdmi.data.repositories.MovieRepository
import com.example.bdmi.data.utils.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenreViewModel @Inject constructor(
    private val movieRepo: MovieRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    data class GenreUIState(
        override val isLoading: Boolean = false,
        val movies: List<Movie> = emptyList(),
        val page: Int = 1,
        val totalPages: Int = Int.MAX_VALUE,
        // filter fields
        val sortBy: String = "popularity.desc",
        val voteCountGte: Float? = null,
        val voteCountLte: Float? = null,
        val voteAverageGte: Float? = null,
        val voteAverageLte: Float? = null,
        override val error: APIError? = null
    ) : UIState

    private val _uiState = MutableStateFlow(GenreUIState())
    val uiState: StateFlow<GenreUIState> = _uiState.asStateFlow()

    val genreId: String = savedStateHandle.get<String>("genreId") ?: ""

    init {
        loadMovies(resetPage = true)
    }

    fun setSortBy(sort: String) {
        _uiState.update { it.copy(sortBy = sort) }
        loadMovies(resetPage = true)
    }

    fun setVoteCountRange(min: Float?, max: Float?) {
        _uiState.update { it.copy(voteCountGte = min, voteCountLte = max) }
        loadMovies(resetPage = true)
    }

    fun setVoteAverageRange(min: Float?, max: Float?) {
        _uiState.update { it.copy(voteAverageGte = min, voteAverageLte = max) }
        loadMovies(resetPage = true)
    }

    fun loadNextPage() {
        val state = _uiState.value
        if (state.isLoading || state.page >= state.totalPages) return

        _uiState.update { it.copy(page = it.page + 1) }
        loadMovies(resetPage = false)
    }

    fun refresh() = loadMovies(resetPage = true)

    private fun loadMovies(resetPage: Boolean) {
        viewModelScope.launch {
            val currentState = _uiState.value
            val targetPage = if (resetPage) 1 else currentState.page

            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    page = targetPage,
                    movies = if (resetPage) emptyList() else it.movies
                )
            }

            movieRepo.discoverMovies(
                page = targetPage,
                genres = genreId,
                sortBy = currentState.sortBy,
                voteCountGte = currentState.voteCountGte,
                voteCountLte = currentState.voteCountLte,
                voteAverageGte = currentState.voteAverageGte,
                voteAverageLte = currentState.voteAverageLte
            ).fold(
                onSuccess = { response ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            movies = if (resetPage) response.results else currentState.movies + response.results,
                            totalPages = response.totalPages
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.toAPIError(),
                            page = currentState.page // Revert page increment if failed
                        )
                    }
                }
            )
        }
    }
}