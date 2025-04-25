package com.example.bdmi.ui.home

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bdmi.ui.composables.movie_detail.middle.ReviewHistogram

@Composable
fun SearchScreen() {
    /*
    var isTransformed by rememberSaveable { mutableStateOf(false) }
    val width by animateDpAsState(
        targetValue = if (isTransformed) 360.dp else 130.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = 1000f
        )
    )

    val height by animateDpAsState(
        targetValue = if (isTransformed) 620.dp else 220.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = 1000f
        )
    )

    Icon(
        imageVector = Icons.Default.Favorite,
        contentDescription = null,
        modifier = Modifier
            .size(width = width, height = height)
            .clickable { isTransformed = !isTransformed }
    )
     */
    ReviewHistogram(
        averageRating = 3.9f,
        totalReviews = 12045,
        ratingCounts = mapOf(
            "5.0" to 2345, "4.5" to 1600, "4.0" to 2800, "3.5" to 3400,
            "3.0" to 2300, "2.5" to 1200, "2.0" to 500, "1.5" to 300,
            "1.0" to 150, "0.5" to 1600
        )
    )
}