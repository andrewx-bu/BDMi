package com.example.bdmi.data.repositories

import com.google.firebase.Timestamp

/* Used in Review Journey */
// For Review Repository
data class Review(
    val userId: String,
    val userName: String,
    val userProfilePicture: String,
    val reviewTitle: String,
    val reviewText: String,
    val rating: Float,
    val spoiler: Boolean = false,
    val timestamp: Timestamp = Timestamp.now()
)

// For User Repository
data class UserReview(
    val movieId: Int,
    val movieTitle: String,
    val userProfilePicture: String,
    val reviewTitle: String,
    val reviewText: String,
    val rating: Float,
    val spoiler: Boolean = false,
    val timestamp: Timestamp = Timestamp.now()
)

// Used in Watchlist Journey
data class CustomList(
    val listId: String = "",
    val name: String = "",
    val description: String = "",
    val numOfItems: Int = 0,
    val timestamp: Timestamp = Timestamp.now(),
    val isPublic: Boolean = true
)

data class MediaItem(
    val id: Int = 0,
    val title: String = "",
    val posterPath: String = "",
    val releaseDate: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val isWatched: Boolean = false
)