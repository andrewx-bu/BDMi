package com.example.bdmi.data.repositories

import com.example.bdmi.data.api.APIService
import com.example.bdmi.data.api.MoviesResponse
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val apiService: APIService,
    private val db: FirebaseFirestore
) {
    suspend fun discoverMovies(page: Int): MoviesResponse {
        try {
            return apiService.discoverMovies(page = page)
        } catch (e: Exception) {
            throw e
        }
    }


}