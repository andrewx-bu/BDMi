package com.example.bdmi.ui.composables.movie_detail.middle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.example.bdmi.data.utils.formatReviewCount
import com.example.bdmi.ui.theme.dimens

@Composable
fun ReviewHistogram(
    averageRating: Double,
    totalRatings: Int,
    ratingBreakdown: Map<String, Int>,
) {
    val maxCount = remember(ratingBreakdown) {
        ratingBreakdown.values.maxOrNull()?.coerceAtLeast(1) ?: 1
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        // Average + Review Count
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiaryContainer,
                    modifier = Modifier.size(dimens.iconLarge)
                )
                Spacer(Modifier.width(dimens.small2))
                Text(
                    text = "%.1f".format(averageRating),
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.inverseSurface
                )
            }
            Spacer(Modifier.height(dimens.small2))
            Text(
                text = "(${formatReviewCount(totalRatings)})",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.width(dimens.medium1))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .height(dimens.reviewHeight)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimens.small3),
                verticalAlignment = Alignment.Bottom
            ) {
                // Construct bars
                (1..10).map { it * 0.5f }.forEach { rating ->
                    val count = ratingBreakdown[rating.toString()] ?: 0
                    val fraction = count.toFloat() / maxCount
                    Box(
                        modifier = Modifier
                            .width(dimens.large1)
                            .fillMaxHeight(fraction)
                            .background(
                                MaterialTheme.colorScheme.tertiaryContainer,
                                RoundedCornerShape(dimens.small1)
                            )
                    )
                }
            }

            Spacer(Modifier.height(dimens.small2))

            // Y-axis Labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimens.small3),
                verticalAlignment = Alignment.CenterVertically
            ) {
                (1..10).map { it * 0.5f }.forEach { rating ->
                    Text(
                        text = if (rating % 1 == 0f) rating.toInt().toString() else "%.1f".format(
                            rating
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.width(dimens.large1),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}