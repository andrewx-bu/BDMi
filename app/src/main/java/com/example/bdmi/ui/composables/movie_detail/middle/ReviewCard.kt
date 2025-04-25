package com.example.bdmi.ui.composables.movie_detail.middle

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Group
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
import androidx.compose.ui.text.style.TextOverflow
import com.example.bdmi.ui.theme.dimens
import com.example.bdmi.ui.theme.uiConstants

@Composable
fun ReviewCard(text: String, rating: Float, liked: Boolean, username: String) {
    Column {
        // Review info row
        Row(
            modifier = Modifier.padding(horizontal = MaterialTheme.dimens.small2),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val fullStars = rating.toInt()
            val hasHalf = (rating - fullStars) >= 0.5f
            val starCount = fullStars + if (hasHalf) 1 else 0
            // Stars
            repeat(starCount) { index ->
                val icon = when {
                    index < fullStars -> Icons.Default.Star
                    else -> Icons.AutoMirrored.Filled.StarHalf
                }
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiaryContainer,
                    modifier = Modifier.size(MaterialTheme.dimens.iconMedium)
                )
                Spacer(Modifier.width(MaterialTheme.dimens.small1))
            }
            // Heart
            if (liked) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Liked",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(MaterialTheme.dimens.iconMedium)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            //
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = username,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                // TODO: Show Icon if is Friend
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = "Friend",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(MaterialTheme.dimens.iconSmall)
                )
            }
            Spacer(Modifier.width(MaterialTheme.dimens.small3))
            // Profile photo
            Box(
                modifier = Modifier
                    .size(MaterialTheme.dimens.iconLarge)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .clickable { /* TODO: Implement */ }
            )
        }
        Spacer(Modifier.height(MaterialTheme.dimens.small3))
        // Review text
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaterialTheme.dimens.small2)
                .height(MaterialTheme.dimens.reviewCardHeight),
            shape = RoundedCornerShape(MaterialTheme.dimens.medium3),
            elevation = CardDefaults.cardElevation(MaterialTheme.dimens.medium2)
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(MaterialTheme.dimens.medium1),
                style = MaterialTheme.typography.labelLarge,
                maxLines = MaterialTheme.uiConstants.reviewMaxLines,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
