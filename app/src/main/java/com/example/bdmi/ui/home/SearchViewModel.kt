package com.example.bdmi.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bdmi.data.api.APIError
import com.example.bdmi.data.api.models.Movie
import com.example.bdmi.data.api.toAPIError
import com.example.bdmi.data.repositories.MovieRepository
import com.example.bdmi.data.utils.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MovieFilters(
    val sortBy: String = "popularity.desc",
    val withGenres: String? = null,
    val withPeople: String? = null,
    val voteCountGte: Float? = null,
    val voteCountLte: Float? = null,
    val voteAverageGte: Float? = null,
    val voteAverageLte: Float? = null
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : ViewModel() {

    data class SearchUIState(
        override val isLoading: Boolean = false,
        val movies: List<Movie> = emptyList(),
        val page: Int = 1,
        val totalPages: Int = Int.MAX_VALUE,
        override val error: APIError? = null,
        val filters: MovieFilters = MovieFilters()
    ) : UIState

    private val _uiState = MutableStateFlow(SearchUIState())
    val uiState = _uiState.asStateFlow()

    fun refreshScreen() {
        val currentFilters = _uiState.value.filters
        _uiState.value = SearchUIState(isLoading = true, filters = currentFilters)
        loadMovies(page = 1, filters = currentFilters, append = false)
    }

    fun loadFirstPage(filters: MovieFilters = MovieFilters()) {
        _uiState.value = SearchUIState(isLoading = true, filters = filters)
        loadMovies(page = 1, filters = filters, append = false)
    }

    fun loadNextPage() {
        val state = _uiState.value
        if (state.isLoading || state.page >= state.totalPages) return
        loadMovies(page = state.page + 1, filters = state.filters, append = true)
    }

    private fun loadMovies(page: Int, filters: MovieFilters, append: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            movieRepository.discoverMovies(
                page = page,
                sortBy = filters.sortBy,
                genres = filters.withGenres,
                people = filters.withPeople,
                voteCountGte = filters.voteCountGte,
                voteCountLte = filters.voteCountLte,
                voteAverageGte = filters.voteAverageGte,
                voteAverageLte = filters.voteAverageLte
            ).fold(
                onSuccess = { response ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            movies = if (append) it.movies + response.results else response.results,
                            page = response.page,
                            totalPages = response.totalPages
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.toAPIError()) }
                }
            )
        }
    }
}
