package com.example.bdmi.ui.composables.profile

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.bdmi.data.repositories.UserReview
import com.example.bdmi.ui.composables.movie_detail.middle.DotsIndicator
import com.example.bdmi.ui.composables.movie_detail.middle.ShimmeringDivider
import com.example.bdmi.ui.theme.dimens
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserReviewCarousel(
    reviews: List<UserReview>,
    pagerState: PagerState,
    onMovieClick: (Int) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    if (reviews.isEmpty()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = dimens.medium2)
        ) {
            Text("No reviews available")
        }
    } else {
        val currentReviewIndex by remember { derivedStateOf { pagerState.currentPage % reviews.size } }

        HorizontalPager(state = pagerState) { page ->
            val reviewIndex = page % reviews.size
            val review = reviews[reviewIndex]
            UserReviewCard(
                review = review,
                onMovieClick = onMovieClick
            )
        }

        Spacer(modifier = Modifier.height(dimens.small2))

        // Dots
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
        }
   }
}
