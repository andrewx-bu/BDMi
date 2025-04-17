package com.example.bdmi.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bdmi.data.repositories.FriendRepository
import com.example.bdmi.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileBanner(
    val userId: String = "", // Provide a default value
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

private const val TAG = "FriendViewModel"

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
    private val userRepository: UserRepository
): ViewModel() {
    // List of friends for the current user
    private val _friends = MutableStateFlow<MutableList<ProfileBanner>>(mutableListOf())
    val friends: StateFlow<MutableList<ProfileBanner>?> = _friends.asStateFlow()

    // Current Profile Visiting. Takes in a UserInfo object retrieved from Profile Collection
    private val _friendProfile = MutableStateFlow<UserInfo?>(null)
    val friendProfile: StateFlow<UserInfo?> = _friendProfile.asStateFlow()

    private val _friendState = MutableStateFlow<FriendStatus?>(null)
    val friendState: StateFlow<FriendStatus?> = _friendState.asStateFlow()

    private val _searchResults = MutableStateFlow<List<ProfileBanner>>(emptyList())
    val searchResults: StateFlow<List<ProfileBanner>> = _searchResults.asStateFlow()

    // Load initial friends list. Done onLogin and onRegister (maybe)
    fun loadFriends(userId: String) {
        Log.d(TAG, "Loading friends for user: $userId")

        viewModelScope.launch {
            friendRepository.getFriends(userId) { friendsList ->
                _friends.value = friendsList as MutableList<ProfileBanner>
            }
        }
    }

    // Add friend journey
    // Step 1: Search for users by display name
    fun searchUsers(displayName: String, onComplete: (List<ProfileBanner>) -> Unit) {
        Log.d(TAG, "Searching for users with display name: $displayName")

        viewModelScope.launch {
            friendRepository.searchUsers(displayName) { users ->
                onComplete(users)
            }
        }
    }

    // Step 2: Send friend invite
    // Takes in the userId of the person you want to send the invite to and the userInfo of the person who sent the invite
    fun sendFriendInvite(senderInfo: ProfileBanner, recipientId: String, onComplete: (Boolean) -> Unit) {
        Log.d(TAG, "Sending friend invite to user with ID: $recipientId")
        viewModelScope.launch {
            friendRepository.sendFriendInvite(senderInfo, recipientId) {
                _friendState.value = FriendStatus.PENDING
                onComplete(it)
            }
        }
    }

    // Step 3: Accept or decline friend invite
    /* Moved to Notification View Model */

    fun removeFriend(userId: String, friendId: String, onComplete: (Boolean) -> Unit) {
        Log.d(TAG, "Removing friend with ID: $friendId")

        viewModelScope.launch {
            friendRepository.removeFriend(userId, friendId) {
                if (it) {
                    // Remove friend from list
                    _friends.value = _friends.value.toMutableList().apply {
                        removeIf { it.userId == friendId }
                        _friendState.value = FriendStatus.NOT_FRIENDS
                    }
                }
            }
            onComplete(true)
        }
    }

    fun loadProfile(profileId: String, onComplete: (UserInfo?) -> Unit) {
        Log.d(TAG, "Loading friend profile for user: $profileId")

        viewModelScope.launch {
            userRepository.loadUser(profileId) { userProfile ->
                _friendProfile.value = userProfile
                onComplete(userProfile)
            }
        }
    }

    fun getFriendStatus(currentUserId: String, friendId: String) {
        Log.d(TAG, "Getting friend status for user: $currentUserId and friend: $friendId")

        viewModelScope.launch {
            friendRepository.getFriendStatus(currentUserId, friendId) { friendStatus ->
                _friendState.value = friendStatus
                Log.d(TAG, "Friend status: $friendStatus")
            }
        }
    }

    fun cancelFriendRequest(userId: String, friendId: String) {
        Log.d(TAG, "Cancelling friend request for user: $userId and friend: $friendId")
        viewModelScope.launch {
            friendRepository.cancelFriendRequest(userId, friendId) {
                if (it) {
                    Log.d(TAG, "Friend request cancelled")
                    _friendState.value = FriendStatus.NOT_FRIENDS
                } else {
                    Log.d(TAG, "Failed to cancel friend request")
                }
            }
        }
    }
}