package com.example.bdmi.data.utils

object ImageURLHelper {
    private const val BASE_BACKDROP_IMAGE_URL = "https://image.tmdb.org/t/p/"
    private const val BASE_POSTER_IMAGE_URL = "https://image.tmdb.org/t/p/"

    fun getBackdropURL(path: String?, width: Int = 780): String {
        return if (path.isNullOrEmpty()) {
            "" // Add placeholder image URL
        } else {
            "${BASE_BACKDROP_IMAGE_URL}w${width}$path"
        }
    }

    fun getPosterURL(path: String?, width: Int = 500): String {
        return if (path.isNullOrEmpty()) {
            ""
        } else {
            "${BASE_POSTER_IMAGE_URL}w${width}$path"
        }
    }
}