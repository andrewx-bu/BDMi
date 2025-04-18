package com.example.bdmi.data.repositories

import com.example.bdmi.data.api.APIService
import com.example.bdmi.data.api.MovieCreditsResponse
import com.example.bdmi.data.api.MovieDetails
import com.example.bdmi.data.api.MoviesResponse
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val apiService: APIService
) {
    suspend fun discoverMovies(page: Int = 1): Result<MoviesResponse> =
        runCatching {
            apiService.discoverMovies(page = page)
        }

    suspend fun getMovieDetails(movieId: Int): Result<MovieDetails> =
        runCatching {
            apiService.getMovieDetails(movieId = movieId)
        }

    suspend fun getMovieCredits(movieId: Int): Result<MovieCreditsResponse> =
        runCatching {
            apiService.getMovieCredits(movieId = movieId)
        }
}