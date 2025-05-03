package com.example.bdmi.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bdmi.data.api.APIError
import com.example.bdmi.data.api.models.Movie
import com.example.bdmi.data.api.models.MoviesResponse
import com.example.bdmi.data.api.toAPIError
import com.example.bdmi.data.repositories.MovieRepository
import com.example.bdmi.data.utils.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class MovieCategory(val displayName: String) {
    NOW_PLAYING("Now Playing"),
    POPULAR("Popular"),
    TOP_RATED("Top Rated"),
    UPCOMING("Upcoming")
}

@HiltViewModel
class HomeViewModel @Inject constructor(private val movieRepo: MovieRepository) : ViewModel() {
    data class HomeUIState(
        override val isLoading: Boolean = false,
        val movies: List<Movie> = emptyList(),
        override val error: APIError? = null,
        val category: MovieCategory = MovieCategory.POPULAR
    ) : UIState

    private val _homeUIState = MutableStateFlow(HomeUIState())
    val homeUIState = _homeUIState.asStateFlow()

    fun setCategory(category: MovieCategory) {
        _homeUIState.update {
            HomeUIState(
                isLoading = true,
                movies = emptyList(),
                error = null,
                category = category
            )
        }
        loadMovies()
    }

    fun refreshHome() {
        _homeUIState.update {
            it.copy(
                isLoading = true,
                movies = emptyList(),
                error = null
            )
        }
        loadMovies()
    }

    private fun loadMovies() {
        val category = _homeUIState.value.category

        viewModelScope.launch {
            _homeUIState.update { it.copy(isLoading = true, error = null) }

            val result: Result<MoviesResponse> = when (category) {
                MovieCategory.NOW_PLAYING -> movieRepo.getNowPlayingMovies()
                MovieCategory.POPULAR -> movieRepo.getPopularMovies()
                MovieCategory.TOP_RATED -> movieRepo.getTopRatedMovies()
                MovieCategory.UPCOMING -> movieRepo.getUpcomingMovies()
            }

            result.fold(
                onSuccess = { response ->
                    // empty response, throw error
                    if (response.results.isEmpty()) {
                        _homeUIState.update {
                            it.copy(
                                isLoading = false,
                                error = APIError.EmptyResponseError()
                            )
                        }
                    } else {
                        _homeUIState.update {
                            it.copy(
                                isLoading = false,
                                movies = response.results
                            )
                        }
                    }
                },
                onFailure = { e ->
                    _homeUIState.update {
                        it.copy(
                            isLoading = false,
                            error = e.toAPIError()
                        )
                    }
                }
            )
        }
    }
}