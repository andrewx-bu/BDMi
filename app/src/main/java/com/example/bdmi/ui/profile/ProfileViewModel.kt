package com.example.bdmi.ui.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bdmi.data.repositories.FriendRepository
import com.example.bdmi.data.repositories.FriendStatus
import com.example.bdmi.data.repositories.ProfileRepository
import com.example.bdmi.data.repositories.UserInfo
import com.example.bdmi.data.repositories.UserReview
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ProfileViewModel"

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val friendRepository: FriendRepository,
) : ViewModel() {
    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo: StateFlow<UserInfo?> = _userInfo.asStateFlow()

    private val _friendState = MutableStateFlow<FriendStatus>(FriendStatus.NOT_FRIENDS)
    val friendState: StateFlow<FriendStatus> = _friendState.asStateFlow()


    private val _tempImageURI = MutableStateFlow<Uri?>(null)
    val tempImageURI: StateFlow<Uri?> = _tempImageURI.asStateFlow()

    private val _reviewCarousel = MutableStateFlow<List<UserReview>>(emptyList())
    val reviewCarousel: StateFlow<List<UserReview>> = _reviewCarousel.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun setUserInfo(userInfo: UserInfo) {
        _userInfo.value = userInfo
    }

    fun loadProfile(profileId: String, onComplete: (Boolean) -> Unit) {
        Log.d(TAG, "Loading user profile for user: $profileId")
        _isLoading.value = true
        viewModelScope.launch {
            profileRepository.loadUser(profileId) { userProfile ->
                _userInfo.value = userProfile
                _isLoading.value = false
                onComplete(true)
            }
        }
    }

    fun getFriendStatus(currentUserId: String, friendId: String) {
        Log.d(TAG, "Getting friend status for user: $currentUserId and friend: $friendId")
        _isLoading.value = true
        viewModelScope.launch {
            friendRepository.getFriendStatus(currentUserId, friendId) { friendStatus ->
                _friendState.value = friendStatus
                _isLoading.value = false
                Log.d(TAG, "Friend status: $friendStatus")
            }
        }
    }

    // Takes in the userId of the person you want to send the invite to and the userInfo of the person who sent the invite
    fun sendFriendInvite(
        senderInfo: UserInfo,
        recipientId: String,
    ) {
        Log.d(TAG, "Sending friend invite to user with ID: $recipientId")
        viewModelScope.launch {
            friendRepository.sendFriendInvite(senderInfo, recipientId) {
                _friendState.value = FriendStatus.PENDING
                Log.d(TAG, "Friend invite sent")
            }
        }
    }

    fun removeFriend(
        userId: String,
        friendId: String
    ) {
        Log.d(TAG, "Removing friend with ID: $friendId")

        viewModelScope.launch {
            friendRepository.removeFriend(userId, friendId) { it ->
                if (it) {
                    // Remove friend from list
                    Log.d(TAG, "Friend removed")
                }
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

    fun changeProfilePicture(userId: String, profilePicture: Uri) {
        _tempImageURI.value = profilePicture

        Log.d("UserViewModel", "Changing profile picture for user with ID: $userId")
        _userInfo.value = _userInfo.value?.copy(profilePicture = profilePicture.toString())
        viewModelScope.launch {
            profileRepository.changeProfilePicture(userId, profilePicture) { newProfilePicture ->
                if (newProfilePicture != null) {
                    val updatedUserInfo = _userInfo.value?.copy(profilePicture = newProfilePicture)
                    _userInfo.value = updatedUserInfo
                    _tempImageURI.value = null
                    Log.d("UserViewModel", "Updated user info: ${_userInfo.value}")
                }
                else {
                    Log.e("UserViewModel", "Failed to update profile picture")
                }
            }
        }
    }

    fun reviewCarousel() {
        Log.d("ProfileViewModel", "Getting reviews for user with ID: ${_userInfo.value?.userId}")
        _isLoading.value = true
        viewModelScope.launch {
            profileRepository.getReviews(
                userId = _userInfo.value?.userId ?: "",
                pageSize = 5,
                onComplete = { reviews, _ ->
                    _reviewCarousel.value = reviews
                    _isLoading.value = false
                }
            )
        }
    }
}