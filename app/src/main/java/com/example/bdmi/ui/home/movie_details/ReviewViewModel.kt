package com.example.bdmi.ui.home.movie_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bdmi.data.repositories.MovieReview
import com.example.bdmi.data.repositories.ReviewRepository
import com.example.bdmi.data.repositories.TimeFilter
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ReviewViewModel"

@HiltViewModel
class ReviewViewModel @Inject constructor(private val reviewRepository: ReviewRepository
): ViewModel() {
    private val _reviews = MutableStateFlow<List<MovieReview>>(emptyList())
    val reviews = _reviews.asStateFlow()

    private var lastSnapshot: DocumentSnapshot? = null

    private val _timeFilter = MutableStateFlow<TimeFilter>(TimeFilter.DESCENDING)
    val timeFilter = _timeFilter.asStateFlow()

    private val _ratingFilter = MutableStateFlow<Float?>(null)
    val ratingFilter = _ratingFilter.asStateFlow()

    private val _loading = MutableStateFlow(true)
    val loading = _loading.asStateFlow()

    private var noMoreReviews = false

    fun loadNextPage(movieId: Int, newRatingFilter: Float? = null, newTimeFilter: TimeFilter = TimeFilter.DESCENDING) {
        if (noMoreReviews) return
        _loading.value = true
        if (_ratingFilter.value != newRatingFilter || _timeFilter.value != newTimeFilter) {
            _ratingFilter.value = newRatingFilter
            _timeFilter.value = newTimeFilter
            lastSnapshot = null
            _reviews.value = emptyList()
        }
        viewModelScope.launch {
            reviewRepository.getReviews(
                movieId = movieId,
                rating = _ratingFilter.value,
                lastVisible = lastSnapshot,
                pageSize = 5,
            ) { reviews, newLast ->
                if (reviews.size < 5)
                    noMoreReviews = true
                lastSnapshot = newLast
                _reviews.value = _reviews.value + reviews
                _loading.value = false
            }
        }
    }

    fun setRatingFilter(rating: Float?) {
        _ratingFilter.value = rating
    }

    fun setTimeFilter(timeFilter: TimeFilter) {
        _timeFilter.value = timeFilter
    }
}