package com.example.bdmi.ui.composables.movie_detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.example.bdmi.ui.theme.dimens
import kotlinx.coroutines.launch

// TODO: Prioritize Friend Reviews.
// TODO: Make Reviews clickable.
@Composable
fun ReviewCarousel(reviews: List<String>) {
    // Simulate infinite scroll
    val pageCount = 1000 * reviews.size
    val startIndex = (pageCount / 2)
    val pagerState = rememberPagerState(
        initialPage = startIndex,
        pageCount = { pageCount }
    )
    val currentReviewIndex by remember { derivedStateOf { pagerState.currentPage % reviews.size } }
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
                text = "REVIEWS",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                modifier = Modifier.padding(horizontal = MaterialTheme.dimens.small3)
            )
            Box(modifier = Modifier.weight(1f)) {
                ShimmeringDivider()
            }
        }

        // Scrollable Review Cards
        HorizontalPager(state = pagerState) { page ->
            val reviewIndex = page % reviews.size
            ReviewCard(
                text = reviews[reviewIndex],
                rating = 5f,
                liked = true,
                username = "Steve"
            )
        }

        Spacer(Modifier.height(MaterialTheme.dimens.small3))

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
}
