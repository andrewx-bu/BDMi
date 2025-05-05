package com.example.bdmi.data.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// Person details endpoint
@JsonClass(generateAdapter = true)
data class PersonDetails(
    @Json(name = "also_known_as") val alsoKnownAs: List<String>,
    @Json(name = "biography") val biography: String,
    @Json(name = "birthday") val birthday: String?,
    @Json(name = "deathday") val deathday: String?,
    @Json(name = "gender") val gender: Int,
    @Json(name = "homepage") val homepage: String?,
    @Json(name = "id") val id: Int,
    @Json(name = "imdb_id") val imdbId: String?,
    @Json(name = "known_for_department") val knownForDepartment: String,
    @Json(name = "name") val name: String,
    @Json(name = "place_of_birth") val placeOfBirth: String?,
    @Json(name = "popularity") val popularity: Double,
    @Json(name = "profile_path") val profilePath: String?,
    @Json(name = "combined_credits") val combinedCredits: CombinedCredits? = null
)

@JsonClass(generateAdapter = true)
data class CombinedCredits(
    @Json(name = "cast") val cast: List<Credit>,
    @Json(name = "crew") val crew: List<Credit>
)

@JsonClass(generateAdapter = true)
data class Credit(
    @Json(name = "adult") val adult: Boolean,
    @Json(name = "backdrop_path") val backdropPath: String?,
    @Json(name = "genre_ids") val genreIds: List<Int>?,
    @Json(name = "id") val id: Int,
    @Json(name = "original_language") val originalLanguage: String?,
    @Json(name = "original_title") val originalTitle: String?,
    @Json(name = "overview") val overview: String?,
    @Json(name = "popularity") val popularity: Double?,
    @Json(name = "poster_path") val posterPath: String?,
    @Json(name = "release_date") val releaseDate: String?,
    @Json(name = "first_air_date") val firstAirDate: String?,
    @Json(name = "title") val title: String?,
    @Json(name = "name") val name: String?,
    @Json(name = "video") val video: Boolean?,
    @Json(name = "vote_average") val voteAverage: Double?,
    @Json(name = "vote_count") val voteCount: Int?,
    @Json(name = "character") val character: String?,
    @Json(name = "credit_id") val creditId: String,
    @Json(name = "order") val order: Int?,
    @Json(name = "department") val department: String?,
    @Json(name = "job") val job: String?,
    @Json(name = "media_type") val mediaType: String
)