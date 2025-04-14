package com.example.bdmi.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bdmi.data.api.MovieDetails
import com.example.bdmi.data.repositories.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailScreenViewModel @Inject constructor(
    private val movieRepo: MovieRepository,
) : ViewModel() {
    private val _movieDetails = MutableStateFlow<MovieDetails?>(null)
    val movieDetails = _movieDetails.asStateFlow()

    fun loadMovieDetails(movieId: Int) {
        viewModelScope.launch {
            try {
                val details = movieRepo.getMovieDetails(movieId)
                _movieDetails.value = details
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}