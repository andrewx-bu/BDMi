package com.example.bdmi.ui.onboarding

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bdmi.data.api.models.Movie
import com.example.bdmi.data.api.models.MoviesResponse
import com.example.bdmi.data.api.toAPIError
import com.example.bdmi.data.repositories.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val movieRepo: MovieRepository
) : ViewModel() {
    private var _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies : StateFlow<List<Movie>> = _movies.asStateFlow()

    init {
        loadMovies()
    }

    private fun loadMovies() {
        viewModelScope.launch {
            val result: Result<MoviesResponse> = movieRepo.getPopularMovies()

            result.fold(
                onSuccess = { response ->
                    _movies.value = response.results
                    Log.d("Movies", "Success")
                },
                onFailure = { e ->
                    e.toAPIError()
                    Log.d("Movies", "Failure")
                }
            )
        }
    }
}