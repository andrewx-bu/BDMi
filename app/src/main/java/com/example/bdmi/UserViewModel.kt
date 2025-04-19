package com.example.bdmi

import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.bdmi.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.core.content.edit

data class UserInfo(
    val userId: String = "", // Provide a default value
    val displayName: String? = null,
    val profilePicture: String? = null,
    val friendCount: Long? = null,
    val listCount: Long? = null,
    val reviewCount: Long? = null,
    val isPublic: Boolean? = null,
    // Add other fields later
)

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepo: UserRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo: StateFlow<UserInfo?> = _userInfo.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized

    private val _tempImageURI = MutableStateFlow<Uri?>(null)
    val tempImageURI: StateFlow<Uri?> = _tempImageURI.asStateFlow()

    // Collection of functions from the UserRepository
    fun loadUser(
        userId: String?,
        onComplete: (UserInfo?) -> Unit
    ) {
        Log.d("UserViewModel", "Loading user with ID: $userId")
        if (userId == null) {
            _isInitialized.value = true
            return
        }

        viewModelScope.launch {
            userRepo.loadUser(userId) { loadedUserInfo ->
                _userInfo.value = loadedUserInfo
                _isLoggedIn.value = loadedUserInfo != null
                _isInitialized.value = true
                Log.d("UserViewModel", "User loaded: ${_userInfo.value}")
                onComplete(_userInfo.value)
            }
        }
    }

    fun login(
        loginInformation: HashMap<String, String>,
        onComplete: (UserInfo?) -> Unit
    ) {
        Log.d("UserViewModel", "Logging in with email: ${loginInformation["email"]}")

        viewModelScope.launch {
            userRepo.authenticateUser(loginInformation) { userId ->
                if (userId != null) {
                    loadUser(userId) { loadedUserInfo ->
                        _userInfo.value = loadedUserInfo
                        _isLoggedIn.value = loadedUserInfo != null
                        sharedPreferences.edit { putString("userId", userId) }
                        Log.d("UserViewModel", "User logged in: ${_userInfo.value}")
                        onComplete(loadedUserInfo)
                    }
                }
            }
        }

    }

    fun logout() {
        _isLoggedIn.value = false
        _userInfo.value = null // Clear user info on logout
        sharedPreferences.edit { remove("userId") }
    }

    fun register(
        userInformation: HashMap<String, Any>,
        onComplete: (UserInfo?) -> Unit
    ) {
        Log.d("UserViewModel", "Registering user with email: ${userInformation["email"]}")

        viewModelScope.launch {
            userRepo.createUser(userInformation) { loadedUserInfo ->
                _userInfo.value = loadedUserInfo
                _isLoggedIn.value = loadedUserInfo != null
                sharedPreferences.edit { putString("userId", loadedUserInfo?.userId) }
                Log.d("UserViewModel", "User registered: ${_userInfo.value}")
                onComplete(loadedUserInfo)
            }
        }

    }

    // Needs testing still
    fun updateUserInfo(userInfo: HashMap<String, Any>, onComplete: (Boolean) -> Unit) {
        Log.d("UserViewModel", "Updating user info: $userInfo")

        viewModelScope.launch {
            userRepo.updateUserInfo(userInfo, onComplete)
        }
    }

    fun changeProfilePicture(userId: String, profilePicture: Uri, onComplete: (Boolean) -> Unit) {
        _tempImageURI.value = profilePicture

        Log.d("UserViewModel", "Changing profile picture for user with ID: $userId")
        _userInfo.value = _userInfo.value?.copy(profilePicture = profilePicture.toString())
        viewModelScope.launch {
            userRepo.changeProfilePicture(userId, profilePicture) { newProfilePicture ->
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

    fun deleteUser(userId: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            userRepo.deleteUser(userId, onComplete)
        }
    }
}