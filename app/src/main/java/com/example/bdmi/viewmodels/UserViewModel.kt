package com.example.bdmi.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.bdmi.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class UserInfo(
    val userId: String = "", // Provide a default value
    val displayName: String? = null, // Default to null for nullable types
    val profilePicture: String? = null,
    val friendCount: Long? = null,
    val listCount: Long? = null,
    val reviewCount: Long? = null,
    val isPublic: Boolean? = null,
    // Add other fields later
)

@HiltViewModel
class UserViewModel @Inject constructor(private val userRepo: UserRepository) : ViewModel() {
    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo: StateFlow<UserInfo?> = _userInfo.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()


    // Collection of functions from the UserRepository
    fun loadUser(
        userId: String,
        onComplete: (UserInfo?) -> Unit
    ) {
        userRepo.loadUser(userId) { loadedUserInfo ->
            _userInfo.value = loadedUserInfo
            _isLoggedIn.value = loadedUserInfo != null
            Log.d("UserViewModel", "User loaded: ${_userInfo.value}")
            onComplete(loadedUserInfo)
        }
    }

    fun login(
        loginInformation: HashMap<String, String>,
        onComplete: (UserInfo?) -> Unit
    ) {
        userRepo.authenticateUser(loginInformation) { loadedUserInfo ->
            _userInfo.value = loadedUserInfo
            onComplete(loadedUserInfo)
        }
    }

    fun logout() {
        _isLoggedIn.value = false
        _userInfo.value = null // Clear user info on logout
    }

    fun register(
        userInformation: HashMap<String, Any>,
        onComplete: (UserInfo?) -> Unit
    ) {
        userRepo.createUser(userInformation) { loadedUserInfo ->
            _userInfo.value = loadedUserInfo
            _isLoggedIn.value = loadedUserInfo != null
            onComplete(loadedUserInfo)
        }
    }

    fun updateUserInfo(userInfo: HashMap<String, Any>, onComplete: (Boolean) -> Unit) {
        userRepo.updateUserInfo(userInfo, onComplete)
    }

    fun changeProfilePicture(userId: String, profilePicture: Uri, onComplete: (Boolean) -> Unit) {
        userRepo.changeProfilePicture(userId, profilePicture, onComplete)
    }

    fun deleteUser(userId: String, onComplete: (Boolean) -> Unit) {
        userRepo.deleteUser(userId, onComplete)
    }

    fun addFriend(userId: String, friendId: String, onComplete: (Boolean) -> Unit) {
        userRepo.addFriend(userId, friendId, onComplete)
    }

    fun removeFriend(userId: String, friendId: String, onComplete: (Boolean) -> Unit) {
        userRepo.removeFriend(userId, friendId, onComplete)
    }
}