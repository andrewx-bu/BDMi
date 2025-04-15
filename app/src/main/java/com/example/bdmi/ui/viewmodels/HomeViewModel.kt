package com.example.bdmi.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bdmi.data.api.Movie
import com.example.bdmi.data.api.MovieDetails
import com.example.bdmi.data.repositories.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val movieRepo: MovieRepository) : ViewModel() {

    // List of movies
    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies = _movies.asStateFlow()

    // Specific movie details
    private val _movieDetails = MutableStateFlow<MovieDetails?>(null)
    val movieDetails = _movieDetails.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun loadMovies() {
        viewModelScope.launch {
            movieRepo.discoverMovies().fold(
                onSuccess = { response ->
                    _movies.value = response.results
                    _error.value = null
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Unexpected error"
                    exception.printStackTrace()
                }
            )
        }
    }

    fun loadMovieDetails(movieId: Int) {
        viewModelScope.launch {
            movieRepo.getMovieDetails(movieId).fold(
                onSuccess = { details ->
                    _movieDetails.value = details
                    _error.value = null
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Unexpected error"
                    exception.printStackTrace()
                }
            )
        }
    }
}