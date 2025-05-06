package com.example.bdmi.ui.composables.movie_detail.middle

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import com.example.bdmi.data.repositories.MovieReview
import com.example.bdmi.ui.theme.dimens
import com.example.bdmi.ui.theme.uiConstants

@Composable
fun ReviewCard(
    review: MovieReview,
    liked: Boolean = true,
    onProfileClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        // Top Row: Rating, Heart, User Info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.small2),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User Info (Display Name + Profile Picture)
            Box(
                modifier = Modifier
                    .size(dimens.iconLarge)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .clickable { onProfileClick(review.userId) }
            ) {
                AsyncImage(
                    model = review.userProfilePicture,
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(Modifier.width(dimens.small2))

            Text(
                text = review.displayName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(Modifier.weight(1f))

            // Stars
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

            if (liked) {
                Spacer(Modifier.width(dimens.small1))
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Liked",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(dimens.iconMedium)
                )
            }
        }

        Spacer(Modifier.height(dimens.small3))

        // Review Card (Title + Text)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimens.reviewCardHeight),
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
                maxLines = uiConstants.reviewMaxLines,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(dimens.small3)
            )
        }
    }
}