package com.example.bdmi.data.api

import com.example.bdmi.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {
    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("page") page: Int = 1,
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): MoviesResponse
}
