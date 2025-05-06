package com.example.bdmi.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.snapshotFlow
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bdmi.data.repositories.UserReview
import com.example.bdmi.ui.composables.profile.UserReviewCard
import com.example.bdmi.ui.theme.dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserReviews(
    userId: String,
    onNavigateBack: () -> Unit,
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
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Reviews") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {

                }
            )
        }
    ) { padding ->
        LazyColumn(
            state = scrollState,
            verticalArrangement = Arrangement.spacedBy(dimens.medium3),
            contentPadding = padding
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