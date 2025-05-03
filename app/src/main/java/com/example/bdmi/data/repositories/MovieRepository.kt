package com.example.bdmi.data.repositories

import com.example.bdmi.data.api.APIService
import com.example.bdmi.data.api.models.MovieDetails
import com.example.bdmi.data.api.models.MoviesResponse
import com.example.bdmi.data.api.models.WatchProvidersResponse
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val apiService: APIService
) {
    suspend fun discoverMovies(
        page: Int = 1,
        sortBy: String = "popularity.desc",
        genres: String? = null,
        people: String? = null,
        voteCountGte: Float? = null,
        voteCountLte: Float? = null,
        voteAverageGte: Float? = null,
        voteAverageLte: Float? = null
    ): Result<MoviesResponse> = runCatching {
        apiService.discoverMovies(
            page = page,
            sortBy = sortBy,
            genres = genres,
            people = people,
            voteCountGte = voteCountGte,
            voteCountLte = voteCountLte,
            voteAverageGte = voteAverageGte,
            voteAverageLte = voteAverageLte
        )
    }

    suspend fun searchMovies(
        query: String,
        page: Int = 1,
        includeAdult: Boolean = false,
        region: String? = null,
        year: String? = null
    ): Result<MoviesResponse> = runCatching {
        apiService.searchMovies(
            query = query,
            page = page,
            includeAdult = includeAdult,
            region = region,
            year = year
        )
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