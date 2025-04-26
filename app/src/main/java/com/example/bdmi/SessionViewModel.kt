package com.example.bdmi

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
import com.example.bdmi.data.repositories.CustomList
import com.example.bdmi.data.repositories.Movie
import com.example.bdmi.data.repositories.MovieRepository
import com.example.bdmi.data.repositories.WatchlistRepository
import com.example.bdmi.data.utils.SessionManager

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
class SessionViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val userRepository: UserRepository,
    private val watchlistRepository: WatchlistRepository,
    private val movieRepository: MovieRepository
) : ViewModel() {
    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo: StateFlow<UserInfo?> = _userInfo.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized

    private val _watchlists = MutableStateFlow<List<CustomList>>(emptyList())
    val watchlists: StateFlow<List<CustomList>> = _watchlists.asStateFlow()

    private val _cachedMovies = MutableStateFlow<List<Movie>>(emptyList())
    val cachedMovies: StateFlow<List<Movie>> = _cachedMovies.asStateFlow()

    init {
        val userId = sessionManager.getUserId()
        Log.d("UserViewModel", "UserViewModel initialized with userId: $userId")
        if (userId != null) {
            viewModelScope.launch {
                loadUser(userId) { loadedUserInfo ->
                    if (loadedUserInfo != null) {
                        loadCachedInfo()
                    }
                }
            }
        } else
            _isInitialized.value = true
    }

    fun loadUser(userId: String?, onComplete: (UserInfo?) -> Unit) {
        Log.d("UserViewModel", "Loading user with ID: $userId")
        if (userId == null) {
            _isInitialized.value = true
            return
        }

        viewModelScope.launch {
            userRepository.loadUser(userId) { loadedUserInfo ->
                _userInfo.value = loadedUserInfo
                _isLoggedIn.value = loadedUserInfo != null
                _isInitialized.value = true
                if (loadedUserInfo != null) {
                    loadCachedInfo()
                }
                Log.d("UserViewModel", "User loaded: ${_userInfo.value}")
                onComplete(_userInfo.value)
            }
        }
    }

    // Load various session info to be cached for later use
    /*
     * Loads the user's custom lists from the db.
     * TODO: Load discover movies from TMDB
     */
    private fun loadCachedInfo() {
        viewModelScope.launch {
            watchlistRepository.getLists(_userInfo.value?.userId.toString()) { lists ->
                _watchlists.value = lists
            }
        }
    }

    // TODO: Job for Andrew
    private fun loadDiscoverMovies() {

    }

    fun login(
        loginInformation: HashMap<String, String>,
        onComplete: (UserInfo?) -> Unit
    ) {
        Log.d("UserViewModel", "Logging in with email: ${loginInformation["email"]}")

        viewModelScope.launch {
            userRepository.authenticateUser(loginInformation) { userId ->
                if (userId != null) {
                    loadUser(userId) { loadedUserInfo ->
                        _userInfo.value = loadedUserInfo
                        _isLoggedIn.value = loadedUserInfo != null
                        sessionManager.saveUserId(userId)
                        loadCachedInfo()
                        Log.d("UserViewModel", "User logged in: ${_userInfo.value}")
                        onComplete(loadedUserInfo)
                    }
                }
            }
        }
    }

    fun logout() {
        _isLoggedIn.value = false
        _userInfo.value = null
        _watchlists.value = emptyList()
        _cachedMovies.value = emptyList() // Clear user info on logout
        sessionManager.clearUserId()
    }

    fun register(
        userInformation: HashMap<String, Any>,
        onComplete: (UserInfo?) -> Unit
    ) {
        Log.d("UserViewModel", "Registering user with email: ${userInformation["email"]}")

        viewModelScope.launch {
            userRepository.createUser(userInformation) { loadedUserInfo ->
                _userInfo.value = loadedUserInfo
                _isLoggedIn.value = loadedUserInfo != null
                sessionManager.saveUserId(loadedUserInfo?.userId.toString())
                Log.d("UserViewModel", "User registered: ${_userInfo.value}")
                onComplete(loadedUserInfo)
            }
        }
    }

    // Needs testing still
    fun updateUserInfo(userInfo: HashMap<String, Any>, onComplete: (Boolean) -> Unit) {
        Log.d("UserViewModel", "Updating user info: $userInfo")

        viewModelScope.launch {
            userRepository.updateUserInfo(userInfo, onComplete)
        }
    }

    // Needs testing still
    fun deleteUser(userId: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            userRepository.deleteUser(userId, onComplete)
        }
    }
}