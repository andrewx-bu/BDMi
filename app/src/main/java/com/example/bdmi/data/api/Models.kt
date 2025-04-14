package com.example.bdmi.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// Discover endpoint
@JsonClass(generateAdapter = true)
data class MoviesResponse(
    @Json(name = "page") val page: Int,
    @Json(name = "results") val results: List<Movie>,
    @Json(name = "total_pages") val totalPages: Int,
    @Json(name = "total_results") val totalResults: Int
)

@JsonClass(generateAdapter = true)
data class Movie(
    @Json(name = "adult") val adult: Boolean,
    @Json(name = "backdrop_path") val backdropPath: String?,
    @Json(name = "genre_ids") val genreIds: List<Int>,
    @Json(name = "id") val id: Int,
    @Json(name = "original_language") val originalLanguage: String,
    @Json(name = "original_title") val originalTitle: String,
    @Json(name = "overview") val overview: String,
    @Json(name = "popularity") val popularity: Double,
    @Json(name = "poster_path") val posterPath: String?,
    @Json(name = "release_date") val releaseDate: String,
    @Json(name = "title") val title: String,
    @Json(name = "video") val video: Boolean,
    @Json(name = "vote_average") val voteAverage: Double,
    @Json(name = "vote_count") val voteCount: Int
)

// Details endpoint
@JsonClass(generateAdapter = true)
data class MovieDetails(
    @Json(name = "adult") val adult: Boolean,
    @Json(name = "backdrop_path") val backdropPath: String?,
    @Json(name = "budget") val budget: Int,
    @Json(name = "genres") val genres: List<Genre>,
    @Json(name = "homepage") val homepage: String?,
    @Json(name = "id") val id: Int,
    @Json(name = "origin_country") val originCountry: List<String>,
    @Json(name = "original_language") val originalLanguage: String,
    @Json(name = "original_title") val originalTitle: String,
    @Json(name = "overview") val overview: String,
    @Json(name = "popularity") val popularity: Double,
    @Json(name = "poster_path") val posterPath: String?,
    @Json(name = "release_date") val releaseDate: String,
    @Json(name = "revenue") val revenue: Long,
    @Json(name = "runtime") val runtime: Int,
    @Json(name = "status") val status: String,
    @Json(name = "tagline") val tagline: String?,
    @Json(name = "title") val title: String,
    @Json(name = "video") val video: Boolean,
    @Json(name = "vote_average") val voteAverage: Double,
    @Json(name = "vote_count") val voteCount: Int
)

@JsonClass(generateAdapter = true)
data class Genre(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String
)
