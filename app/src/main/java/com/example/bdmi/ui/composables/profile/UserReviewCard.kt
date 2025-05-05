package com.example.bdmi.ui.composables.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.bdmi.data.repositories.UserReview
import com.example.bdmi.ui.composables.home.MoviePoster
import com.example.bdmi.ui.theme.dimens

@Composable
fun UserReviewCard(
    review: UserReview,
    onMovieClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimens.medium2),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().height(65.dp)
        ) {
            Row(
                modifier = Modifier.weight(.6f),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MoviePoster(
                    review.movieTitle,
                    review.posterPath,
                    3.dp
                ) { onMovieClick(review.movieId) }

                Text(
                    text = review.movieTitle,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(start = dimens.small2)
                        .clickable { onMovieClick(review.movieId) }
                )
            }

            Spacer(modifier = Modifier.width(dimens.small2))

            Row(
                modifier = Modifier.weight(.5f),
                horizontalArrangement = Arrangement.End
            ) {
                val fullStars = review.rating.toInt()
                val hasHalf = (review.rating - fullStars) >= 0.5f
                repeat(fullStars) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiaryContainer,
                        modifier = Modifier.size(dimens.iconMedium)
                    )
                }
                if (hasHalf) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.StarHalf,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiaryContainer,
                        modifier = Modifier.size(dimens.iconMedium)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(dimens.small3))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp)
                .height(140.dp),
            shape = RoundedCornerShape(dimens.medium1),
            elevation = CardDefaults.cardElevation(dimens.medium2)
        ) {
            Text(
                text = review.reviewTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(dimens.small3)
            )

            Text(
                text = review.reviewText,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(dimens.small3)
            )
        }

    }
}
