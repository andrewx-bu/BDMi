package com.example.bdmi.ui.friends

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bdmi.data.repositories.FriendRepository
import com.example.bdmi.data.repositories.UserRepository
import com.example.bdmi.ui.viewmodels.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FriendInfo(
    val userId: String = "", // Provide a default value
    val displayName: String = "",
    val profilePicture: String = "",
    val friendCount: Long = 0,
    val listCount: Long = 0,
    val reviewCount: Long = 0,
    val isPublic: Boolean = true
)

private const val TAG = "FriendViewModel"

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
    private val userRepository: UserRepository
): ViewModel() {
    // List of friends for the current user
    private val _friends = MutableStateFlow<MutableList<FriendInfo>>(mutableListOf())
    val friends: StateFlow<MutableList<FriendInfo>?> = _friends.asStateFlow()

    // Current Profile Visiting. Takes in a UserInfo object retrieved from Profile Collection
    private val _friendProfile = MutableStateFlow<UserInfo?>(null)
    val friendProfile: StateFlow<UserInfo?> = _friendProfile.asStateFlow()

    // Load initial friends list. Done onLogin and onRegister (maybe)
    fun loadFriends(userId: String) {
        Log.d(TAG, "Loading friends for user: $userId")

        viewModelScope.launch {
            friendRepository.getFriends(userId) { friendsList ->
                _friends.value = friendsList as MutableList<FriendInfo>
            }
        }
    }

    // Add friend journey
    // Step 1: Search for users by display name
    fun searchUsers(displayName: String, onComplete: () -> Unit) {
        Log.d(TAG, "Searching for users with display name: $displayName")

        viewModelScope.launch {
            friendRepository.searchUsers(displayName) { users ->
                onComplete()
            }
        }
    }

    // Step 2: Send friend invite
    fun sendFriendInvite(userId: String, friendId: String, onComplete: (Boolean) -> Unit) {

    }

    // Step 3: Accept or decline friend invite
    fun acceptInvite(userId: String, friendId: String, onComplete: (Boolean) -> Unit) {
        Log.d(TAG, "Adding friend with ID: $friendId")

        viewModelScope.launch {
            friendRepository.acceptFriendInvite(userId, friendId) { newFriend ->
                if (newFriend != null) {
                    _friends.value = _friends.value.toMutableList().apply {
                        add(newFriend)
                    }
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

    fun removeFriend(userId: String, friendId: String, onComplete: (Boolean) -> Unit) {
        Log.d(TAG, "Removing friend with ID: $friendId")

        viewModelScope.launch {
            friendRepository.removeFriend(userId, friendId) {
                if (it) {
                    // Remove friend from list
                    _friends.value = _friends.value.toMutableList().apply {
                        removeIf { it.userId == friendId }
                    }
                }
            }
            onComplete(true)
        }
    }

    fun loadFriendProfile(userId: String) {
        Log.d(TAG, "Loading friend profile for user: $userId")

        viewModelScope.launch {
            userRepository.loadUser(userId) { friendProfile ->
                _friendProfile.value = friendProfile
            }
        }
    }

    fun closeFriendProfile() {
        Log.d(TAG, "Closing friend profile")
        _friendProfile.value = null
    }
}