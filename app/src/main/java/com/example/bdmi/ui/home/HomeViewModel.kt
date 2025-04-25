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

@HiltViewModel
class HomeViewModel @Inject constructor(private val movieRepo: MovieRepository) : ViewModel() {
    data class HomeUIState(
        override val isLoading: Boolean = false,
        val movies: List<Movie> = emptyList(),
        override val error: APIError? = null
    ) : UIState

    private val _homeUIState = MutableStateFlow(HomeUIState())
    val homeUIState = _homeUIState.asStateFlow()

    fun refreshHome() {
        _homeUIState.update { HomeUIState(isLoading = true, movies = emptyList(), error = null) }
        loadMovies()
    }

    private fun loadMovies() {
        viewModelScope.launch {
            _homeUIState.update { it.copy(isLoading = true, error = null) }
            movieRepo.discoverMovies().fold(
                onSuccess = { response ->
                    // If no movies are fetched, we'll throw an empty response error
                    if (response.results.isEmpty()) {
                        _homeUIState.update {
                            it.copy(error = APIError.EmptyResponseError(), isLoading = false)
                        }
                    } else {
                        _homeUIState.update {
                            it.copy(movies = response.results, isLoading = false)
                        }
                    }
                },
                onFailure = { e ->
                    _homeUIState.update { it.copy(error = e.toAPIError(), isLoading = false) }
                }
            )
        }
    }
}