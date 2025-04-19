package com.example.bdmi.ui.notifications

import com.google.firebase.Timestamp

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