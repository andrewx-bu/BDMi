package com.example.bdmi.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bdmi.data.repositories.UserReview
import com.example.bdmi.ui.composables.profile.UserReviewCard
import com.example.bdmi.ui.theme.dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserReviews(
    userId: String,
    onMovieClick: (Int) -> Unit
) {
    val userReviewsViewModel: UserReviewsViewModel = hiltViewModel()
    val reviews = userReviewsViewModel.reviews.collectAsState().value
    val timeFilter = userReviewsViewModel.timeFilter.collectAsState().value
    val ratingFilter = userReviewsViewModel.ratingFilter.collectAsState().value
    val scrollState = rememberLazyListState()

    LaunchedEffect(timeFilter, ratingFilter) {
        userReviewsViewModel.loadNextPage(
            userId = userId,
            newRatingFilter = ratingFilter,
            newTimeFilter = timeFilter
        )
    }
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(dimens.medium2)
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Reviews",
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.weight(1f)
            )
        }
        LazyColumn(
            state = scrollState,
            verticalArrangement = Arrangement.spacedBy(dimens.medium3),
        ) {
            items(reviews) { review: UserReview ->
                UserReviewCard(
                    review = review,
                    onMovieClick = onMovieClick,
                )
            }
        }
        LaunchedEffect(scrollState) {
            snapshotFlow { scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                .collect { lastVisibleItem ->
                    if (lastVisibleItem != null && lastVisibleItem >= reviews.size - 1) {
                        userReviewsViewModel.loadNextPage(
                            userId = userId,
                            newRatingFilter = ratingFilter,
                            newTimeFilter = timeFilter
                        )
                    }
                }
        }
    }
}