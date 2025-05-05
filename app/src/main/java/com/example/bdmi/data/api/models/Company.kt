package com.example.bdmi.data.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// Company details endpoint
@JsonClass(generateAdapter = true)
data class Company(
    @Json(name = "headquarters") val headquarters: String,
    @Json(name = "homepage") val homepage: String?,
    @Json(name = "id") val id: Int,
    @Json(name = "logo_path") val logoPath: String?,
    @Json(name = "name") val name: String,
    @Json(name = "origin_country") val originCountry: String,
    @Json(name = "parent_company") val parentCompany: ParentCompany?
)

@JsonClass(generateAdapter = true)
data class ParentCompany(
    @Json(name = "name") val name: String,
    @Json(name = "id") val id: Int,
    @Json(name = "logo_path") val logoPath: String?
)