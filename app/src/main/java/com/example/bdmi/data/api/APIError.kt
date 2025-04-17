package com.example.bdmi.data.api

import retrofit2.HttpException
import java.io.IOException

sealed class APIError {
    abstract val message: String

    data class NetworkError(override val message: String) : APIError() {
        override fun toString() = message
    }

    data class ServerError(val code: Int, override val message: String) : APIError() {
        override fun toString() = message
    }

    data class GenericError(override val message: String) : APIError() {
        override fun toString() = message
    }
}

fun Throwable.toAPIError(): APIError {
    return when (this) {
        is IOException -> APIError.NetworkError("Network error. Please check your internet connection.")
        is HttpException -> {
            val errorMessage = when (this.code()) {
                404 -> "Content not found"
                500 -> "Server error. Please try again later."
                else -> "Server error (${this.code()})"
            }
            APIError.ServerError(this.code(), errorMessage)
        }
        else -> APIError.GenericError("An unexpected error occurred")
    }
}
