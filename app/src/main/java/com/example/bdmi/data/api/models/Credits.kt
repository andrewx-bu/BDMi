package com.example.bdmi.data.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// Credits endpoint
@JsonClass(generateAdapter = true)
data class MovieCreditsResponse(
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