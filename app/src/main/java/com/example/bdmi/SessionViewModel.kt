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
import com.example.bdmi.data.api.models.MovieDetails
import kotlinx.coroutines.launch
import com.example.bdmi.data.repositories.CustomList
import com.example.bdmi.data.repositories.MovieRepository
import com.example.bdmi.data.repositories.MovieReview
import com.example.bdmi.data.repositories.Notification
import com.example.bdmi.data.repositories.NotificationRepository
import com.example.bdmi.data.repositories.UserInfo
import com.example.bdmi.data.repositories.WatchlistRepository
import com.example.bdmi.data.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth

private const val TAG = "SessionViewModel"

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val sessionManager: SessionManager,
    private val userRepository: UserRepository,
    private val watchlistRepository: WatchlistRepository,
    private val movieRepository: MovieRepository,
    private val notificationRepository: NotificationRepository
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

    private val _numUnreadNotifications = MutableStateFlow(0)
    val numUnreadNotifications: StateFlow<Int> = _numUnreadNotifications.asStateFlow()

    private val _cachedMovies = MutableStateFlow<List<Movie>>(emptyList())
    val cachedMovies: StateFlow<List<Movie>> = _cachedMovies.asStateFlow()

    private val _selectedMovie = MutableStateFlow<MovieDetails?>(null)
    val selectedMovie: StateFlow<MovieDetails?> = _selectedMovie.asStateFlow()

    private val _selectedMovieReview = MutableStateFlow<MovieReview?>(null)
    val selectedMovieReview: StateFlow<MovieReview?> = _selectedMovieReview.asStateFlow()

    // Loads user if they are logged in, gets the current theme, and load cached info
    init {
        _darkMode.value = sessionManager.getDarkMode()
        val currentUser = firebaseAuth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid
            Log.d("UserViewModel", "UserViewModel initialized with userId: $userId")
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
            launch {
                watchlistRepository.getLists(_userInfo.value?.userId.toString()) { lists ->
                    _watchlists.value = lists
                }
            }
            launch {
                notificationRepository.getNotifications(_userInfo.value?.userId.toString()) { notifications ->
                    _numUnreadNotifications.value = notifications.count { !it.read }
                }
            }
        }
    }

    // TODO: Job for Andrew
    private fun loadDiscoverMovies() {

    }

    fun logout() {
        firebaseAuth.signOut()
        userRepository.removeUserListener()
        _isLoggedIn.value = false
        _userInfo.value = null
        _watchlists.value = emptyList()
        _cachedMovies.value = emptyList() // Clear user info on logout
        sessionManager.clearUserId()
    }

    fun login(
        email: String,
        password: String,
        onComplete: (UserInfo?) -> Unit
    ) {
        Log.d(TAG, "Logging in with email: $email")

        viewModelScope.launch {
            userRepository.authenticateUser(email, password) { userId ->
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

    // TODO: Update with Firebase Auth
    fun deleteUser(userId: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            userRepository.deleteUser(userId, onComplete)
        }
    }

    fun switchTheme() {
        _darkMode.value = !_darkMode.value
        sessionManager.setDarkMode(_darkMode.value)
    }

    fun loadSelectedMovie(movieDetails: MovieDetails) {
        _selectedMovie.value = movieDetails
    }

    // Unsure how to use this as of now
    fun clearSelectedMovie() {
        _selectedMovie.value = null
    }

    fun loadSelectedMovieReview(movieReview: MovieReview) {
        _selectedMovieReview.value = movieReview
    }

    fun clearSelectedMovieReview() {
        _selectedMovieReview.value = null
    }

    fun loadNumOfUnreadNotifications(numUnreadNotifications: Int) {
        _numUnreadNotifications.value = numUnreadNotifications
    }
}