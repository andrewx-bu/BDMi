package com.example.bdmi.data.api

import com.example.bdmi.BuildConfig
import com.example.bdmi.data.api.models.Company
import com.example.bdmi.data.api.models.MovieDetails
import com.example.bdmi.data.api.models.MoviesResponse
import com.example.bdmi.data.api.models.PersonDetails
import com.example.bdmi.data.api.models.WatchProvidersResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface APIService {
    // Details Screens
    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("page") page: Int = 1,
        // popularity, revenue, primary_release_date, title, vote_average, and vote_count
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("with_genres") genres: String? = null,
        @Query("with_people") people: String? = null,
        @Query("with_companies") companies: String? = null,
        @Query("with_origin_country") country: String? = null,
        @Query("vote_count.gte") voteCountGte: Float? = null,
        @Query("vote_count.lte") voteCountLte: Float? = null,
        @Query("vote_average.gte") voteAverageGte: Float? = null,
        @Query("vote_average.lte") voteAverageLte: Float? = null,
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): MoviesResponse

    @GET("company/{company_id}")
    suspend fun getCompanyDetails(
        @Path("company_id") companyId: Int,
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): Company

    @GET("person/{person_id}")
    suspend fun getPersonDetails(
        @Path("person_id") personId: Int,
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY,
        @Query("append_to_response") appendToResponse: String = "combined_credits",
        @Query("language") language: String = "en-US"
    ): PersonDetails

    // Search Screen
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("primary_release_year") primaryReleaseYear: String? = null,
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): MoviesResponse

    // Home Screen
    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("page") page: Int = 1,
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): MoviesResponse

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int = 1,
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): MoviesResponse

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("page") page: Int = 1,
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): MoviesResponse

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("page") page: Int = 1,
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): MoviesResponse

    // Details Screen
    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY,
        @Query("append_to_response") appendToResponse: String =
            "credits,videos,release_dates,recommendations,similar,images"
    ): MovieDetails

    @GET("movie/{movie_id}/watch/providers")
    suspend fun getMovieWatchProviders(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): WatchProvidersResponse
}
