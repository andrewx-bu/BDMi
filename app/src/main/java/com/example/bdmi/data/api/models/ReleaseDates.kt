package com.example.bdmi.data.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// Release Dates endpoint
@JsonClass(generateAdapter = true)
data class ReleaseDatesResponse(
    @Json(name = "results") val results: List<ReleaseDateGroup>
)

@JsonClass(generateAdapter = true)
data class ReleaseDateGroup(
    @Json(name = "iso_3166_1") val iso31661: String,
    @Json(name = "release_dates") val releaseDates: List<ReleaseDate>
)

@JsonClass(generateAdapter = true)
data class ReleaseDate(
    @Json(name = "certification") val certification: String,
    @Json(name = "type") val type: Int
)
