package com.example.bdmi.screens

import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import com.example.bdmi.api.APIService
import com.example.bdmi.api.MoviesResponse

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