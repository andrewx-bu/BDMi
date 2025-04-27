package com.example.bdmi.ui.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bdmi.data.repositories.ProfileRepository
import com.example.bdmi.data.repositories.UserInfo
import com.example.bdmi.data.repositories.UserReview
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
) : ViewModel() {
    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo: StateFlow<UserInfo?> = _userInfo.asStateFlow()

    private val _tempImageURI = MutableStateFlow<Uri?>(null)
    val tempImageURI: StateFlow<Uri?> = _tempImageURI.asStateFlow()

    private val _reviewCarousel = MutableStateFlow<List<UserReview>>(emptyList())
    val reviewCarousel: StateFlow<List<UserReview>> = _reviewCarousel.asStateFlow()

    fun setUserInfo(userInfo: UserInfo, onComplete: (Boolean) -> Unit) {
        _userInfo.value = userInfo
        onComplete(true)
    }

    fun changeProfilePicture(userId: String, profilePicture: Uri, onComplete: (Boolean) -> Unit) {
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
                    onComplete(true)
                }
                else {
                    Log.e("UserViewModel", "Failed to update profile picture")
                    onComplete(false)
                }
            }
        }
    }

    fun reviewCarousel() {
        Log.d("ProfileViewModel", "Getting reviews for user with ID: ${_userInfo.value?.userId}")
        viewModelScope.launch {
            profileRepository.getReviews(
                userId = _userInfo.value?.userId ?: "",
                pageSize = 5,
                onComplete = { reviews, _ ->
                    _reviewCarousel.value = reviews
                }
            )
        }
    }
}