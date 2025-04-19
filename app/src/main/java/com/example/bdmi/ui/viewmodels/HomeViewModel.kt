package com.example.bdmi.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bdmi.data.api.APIError
import com.example.bdmi.data.api.CastMember
import com.example.bdmi.data.api.CrewMember
import com.example.bdmi.data.api.Movie
import com.example.bdmi.data.api.MovieDetails
import com.example.bdmi.data.api.toAPIError
import com.example.bdmi.data.repositories.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val movieRepo: MovieRepository) : ViewModel() {
    data class HomeUIState(
        override val isLoading: Boolean = false,
        val movies: List<Movie> = emptyList(),
        override val error: APIError? = null
    ) : UIState

    data class DetailUIState(
        override val isLoading: Boolean = false,
        val movieDetails: MovieDetails? = null,
        val hasBackdrop: Boolean? = null,
        val cast: List<CastMember> = emptyList(),
        val crew: List<CrewMember> = emptyList(),
        val directors: String = "Unknown",
        override val error: APIError? = null
    ) : UIState

    private val _homeUIState = MutableStateFlow(HomeUIState())
    val homeUIState = _homeUIState.asStateFlow()

    private val _detailUIState = MutableStateFlow(DetailUIState())
    val detailUIState = _detailUIState.asStateFlow()

    fun refreshHome() {
        _homeUIState.update { HomeUIState(isLoading = true, movies = emptyList(), error = null) }
        loadMovies()
    }

    fun refreshDetails(movieId: Int) {
        _detailUIState.update {
            DetailUIState(
                isLoading = true,
                movieDetails = null,
                hasBackdrop = null,
                cast = emptyList(),
                crew = emptyList(),
                directors = "Unknown",
                error = null
            )
        }
        loadMovieDetails(movieId)
    }

    private fun loadMovies() {
        viewModelScope.launch {
            _homeUIState.update { it.copy(isLoading = true, error = null) }
            // Simulate Network Delay
            delay(1000)
            movieRepo.discoverMovies().fold(
                onSuccess = { response ->
                    _homeUIState.update {
                        it.copy(
                            movies = response.results,
                            error = null,
                            isLoading = false
                        )
                    }
                },
                onFailure = { e ->
                    _homeUIState.update {
                        it.copy(
                            error = e.toAPIError(),
                            isLoading = false
                        )
                    }
                    e.printStackTrace()
                }
            )
        }
    }

    /* TODO: Parallel API calls */
    private fun loadMovieDetails(movieId: Int) {
        viewModelScope.launch {
            _detailUIState.update {
                it.copy(
                    isLoading = true,
                    movieDetails = null,
                    hasBackdrop = null,
                    cast = emptyList(),
                    crew = emptyList(),
                    directors = "Unknown",
                    error = null
                )
            }
            // Simulate Network Delay
            delay(1000)
            movieRepo.getMovieDetails(movieId).fold(
                onSuccess = { details ->
                    val hasBackdrop = details.backdropPath?.isNotEmpty()

                    _detailUIState.update {
                        it.copy(
                            movieDetails = details,
                            hasBackdrop = hasBackdrop,
                            error = null,
                        )
                    }
                },
                onFailure = { e ->
                    _detailUIState.update {
                        it.copy(
                            error = e.toAPIError(),
                        )
                    }
                    e.printStackTrace()
                }
            )
            movieRepo.getMovieCredits(movieId).fold(
                onSuccess = { credits ->
                    val directors = credits.crew
                        .filter { it.job.equals("director", ignoreCase = true) }
                        .takeIf { it.isNotEmpty() }
                        ?.joinToString(", ") { it.name }
                        ?: "Unknown"

                    _detailUIState.update {
                        it.copy(
                            cast = credits.cast,
                            crew = credits.crew,
                            directors = directors,
                            isLoading = false
                        )
                    }
                },
                onFailure = { e ->
                    _detailUIState.update {
                        it.copy(
                            error = e.toAPIError(),
                            isLoading = false
                        )
                    }
                    e.printStackTrace()
                }
            )
        }
    }
}