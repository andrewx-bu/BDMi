package com.example.bdmi.ui.composables.movie_detail.middle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.example.bdmi.data.repositories.MovieMetrics
import com.example.bdmi.data.repositories.MovieReview
import com.example.bdmi.ui.theme.dimens
import kotlinx.coroutines.launch

@Composable
fun ReviewSection(
    reviews: List<MovieReview>,
    movieData: MovieMetrics?,
    onProfileClick: (String) -> Unit,
    onAllReviewsClick: () -> Unit
) {
    // Simulate infinite scroll
    val pageCount = 1000 * reviews.size
    val startIndex = (pageCount / 2)
    val pagerState = rememberPagerState(
        initialPage = startIndex,
        pageCount = { pageCount }
    )
    val coroutineScope = rememberCoroutineScope()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Shimmering Review Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .graphicsLayer { scaleX = -1f }
            ) {
                ShimmeringDivider()
            }
            Text(
                text = "RATINGS",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                modifier = Modifier.padding(horizontal = dimens.small3)
            )
            Box(modifier = Modifier.weight(1f)) {
                ShimmeringDivider()
            }
        }

        var averageRating by remember { mutableDoubleStateOf(0.0) }
        var totalRatings by remember { mutableIntStateOf(0) }
        var ratingBreakdown by remember { mutableStateOf(mapOf<String, Int>()) }
        if (movieData != null) {
            averageRating = movieData.averageRating
            totalRatings = movieData.ratingCount
            ratingBreakdown = movieData.ratingBreakdown
        }
        if (totalRatings != 0) {
            ReviewHistogram(
                averageRating = averageRating,
                totalRatings = totalRatings,
                ratingBreakdown = ratingBreakdown
            )
        } else {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = dimens.medium2)
            ) {
                Text("No reviews available")
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .graphicsLayer { scaleX = -1f }
            ) {
                ShimmeringDivider()
            }
            Text(
                text = "FEATURED REVIEWS",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                modifier = Modifier.padding(horizontal = dimens.small3)
            )
            Box(modifier = Modifier.weight(1f)) {
                ShimmeringDivider()
            }
        }
        if (reviews.isEmpty()) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = dimens.medium2)
            ) {
                Text("No reviews available")
            }
        } else {
            val currentReviewIndex by remember { derivedStateOf { pagerState.currentPage % reviews.size } }
            // Scrollable Review Cards
            HorizontalPager(state = pagerState) { page ->
                val reviewIndex = page % reviews.size
                val review = reviews[reviewIndex]
                ReviewCard(
                    review = review,
                    liked = true,
                    onProfileClick = onProfileClick,
                )
            }

            Spacer(Modifier.height(dimens.small3))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimens.medium2),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    DotsIndicator(
                        numDots = reviews.size,
                        currentIndex = currentReviewIndex,
                        onDotClick = { index ->
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                    )
                }
                TextButton(
                    onClick = { onAllReviewsClick() } ,
                    modifier = Modifier.padding(horizontal = dimens.small3)
                ) {
                    Text(
                        text = "ALL REVIEWS (${movieData?.reviewCount})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        modifier = Modifier.padding(horizontal = dimens.small3)
                    )
                }
            }
        }
    }
}
