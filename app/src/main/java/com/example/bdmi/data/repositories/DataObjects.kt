package com.example.bdmi.data.repositories

import com.google.firebase.Timestamp

/* Used in Review Journey */
// For Review Repository
data class Movie(
    val reviewCount : Int = 0,
    val averageRating : Double = 0.0,
    val ratingCount : Int = 0,
    val ratingSum : Double = 0.0,
    val ratingBreakdown : Map<String, Int> = mapOf(
        ".5" to 0,
        "1" to 0,
        "1.5" to 0,
        "2" to 0,
        "2.5" to 0,
        "3" to 0,
        "3.5" to 0,
        "4" to 0,
        "4.5" to 0,
        "5" to 0
    )
)

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
    val posterPath: String,
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

// Used in Notification Journey
data class Notification(
    val notificationId: String = "",
    val type: String = "",
    val data: NotificationType = NotificationType.FriendRequest(),
    val read: Boolean = false,
    val timestamp: Timestamp = Timestamp.now()
)

sealed class NotificationType {
    data class FriendRequest(
        val userId: String = "",
        val displayName: String = "",
        val profilePicture: String = "",
        val friendCount: Long? = 0,
        val listCount: Long? = 0,
        val reviewCount: Long? = 0,
        val isPublic: Boolean? = true,
        val responded: Boolean = false
    ) : NotificationType()
    object Message : NotificationType()
    object Review : NotificationType()
}