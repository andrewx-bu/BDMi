package com.example.bdmi.ui.profile

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bdmi.SessionViewModel
import com.example.bdmi.ui.home.movie_details.ReviewViewModel

// TODO: Implement screen
@Composable
fun UserReviews(
    sessionViewModel: SessionViewModel,
    userId: String,
    onNavigateBack: () -> Unit,
    onMovieClick: (Int) -> Unit
) {
    val reviewViewModel : ReviewViewModel = hiltViewModel()
    val reviews = reviewViewModel.reviews.collectAsState().value
    val timeFilter = reviewViewModel.timeFilter.collectAsState().value
    val ratingFilter = reviewViewModel.ratingFilter.collectAsState().value
    val scrollState = rememberLazyListState()

//    LaunchedEffect(timeFilter, ratingFilter) {
//        reviewViewModel.loadNextPage(
//            movieId = movieId,
//            newRatingFilter = ratingFilter,
//            newTimeFilter = timeFilter
//        )
//    }
//
//    LazyColumn (
//        state = scrollState,
//        verticalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        items(reviews) { review : MovieReview ->
//            ReviewCard(
//                review = review,
//                onProfileClick = onProfileClick,
//            )
//        }
//    }
//    LaunchedEffect(scrollState) {
//        snapshotFlow { scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
//            .collect { lastVisibleItem ->
//                if (lastVisibleItem != null && lastVisibleItem >= reviews.size - 1) {
//                    reviewViewModel.loadNextPage(
//                        movieId = movieId,
//                        newRatingFilter = ratingFilter,
//                        newTimeFilter = timeFilter
//                    )
//                }
//            }
//    }
}