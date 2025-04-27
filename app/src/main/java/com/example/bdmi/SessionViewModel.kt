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
import com.example.bdmi.data.api.models.Movie
import kotlinx.coroutines.launch
import com.example.bdmi.data.repositories.CustomList
import com.example.bdmi.data.repositories.MovieRepository
import com.example.bdmi.data.repositories.WatchlistRepository
import com.example.bdmi.data.utils.SessionManager

private const val TAG = "SessionViewModel"

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
    private val _darkMode = MutableStateFlow(false)
    val darkMode: StateFlow<Boolean> = _darkMode.asStateFlow()

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
        _darkMode.value = sessionManager.getDarkMode()
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
        } else _isInitialized.value = true
    }

    fun loadUser(userId: String?, onComplete: (UserInfo?) -> Unit) {
        Log.d(TAG, "Loading (listening to) user with ID: $userId")
        if (userId == null) {
            _isInitialized.value = true
            return
        }

        userRepository.listenToUser(userId) { loadedUserInfo ->
            _userInfo.value = loadedUserInfo
            _isLoggedIn.value = loadedUserInfo != null
            _isInitialized.value = true
            Log.d(TAG, "User updated: ${_userInfo.value}")
            onComplete(loadedUserInfo)
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

    fun logout() {
        userRepository.removeUserListener()
        _isLoggedIn.value = false
        _userInfo.value = null
        _watchlists.value = emptyList()
        _cachedMovies.value = emptyList() // Clear user info on logout
        sessionManager.clearUserId()
    }

    fun login(
        loginInformation: HashMap<String, String>,
        onComplete: (UserInfo?) -> Unit
    ) {
        Log.d(TAG, "Logging in with email: ${loginInformation["email"]}")

        viewModelScope.launch {
            userRepository.authenticateUser(loginInformation) { userId ->
                if (userId != null) {
                    loadUser(userId) { loadedUserInfo ->
                        if (loadedUserInfo != null) {
                            _userInfo.value = loadedUserInfo
                            _isLoggedIn.value = true
                            sessionManager.saveUserId(userId)
                            loadCachedInfo()
                        }

                        Log.d("UserViewModel", "User logged in: ${_userInfo.value}")
                        onComplete(loadedUserInfo)
                    }
                }
            }
        }
    }

    fun register(
        userInformation: HashMap<String, Any>,
        onComplete: (Boolean) -> Unit
    ) {
        Log.d(TAG, "Registering user with email: ${userInformation["email"]}")

        viewModelScope.launch {
            userRepository.createUser(userInformation) { loadedUserInfo ->
                if (loadedUserInfo != null) {
                    _userInfo.value = loadedUserInfo
                    _isLoggedIn.value = true
                    loadCachedInfo()
                    sessionManager.saveUserId(loadedUserInfo.userId.toString())
                    Log.d(TAG, "User registered: ${_userInfo.value}")
                    onComplete(true)
                } else onComplete(false)
            }
        }
    }

    // Needs testing still
    fun updateUserInfo(userInfo: HashMap<String, Any>, onComplete: (Boolean) -> Unit) {
        Log.d(TAG, "Updating user info: $userInfo")

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

    fun switchTheme() {
        _darkMode.value = !_darkMode.value
        sessionManager.setDarkMode(_darkMode.value)
    }
}