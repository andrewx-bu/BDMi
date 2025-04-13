package com.example.bdmi.data.repositories

import android.util.Log
import com.example.bdmi.ui.notifications.Notification
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
                    val notificationInfo = notificationDoc.toObject(Notification::class.java)
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
}