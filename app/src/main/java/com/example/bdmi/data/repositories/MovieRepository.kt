package com.example.bdmi.data.repositories

import com.example.bdmi.data.api.APIService
import com.example.bdmi.data.api.MovieDetails
import com.example.bdmi.data.api.MoviesResponse
import com.example.bdmi.data.api.WatchProvidersResponse
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

    suspend fun getWatchProviders(movieId: Int): Result<WatchProvidersResponse> =
        runCatching {
            apiService.getMovieWatchProviders(movieId)
        }
}