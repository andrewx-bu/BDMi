package com.example.bdmi.data.repositories

import android.util.Log
import com.example.bdmi.ui.notifications.Notification
import com.example.bdmi.ui.notifications.NotificationType
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import javax.inject.Inject

// Constants
private const val TAG = "NotificationRepository"
private const val USERS_COLLECTION = "users"
private const val NOTIFICATIONS_SUBCOLLECTION = "notifications"

class NotificationRepository @Inject constructor(
    private val db: FirebaseFirestore
) {
    // Collects all notifications for a user
    fun getNotifications(userId: String, onComplete: (List<Notification>) -> Unit) {
        val dbFunction = "getNotifications"
        val notificationList = mutableListOf<Notification>()
        db.collection(USERS_COLLECTION).document(userId).collection(NOTIFICATIONS_SUBCOLLECTION)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { notifications: QuerySnapshot ->
                for (notificationDoc in notifications) {
                    val notificationInfo = docToNotification(notificationDoc)
                    notificationList.add(notificationInfo)
                }
                Log.d("$TAG$dbFunction", "Number of notifications found: ${notificationList.size}")
                onComplete(notificationList)
            }
            .addOnFailureListener { e ->
                Log.e("$TAG$dbFunction", "Error getting notifications", e)
            }
    }

    // Changes the read status of a notification to true
    fun readNotification(userId: String, notificationId: String, onComplete: (Boolean) -> Unit) {
        val dbFunction = "readNotification"
        db.collection(USERS_COLLECTION).document(userId).collection(NOTIFICATIONS_SUBCOLLECTION)
            .document(notificationId)
            .update("isRead", true)
            .addOnSuccessListener {
                Log.d("$TAG$dbFunction", "Notification read status updated successfully")
                onComplete(true)
            }
    }

    // Deletes a notification
    fun deleteNotification(userId: String, notificationId: String, onComplete: (Boolean) -> Unit) {
        val dbFunction = "deleteNotification"
        db.collection(USERS_COLLECTION).document(userId).collection(NOTIFICATIONS_SUBCOLLECTION)
            .document(notificationId)
            .delete()
            .addOnSuccessListener {
                Log.d("$TAG$dbFunction", "Notification deleted successfully")
                onComplete(true)
            }
    }

    // Deletes all notifications
    fun deleteAllNotifications(userId: String, onComplete: (Boolean) -> Unit) {
        val dbFunction = "deleteAllNotifications"
        db.collection(USERS_COLLECTION).document(userId).collection(NOTIFICATIONS_SUBCOLLECTION)
            .get()
            .addOnSuccessListener { notifications: QuerySnapshot ->
                for (notificationDoc in notifications) {
                    notificationDoc.reference.delete()
                }
                Log.d("$TAG$dbFunction", "All notifications deleted successfully")
                onComplete(true)
            }
    }

    private fun docToNotification(doc: DocumentSnapshot): Notification {
        val type = doc.getString("type") ?: ""
        Log.d("NotificationRepository", "Notification data: ${doc.get("data")}")
        val dataMap = doc.get("data") as? Map<*, *>

        val data = when (type) {
            "friend_request" -> {
                NotificationType.FriendRequest(
                    userId = dataMap?.get("userId") as? String ?: "",
                    displayName = dataMap?.get("displayName") as? String ?: "",
                    profilePicture = dataMap?.get("profilePicture") as? String ?: "",
                    friendCount = (dataMap?.get("friendCount") as? Long)?: 0,
                    listCount = (dataMap?.get("listCount") as? Long)?: 0,
                    reviewCount = (dataMap?.get("reviewCount") as? Long)?: 0,
                    isPublic = dataMap?.get("isPublic") as? Boolean == true
                )
            }
            else -> NotificationType.FriendRequest() // Add other types later
        }
        Log.d("NotificationRepository", "New Notification data: $data")

        return Notification(
            notificationId = doc.id,
            type = type,
            data = data,
            read = doc.getBoolean("isRead") == true,
            timestamp = doc.getTimestamp("timestamp") ?: Timestamp.now()
        )
    }
}