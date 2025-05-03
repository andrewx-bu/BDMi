package com.example.bdmi.ui.home.movie_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bdmi.SessionViewModel
import com.example.bdmi.data.repositories.MovieReview
import com.example.bdmi.ui.composables.movie_detail.middle.ReviewCard

@Composable
fun AllReviews(
    sessionViewModel: SessionViewModel,
    movieId: Int,
    onProfileClick: (String) -> Unit,
) {
    val reviewViewModel : ReviewViewModel = hiltViewModel()
    val reviews = reviewViewModel.reviews.collectAsState().value
    val timeFilter = reviewViewModel.timeFilter.collectAsState().value
    val ratingFilter = reviewViewModel.ratingFilter.collectAsState().value
    val scrollState = rememberLazyListState()

    LaunchedEffect(movieId) {
        sessionViewModel.clearSelectedMovie()
        sessionViewModel.clearSelectedMovieReview()
    }

    LaunchedEffect(timeFilter, ratingFilter) {
        reviewViewModel.loadNextPage(
            movieId = movieId,
            newRatingFilter = ratingFilter,
            newTimeFilter = timeFilter
        )
    }

    LazyColumn (
        state = scrollState,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(reviews) { review : MovieReview ->
            ReviewCard(
                review = review,
                onProfileClick = onProfileClick,
            )
        }
    }
    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItem ->
                if (lastVisibleItem != null && lastVisibleItem >= reviews.size - 1) {
                    reviewViewModel.loadNextPage(
                        movieId = movieId,
                        newRatingFilter = ratingFilter,
                        newTimeFilter = timeFilter
                    )
                }
            }
    }
}