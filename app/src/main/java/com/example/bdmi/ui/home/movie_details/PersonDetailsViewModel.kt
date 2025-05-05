package com.example.bdmi.ui.home.movie_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bdmi.data.api.APIError
import com.example.bdmi.data.api.models.Movie
import com.example.bdmi.data.api.models.PersonDetails
import com.example.bdmi.data.api.toAPIError
import com.example.bdmi.data.repositories.MovieRepository
import com.example.bdmi.data.utils.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonDetailsViewModel @Inject constructor(
    private val movieRepo: MovieRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    data class PersonUIState(
        override val isLoading: Boolean = false,
        val movies: List<Movie> = emptyList(),
        val page: Int = 1,
        val totalPages: Int = Int.MAX_VALUE,
        val person: PersonDetails? = null,
        // filter fields
        val sortBy: String = "popularity.desc",
        val voteCountGte: Float? = null,
        val voteCountLte: Float? = null,
        val voteAverageGte: Float? = null,
        val voteAverageLte: Float? = null,
        override val error: APIError? = null
    ) : UIState

    private val _uiState = MutableStateFlow(PersonUIState())
    val uiState: StateFlow<PersonUIState> = _uiState.asStateFlow()

    private val personId: String = savedStateHandle.get<String>("personId") ?: ""

    init {
        loadInitialData()
    }

    fun refresh() = loadInitialData()

    fun setSortBy(sort: String) {
        _uiState.update { it.copy(sortBy = sort, page = 1, movies = emptyList()) }
        loadMovies()
    }

    fun setVoteCountRange(min: Float?, max: Float?) {
        _uiState.update {
            it.copy(
                voteCountGte = min,
                voteCountLte = max,
                page = 1,
                movies = emptyList()
            )
        }
        loadMovies()
    }

    fun setVoteAverageRange(min: Float?, max: Float?) {
        _uiState.update {
            it.copy(
                voteAverageGte = min,
                voteAverageLte = max,
                page = 1,
                movies = emptyList()
            )
        }
        loadMovies()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    page = 1,
                    movies = emptyList(),
                    totalPages = Int.MAX_VALUE,
                    person = null
                )
            }

            val id = personId.toIntOrNull()

            // fetch person details + first page of movies in parallel
            val personDeferred = id?.let { async { movieRepo.getPersonDetails(it) } }
            val moviesDeferred = async {
                movieRepo.discoverMovies(
                    page = 1,
                    people = personId.takeIf { it.isNotBlank() },
                    sortBy = _uiState.value.sortBy,
                    voteCountGte = _uiState.value.voteCountGte,
                    voteCountLte = _uiState.value.voteCountLte,
                    voteAverageGte = _uiState.value.voteAverageGte,
                    voteAverageLte = _uiState.value.voteAverageLte
                )
            }

            personDeferred
                ?.await()
                ?.fold(
                    onSuccess = { details ->
                        _uiState.update { it.copy(person = details) }
                    },
                    onFailure = { e ->
                        _uiState.update {
                            it.copy(isLoading = false, error = e.toAPIError())
                        }
                    }
                )

            moviesDeferred
                .await()
                .fold(
                    onSuccess = { response ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                movies = response.results,
                                totalPages = response.totalPages
                            )
                        }
                    },
                    onFailure = { e ->
                        _uiState.update {
                            it.copy(isLoading = false, error = e.toAPIError())
                        }
                    }
                )
        }
    }

    fun loadNextPage() {
        val s = _uiState.value
        if (s.isLoading || s.page >= s.totalPages) return
        _uiState.update { it.copy(page = it.page + 1) }
        loadMovies()
    }

    private fun loadMovies() {
        val s = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            movieRepo.discoverMovies(
                page = s.page,
                people = personId.takeIf { it.isNotBlank() },
                sortBy = s.sortBy,
                voteCountGte = s.voteCountGte,
                voteCountLte = s.voteCountLte,
                voteAverageGte = s.voteAverageGte,
                voteAverageLte = s.voteAverageLte
            ).fold(
                onSuccess = { response ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            movies = if (s.page == 1) response.results else s.movies + response.results,
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