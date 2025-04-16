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
    @Json(name = "backdrop_path") val backdropPath: String?,
    @Json(name = "budget") val budget: Int,
    @Json(name = "genres") val genres: List<Genre>,
    @Json(name = "homepage") val homepage: String?,
    @Json(name = "id") val id: Int,
    @Json(name = "original_language") val originalLanguage: String,
    @Json(name = "original_title") val originalTitle: String,
    @Json(name = "overview") val overview: String,
    @Json(name = "popularity") val popularity: Double,
    @Json(name = "poster_path") val posterPath: String?,
    @Json(name = "production_companies") val productionCompanies: List<ProductionCompany>,
    @Json(name = "production_countries") val productionCountries: List<ProductionCountry>,
    @Json(name = "release_date") val releaseDate: String,
    @Json(name = "revenue") val revenue: Long,
    @Json(name = "runtime") val runtime: Int,
    @Json(name = "spoken_languages") val spokenLanguages: List<SpokenLanguage>,
    @Json(name = "status") val status: String?,
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

@JsonClass(generateAdapter = true)
data class ProductionCompany(
    @Json(name = "id") val id: Int,
    @Json(name = "logo_path") val logoPath: String?,
    @Json(name = "name") val name: String,
)

@JsonClass(generateAdapter = true)
data class ProductionCountry(
    @Json(name = "name") val name: String
)

@JsonClass(generateAdapter = true)
data class SpokenLanguage(
    @Json(name = "english_name") val englishName: String,
    @Json(name = "name") val name: String
)

// Credits endpoint
@JsonClass(generateAdapter = true)
data class MovieCreditsResponse(
    @Json(name = "id") val movieId: Int,
    @Json(name = "cast") val cast: List<CastMember>,
    @Json(name = "crew") val crew: List<CrewMember>
)

@JsonClass(generateAdapter = true)
data class CastMember(
    @Json(name = "id") val id: Int,
    @Json(name = "known_for_department") val knownForDepartment: String?,
    @Json(name = "name") val name: String,
    @Json(name = "original_name") val originalName: String,
    @Json(name = "popularity") val popularity: Double,
    @Json(name = "profile_path") val profilePath: String?,
    @Json(name = "cast_id") val castId: Int,
    @Json(name = "character") val character: String,
    @Json(name = "credit_id") val creditId: String,
    @Json(name = "order") val order: Int
)

@JsonClass(generateAdapter = true)
data class CrewMember(
    @Json(name = "id") val id: Int,
    @Json(name = "known_for_department") val knownForDepartment: String?,
    @Json(name = "name") val name: String,
    @Json(name = "original_name") val originalName: String,
    @Json(name = "popularity") val popularity: Double,
    @Json(name = "profile_path") val profilePath: String?,
    @Json(name = "credit_id") val creditId: String,
    @Json(name = "department") val department: String,
    @Json(name = "job") val job: String
)