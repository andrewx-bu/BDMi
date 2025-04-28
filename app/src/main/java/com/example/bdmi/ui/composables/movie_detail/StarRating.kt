package com.example.bdmi.ui.composables.movie_detail

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

// Written by ChatGPT
@Composable
fun StarRating(
    rating: Float,
    onRatingChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
    starCount: Int = 5,
    starSize: Int = 32
) {
    var localRating by remember { mutableFloatStateOf(rating) }

    LaunchedEffect(rating) {
        localRating = rating
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        onRatingChanged(localRating)
                    },
                    onHorizontalDrag = { change, _ ->
                        val starWidth = starSize.dp.toPx()
                        val normalizedX = change.position.x.coerceIn(0f, starWidth * starCount)
                        val rawRating = (normalizedX / starWidth)

                        val finalRating = (rawRating * 2).roundToInt() / 2f
                        localRating = finalRating.coerceIn(0.5f, starCount.toFloat())
                    }
                )
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val starWidth = starSize.dp.toPx()
                    val normalizedX = offset.x.coerceIn(0f, starWidth * starCount)
                    val rawRating = (normalizedX / starWidth)

                    val finalRating = (rawRating * 2).roundToInt() / 2f
                    localRating = finalRating.coerceIn(0.5f, starCount.toFloat())
                    onRatingChanged(localRating)
                }
            }
    ) {
        for (i in 1..starCount) {
            val icon: ImageVector = when {
                localRating >= i -> Icons.Filled.Star
                localRating >= i - 0.5f -> Icons.AutoMirrored.Filled.StarHalf
                else -> Icons.Outlined.StarBorder
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Yellow,
                modifier = Modifier.padding(2.dp).size(starSize.dp)
            )
        }
    }
}



@Preview
@Composable
fun PreviewStarRating() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        StarRating(
            rating = 3.5f,
            onRatingChanged = { newRating ->
                // Handle the rating change
            }
        )
    }
}