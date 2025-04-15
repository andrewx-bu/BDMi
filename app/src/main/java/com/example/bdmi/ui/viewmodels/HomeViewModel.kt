package com.example.bdmi.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bdmi.data.api.CastMember
import com.example.bdmi.data.api.CrewMember
import com.example.bdmi.data.api.Movie
import com.example.bdmi.data.api.MovieDetails
import com.example.bdmi.data.repositories.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val movieRepo: MovieRepository) : ViewModel() {
    data class HomeUIState(
        val isLoading: Boolean = false,
        val movies: List<Movie> = emptyList(),
        val error: String? = null
    )

    data class DetailUIState(
        val isLoading: Boolean = false,
        val movieDetails: MovieDetails? = null,
        val error: String? = null
    )

    data class CreditsUIState(
        val isLoading: Boolean = false,
        val cast: List<CastMember> = emptyList(),
        val crew: List<CrewMember> = emptyList(),
        val error: String? = null
    )

    private val _homeUIState = MutableStateFlow(HomeUIState())
    val homeUIState = _homeUIState.asStateFlow()

    private val _detailUIState = MutableStateFlow(DetailUIState())
    val detailUIState = _detailUIState.asStateFlow()

    private val _creditsUIState = MutableStateFlow(CreditsUIState())
    val creditsUIState = _creditsUIState.asStateFlow()

    fun refreshHome() {
        _homeUIState.update { HomeUIState(isLoading = true, movies = emptyList(), error = null) }
        loadMovies()
    }

    fun refreshDetails(movieId: Int) {
        _detailUIState.update { DetailUIState(isLoading = true, movieDetails = null, error = null) }
        loadMovieDetails(movieId)
    }

    fun refreshCredits(movieId: Int) {
        _creditsUIState.update { CreditsUIState(isLoading = true, cast = emptyList(), crew = emptyList(), error = null) }
        loadMovieCredits(movieId)
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
                            error = when (e) {
                                is IOException -> "Network error. Please check your internet connection."
                                is HttpException -> when (e.code()) {
                                    404 -> "Content not found"
                                    500 -> "Server error. Please try again later."
                                    else -> "Server error (${e.code()})"
                                }
                                else -> "Failed to load movies"
                            },
                            isLoading = false
                        )
                    }
                    e.printStackTrace()
                }
            )
        }
    }

    private fun loadMovieDetails(movieId: Int) {
        viewModelScope.launch {
            _detailUIState.update { it.copy(isLoading = true, error = null) }
            movieRepo.getMovieDetails(movieId).fold(
                onSuccess = { details ->
                    _detailUIState.update {
                        it.copy(
                            movieDetails = details,
                            error = null,
                            isLoading = false
                        )
                    }
                },
                onFailure = { e ->
                    _detailUIState.update {
                        it.copy(
                            error = when (e) {
                                is IOException -> "Network error. Please check your internet connection."
                                is HttpException -> when (e.code()) {
                                    404 -> "Content not found"
                                    500 -> "Server error. Please try again later."
                                    else -> "Server error (${e.code()})"
                                }
                                else -> "Failed to load movie details"
                            },
                            isLoading = false
                        )
                    }
                    e.printStackTrace()
                }
            )
        }
    }

    private fun loadMovieCredits(movieId: Int) {
        viewModelScope.launch {
            _creditsUIState.update { it.copy(isLoading = true, error = null) }

            movieRepo.getMovieCredits(movieId).fold(
                onSuccess = { credits ->
                    _creditsUIState.update {
                        it.copy(
                            cast = credits.cast,
                            crew = credits.crew,
                            isLoading = false
                        )
                    }
                },
                onFailure = { e ->
                    _creditsUIState.update {
                        it.copy(
                            error = when (e) {
                                is IOException -> "Network error. Please check your internet connection."
                                is HttpException -> when (e.code()) {
                                    404 -> "Content not found"
                                    500 -> "Server error. Please try again later."
                                    else -> "Server error (${e.code()})"
                                }
                                else -> "Failed to load movie credits"
                            },
                            isLoading = false
                        )
                    }
                    e.printStackTrace()
                }
            )
        }
    }
}