package com.example.bdmi.ui.notifications

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bdmi.data.repositories.FriendRepository
import com.example.bdmi.data.repositories.Notification
import com.example.bdmi.data.repositories.NotificationRepository
import com.example.bdmi.data.repositories.NotificationType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "NotificationViewModel"

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val friendRepository: FriendRepository
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
        _notificationList.value = _notificationList.value.map { notification ->
            if (notification.notificationId == notificationId) {
                notification.copy(read = true)
            } else {
                notification
            }
        } as MutableList<Notification>

        viewModelScope.launch {
            notificationRepository.readNotification(userId, notificationId) {
                if (it) {
                    Log.d(TAG, "Notification marked as read: $notificationId")
                }
            }
        }
    }

    fun deleteNotification(userId: String, notificationId: String) {
        Log.d(TAG, "Deleting notification: $notificationId")
        _notificationList.value = _notificationList.value.filter { notification ->
            notification.notificationId != notificationId
        } as MutableList<Notification>
        viewModelScope.launch {
            notificationRepository.deleteNotification(userId, notificationId) {
                if (it) {
                    // Remove the notification from the list
                    Log.d(TAG, "Notification deleted: $notificationId")
                }
            }
        }
    }

    fun deleteAllNotifications(userId: String) {
        Log.d(TAG, "Deleting all notifications for user: $userId")
        _notificationList.value = mutableListOf()
        viewModelScope.launch {
            notificationRepository.deleteAllNotifications(userId) {
                if (it) {
                    // Clear the notification list
                    Log.d(TAG, "All notifications deleted")
                }
            }
        }
    }

    fun acceptInvite(userId: String, friendId: String, onComplete: (Boolean) -> Unit) {
        Log.d(TAG, "Adding friend with ID: $friendId")

        viewModelScope.launch {
            friendRepository.acceptFriendInvite(userId, friendId) { newFriend ->
                if (newFriend != null) {
                    Log.d(TAG, "New friend added: ${newFriend.displayName}")
                    onComplete(true)
                } else {
                    onComplete(false)
                }
            }
        }
    }

    fun declineInvite(userId: String, friendId: String, onComplete: (Boolean) -> Unit) {
        Log.d(TAG, "Declining friend invite with ID: $friendId")

        viewModelScope.launch {
            friendRepository.declineInvite(userId, friendId) {
                onComplete(it)
            }
        }
    }

    // Responding to a friend request notification and changes responded value
    fun friendRequestResponse(userId: String, notificationId: String) {
        Log.d(TAG, "Responding to friend request")
        _notificationList.value = _notificationList.value.map { notification ->
            val friendRequest = notification.data as? NotificationType.FriendRequest
            if (friendRequest != null) {
                notification.copy(data = friendRequest.copy(responded = true))
            } else {
                notification
            }
        } as MutableList<Notification>

        viewModelScope.launch {
            notificationRepository.respondFriendRequest(userId, notificationId) {
                if(it)
                    Log.d(TAG, "Friend request responded to")
            }
        }
    }

}