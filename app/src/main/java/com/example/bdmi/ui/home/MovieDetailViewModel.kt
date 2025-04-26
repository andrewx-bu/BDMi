package com.example.bdmi.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bdmi.data.api.APIError
import com.example.bdmi.data.api.models.MovieDetails
import com.example.bdmi.data.api.models.WatchProvidersResponse
import com.example.bdmi.data.api.toAPIError
import com.example.bdmi.data.repositories.CustomList
import com.example.bdmi.data.repositories.MediaItem
import com.example.bdmi.data.repositories.MovieMetrics
import com.example.bdmi.data.repositories.MovieRepository
import com.example.bdmi.data.repositories.MovieReview
import com.example.bdmi.data.repositories.ReviewRepository
import com.example.bdmi.data.repositories.UserReview
import com.example.bdmi.data.repositories.WatchlistRepository
import com.example.bdmi.data.utils.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlinx.coroutines.launch

private const val TAG = "MovieDetailViewModel"

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val movieRepo: MovieRepository,
    private val watchlistRepository: WatchlistRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {
    data class DetailUIState(
        override val isLoading: Boolean = false,
        val details: MovieDetails? = null,
        val providers: WatchProvidersResponse? = null,
        override val error: APIError? = null
    ) : UIState
    private val _detailUIState = MutableStateFlow(DetailUIState())
    val detailUIState = _detailUIState.asStateFlow()

    private val _lists = MutableStateFlow<List<CustomList>>(emptyList())
    val lists: StateFlow<List<CustomList>> = _lists.asStateFlow()

    private val _userPrivileges = MutableStateFlow(false)
    val userPrivileges: StateFlow<Boolean> = _userPrivileges.asStateFlow()

    private val _carouselReviews = MutableStateFlow<List<MovieReview>>(
        listOf(
        MovieReview(
            userId = "1",
            displayName = "John Doe",
            userProfilePicture = "https://example.com/profile.jpg",
            reviewTitle = "Great Movie!",
            reviewText = "I really enjoyed this movie. It was amazing!",
            rating = 3.5f,
        ))
    )
    val carouselReviews: StateFlow<List<MovieReview>> = _carouselReviews.asStateFlow()

    private val _userReview = MutableStateFlow<MovieReview?>(null)
    val userReview: StateFlow<MovieReview?> = _userReview.asStateFlow()

    private val _movieData = MutableStateFlow<MovieMetrics?>(null)
    val movieData: StateFlow<MovieMetrics?> = _movieData.asStateFlow()

    fun refreshDetails(movieId: Int) {
        _detailUIState.update { DetailUIState(isLoading = true, error = null) }
        loadMovieDetails(movieId)
        loadMovieData(movieId)
    }

    private fun loadMovieDetails(movieId: Int) {
        viewModelScope.launch {
            _detailUIState.update { it.copy(isLoading = true, error = null) }
            // Simulate Network Delay
            delay(1000)

            // Start API calls in parallel
            val detailsDeferred = async { movieRepo.getMovieDetails(movieId) }
            val providersDeferred = async { movieRepo.getWatchProviders(movieId) }

            val detailsResult = detailsDeferred.await()
            val providersResult = providersDeferred.await()

            detailsResult.fold(
                onSuccess = { details ->
                    _detailUIState.update { it.copy(details = details) }
                },
                onFailure = { e ->
                    _detailUIState.update { it.copy(error = e.toAPIError()) }
                }
            )

            providersResult.fold(
                onSuccess = { providers ->
                    _detailUIState.update { it.copy(providers = providers) }
                },
                onFailure = { e ->
                    _detailUIState.update { it.copy(error = e.toAPIError()) }
                }
            )

            _detailUIState.update { it.copy(isLoading = false) }
        }
    }

    private fun loadMovieData(movieId: Int) {
        viewModelScope.launch {
            _detailUIState.update { it.copy(isLoading = true, error = null) }
            reviewRepository.getMovieData(movieId) { movieData ->
                _movieData.value = movieData
            }
        }
    }

    fun setPrivileges(privileges: Boolean) {
        _userPrivileges.value = privileges
    }

    fun addToWatchlist(userId: String, listId: String, item: MediaItem) {
        Log.d(TAG, "Adding item to watchlist: $item")

        viewModelScope.launch {
            watchlistRepository.addToList(listId, userId, item)
        }
    }

    fun setLists(watchlists: List<CustomList>) {
        _lists.value = watchlists
    }

    // Loads reviews for the carousel
    fun reviewCarousel(movieId: Int) {
        viewModelScope.launch {
            reviewRepository.getReviews(movieId, pageSize = 5) { reviews, _ ->
                _carouselReviews.value = _carouselReviews.value + reviews
            }
        }
    }

    // Attempts to load the user review to pin for the carousel
    fun loadUserReview(userId: String, movieId: Int) {
        viewModelScope.launch {
            reviewRepository.getReview(userId, movieId) { review ->
                if (review != null) {
                    Log.d(TAG, "User review retrieved: $review")
                    _carouselReviews.value = listOf(review) + _carouselReviews.value
                    _userReview.value = review
                } else
                    Log.d(TAG, "User review not found")
            }
        }
    }

    // Both used to create and edit review
    fun createReview(
        userId: String, movieId: Int,
        movieReview: MovieReview, userReview: UserReview,
        onComplete: (Boolean) -> Unit
    ) {
        if (_userReview.value != null)
            _carouselReviews.value = _carouselReviews.value.drop(1)
        _carouselReviews.value = _carouselReviews.value + movieReview
        _userReview.value = movieReview

        viewModelScope.launch {
            reviewRepository.createReview(userId, movieId, movieReview, userReview) {
                if (it) {
                    Log.d(TAG, "Review created successfully")
                    onComplete(true)
                } else {
                    Log.d(TAG, "Review creation failed")
                    onComplete(false)
                }
            }
        }
    }
}