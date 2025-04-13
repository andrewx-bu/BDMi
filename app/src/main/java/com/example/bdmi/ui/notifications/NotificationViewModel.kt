package com.example.bdmi.ui.notifications

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bdmi.data.repositories.NotificationRepository
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class Notification(
    val notificationId: String = "",
    val type: String = "",
    val data: Map<String, Any> = emptyMap(),
    val read: Boolean = false,
    val timestamp: Timestamp = Timestamp.now()
)

private const val TAG = "NotificationViewModel"

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
) : ViewModel() {
    private val _notificationList = MutableStateFlow<MutableList<Notification>>(mutableListOf())
    val notificationList: StateFlow<MutableList<Notification>> = _notificationList.asStateFlow()

    private val _numOfNotifications = MutableStateFlow(0)
    var numOfNotifications: StateFlow<Int> = _numOfNotifications.asStateFlow()

    fun getNotifications(userId: String) {
        Log.d(TAG, "Loading notifications for user: $userId")

        viewModelScope.launch {
            notificationRepository.getNotifications(userId) { notifications ->
                _notificationList.value = notifications as MutableList<Notification>
                for (notification in notifications) {
                    if (!notification.read) {
                        _numOfNotifications.value++
                    }
                }
            }
        }
    }

    fun readNotification(userId: String, notificationId: String) {
        Log.d(TAG, "Marking notification as read: $notificationId")

        viewModelScope.launch {
            notificationRepository.readNotification(userId, notificationId) {
                if (it) {
                    // Update the notification list to reflect the read status
                    _notificationList.value = _notificationList.value.map { notification ->
                        if (notification.notificationId == notificationId) {
                            notification.copy(read = true)
                            _numOfNotifications.value--
                        } else {
                            notification
                        }
                    } as MutableList<Notification>
                }
            }
        }
    }

    fun deleteNotification(userId: String, notificationId: String) {
        Log.d(TAG, "Deleting notification: $notificationId")

        viewModelScope.launch {
            notificationRepository.deleteNotification(userId, notificationId) {
                if (it) {
                    // Remove the notification from the list
                    _notificationList.value = _notificationList.value.filter { notification ->
                        notification.notificationId != notificationId
                    } as MutableList<Notification>
                }
            }
        }
    }

    fun deleteAllNotifications(userId: String) {
        Log.d(TAG, "Deleting all notifications for user: $userId")

        viewModelScope.launch {
            notificationRepository.deleteAllNotifications(userId) {
                if (it) {
                    // Clear the notification list
                    _notificationList.value = mutableListOf()
                }
            }
        }
    }
}