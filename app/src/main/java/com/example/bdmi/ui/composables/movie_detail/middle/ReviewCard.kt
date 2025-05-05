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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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

            Spacer(Modifier.weight(1f))

            // User Info (Display Name + Profile Picture)
            Text(
                text = review.displayName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(Modifier.width(dimens.small2))

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
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(Modifier.height(dimens.small3))

        // Review Card (Title + Text)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.small2)
                .height(140.dp),
            shape = RoundedCornerShape(dimens.medium1),
            elevation = CardDefaults.cardElevation(dimens.medium2)
        ) {
            Column(modifier = Modifier.padding(dimens.medium1)) {
                Text(
                    text = review.reviewTitle,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(dimens.small2))
                Text(
                    text = review.reviewText,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = uiConstants.reviewMaxLines,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


/*Icon(
    imageVector = Icons.Default.Group,
    contentDescription = "Friend",
    tint = MaterialTheme.colorScheme.secondary,
    modifier = Modifier.size(dimens.iconSmall)
)*/