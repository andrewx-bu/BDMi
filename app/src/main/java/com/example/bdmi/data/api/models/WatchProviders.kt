package com.example.bdmi.data.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// Watch providers endpoint
@JsonClass(generateAdapter = true)
data class WatchProvidersResponse(
    @Json(name = "id") val id: Int,
    @Json(name = "results") val results: WatchProvidersResults
)

@JsonClass(generateAdapter = true)
data class WatchProvidersResults(
    @Json(name = "US") val us: WatchProviderCountry? = null
)

@JsonClass(generateAdapter = true)
data class WatchProviderCountry(
    @Json(name = "rent") val rent: List<Provider> = emptyList(),
    @Json(name = "buy") val buy: List<Provider> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class Provider(
    @Json(name = "provider_id") val providerId: Int,
    @Json(name = "provider_name") val providerName: String,
    @Json(name = "logo_path") val logoPath: String,
)

