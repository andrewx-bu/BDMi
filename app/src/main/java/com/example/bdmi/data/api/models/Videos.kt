package com.example.bdmi.data.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// Videos endpoint
@JsonClass(generateAdapter = true)
data class VideosResponse(
    @Json(name = "results") val results: List<Video>
)

@JsonClass(generateAdapter = true)
data class Video(
    @Json(name = "key") val key: String,
    @Json(name = "site") val site: String,
    @Json(name = "type") val type: String,
    @Json(name = "official") val official: Boolean,
    @Json(name = "published_at") val publishedAt: String,
)
