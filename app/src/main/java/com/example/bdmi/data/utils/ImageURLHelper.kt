package com.example.bdmi.data.utils

object ImageURLHelper {
    private const val BASE_URL = "https://image.tmdb.org/t/p/"

    fun getBackdropURL(path: String?, width: Int = 780): String {
        return if (path.isNullOrEmpty()) {
            "" // Add placeholder image URL
        } else {
            "${BASE_URL}w${width}$path"
        }
    }

    // Width defaulted for posters
    fun getURL(path: String?, width: Int = 500): String {
        return if (path.isNullOrEmpty()) {
            ""
        } else {
            "${BASE_URL}w${width}$path"
        }
    }
}