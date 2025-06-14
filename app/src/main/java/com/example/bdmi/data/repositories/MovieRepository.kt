package com.example.bdmi.data.repositories

import com.example.bdmi.data.api.APIService
import com.example.bdmi.data.api.models.Company
import com.example.bdmi.data.api.models.MovieDetails
import com.example.bdmi.data.api.models.MoviesResponse
import com.example.bdmi.data.api.models.PersonDetails
import com.example.bdmi.data.api.models.WatchProvidersResponse
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val apiService: APIService
) {
    suspend fun getNowPlayingMovies(page: Int = 1): Result<MoviesResponse> = runCatching {
        apiService.getNowPlayingMovies(page = page)
    }

    suspend fun getPopularMovies(page: Int = 1): Result<MoviesResponse> = runCatching {
        apiService.getPopularMovies(page = page)
    }

    suspend fun getTopRatedMovies(page: Int = 1): Result<MoviesResponse> = runCatching {
        apiService.getTopRatedMovies(page = page)
    }

    suspend fun getUpcomingMovies(page: Int = 1): Result<MoviesResponse> = runCatching {
        apiService.getUpcomingMovies(page = page)
    }

    suspend fun discoverMovies(
        page: Int = 1,
        sortBy: String = "popularity.desc",
        genres: String? = null,
        people: String? = null,
        companies: String? = null,
        country: String? = null,
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
            companies = companies,
            country = country,
            voteCountGte = voteCountGte,
            voteCountLte = voteCountLte,
            voteAverageGte = voteAverageGte,
            voteAverageLte = voteAverageLte
        )
    }

    suspend fun getCompanyDetails(companyId: Int): Result<Company> = runCatching {
        apiService.getCompanyDetails(companyId = companyId)
    }

    suspend fun getPersonDetails(
        personId: Int,
        appendToResponse: String = "combined_credits",
        language: String = "en-US"
    ): Result<PersonDetails> = runCatching {
        apiService.getPersonDetails(
            personId = personId,
            appendToResponse = appendToResponse,
            language = language
        )
    }

    suspend fun searchMovies(
        query: String,
        page: Int = 1,
        includeAdult: Boolean = false,
        primaryReleaseYear: String? = null,
    ): Result<MoviesResponse> = runCatching {
        apiService.searchMovies(
            query = query,
            page = page,
            includeAdult = includeAdult,
            primaryReleaseYear = primaryReleaseYear,
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