package com.example.bdmi.data.repositories

import com.google.firebase.Timestamp

data class UserInfo(
    val userId: String = "", // Provide a default value
    val displayName: String? = "",
    val profilePicture: String? = "https://res.cloudinary.com/dle98umos/image/upload/v1744005666/default_hm4pfx.jpg",
    val friendCount: Long? = 0,
    val listCount: Long? = 0,
    val reviewCount: Long? = 0,
    val isPublic: Boolean? = true,
)

/* Used in Review Journey */
// For Review Repository
data class MovieMetrics(
    val reviewCount : Int = 0,
    val averageRating : Double = 0.0,
    val ratingCount : Int = 0,
    val ratingSum : Double = 0.0,
    val ratingBreakdown : Map<String, Int> = mapOf(
        "0.5" to 0,
        "1.0" to 0,
        "1.5" to 0,
        "2.0" to 0,
        "2.5" to 0,
        "3.0" to 0,
        "3.5" to 0,
        "4.0" to 0,
        "4.5" to 0,
        "5.0" to 0
    )
)

// Base review class
open class Review(
    val reviewTitle: String,
    val reviewText: String,
    val rating: Float,
    val spoiler: Boolean = false,
    val timestamp: Timestamp = Timestamp.now()
)

// User-specific review class
class UserReview(
    val movieId: Int = -1,
    val movieTitle: String = "",
    val posterPath: String = "",
    reviewTitle: String = "",
    reviewText: String = "",
    rating: Float = 0f,
    spoiler: Boolean = false,
    timestamp: Timestamp = Timestamp.now()
) : Review(reviewTitle, reviewText, rating, spoiler, timestamp)

// Movie-specific review class
class MovieReview(
    val userId: String = "",
    val displayName: String = "",
    val userProfilePicture: String = "",
    reviewTitle: String = "",
    reviewText: String = "",
    rating: Float = 0f,
    spoiler: Boolean = false,
    timestamp: Timestamp = Timestamp.now(),
) : Review(reviewTitle, reviewText, rating, spoiler, timestamp)

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
    object Review0 : NotificationType()
}

data class ProfileBanner(
    val userId: String = "",
    val displayName: String = "",
    val profilePicture: String = "",
    val friendCount: Long? = 0,
    val listCount: Long? = 0,
    val reviewCount: Long? = 0,
    val isPublic: Boolean? = true
)

enum class FriendStatus {
    NOT_FRIENDS,
    PENDING,
    FRIEND
}