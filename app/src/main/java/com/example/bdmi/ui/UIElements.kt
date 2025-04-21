package com.example.bdmi.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.example.bdmi.ui.theme.dimens
import com.example.bdmi.ui.theme.uiConstants
import com.valentinilk.shimmer.LocalShimmerTheme
import com.valentinilk.shimmer.defaultShimmerTheme
import com.valentinilk.shimmer.shimmer

// Horizontal Divider with custom shimmer
@Composable
fun ShimmeringDivider() {
    val shimmerTheme = defaultShimmerTheme.copy(
        shaderColors = listOf(
            MaterialTheme.colorScheme.onPrimaryContainer,
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        ),
        blendMode = BlendMode.SrcOver
    )

    CompositionLocalProvider(LocalShimmerTheme provides shimmerTheme) {
        HorizontalDivider(
            modifier = Modifier
                .height(MaterialTheme.dimens.small1)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.onPrimaryContainer)
                .shimmer()
        )
    }
}

// Genre chips in MovieDetail screen
@Composable
fun GenreChip(name: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(MaterialTheme.dimens.small2))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .border(
                width = MaterialTheme.dimens.small1,
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = RoundedCornerShape(MaterialTheme.dimens.small3)
            )
            .clickable(onClick = onClick)
            .padding(
                horizontal = MaterialTheme.dimens.medium3,
                vertical = MaterialTheme.dimens.small2
            )
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

// Dots for ReviewCarousel in MovieDetail screen
@Composable
fun DotsIndicator(numDots: Int, currentIndex: Int, onDotClick: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.dimens.small2),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until numDots) {
            Dot(index = i, isSelected = i == currentIndex, onDotClick)
            Spacer(modifier = Modifier.width(MaterialTheme.dimens.medium3))
        }
    }
}

// Individual dots in DotIndicator
// TODO: Increase size on selected dot
@Composable
fun Dot(index: Int, isSelected: Boolean, onDotClick: (Int) -> Unit) {
    val color =
        if (isSelected) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.secondaryContainer
    Box(
        modifier = Modifier
            .size(MaterialTheme.dimens.carouselDotSize)
            .background(color = color, shape = CircleShape)
            .clickable {
                onDotClick(index)
            }
    )
}

// Error container displaying error message
@Composable
fun ErrorMessage(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(MaterialTheme.dimens.medium1),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onErrorContainer,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        IconButton(
            onClick = onRetry,
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Retry",
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(MaterialTheme.dimens.iconLarge)
            )
        }
    }
}

// TODO: Add Profile, Stars, Heart?
@Composable
fun ReviewCard(text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.dimens.medium3)
            .height(MaterialTheme.dimens.reviewCardHeight),
        shape = RoundedCornerShape(MaterialTheme.dimens.medium3),
        elevation = CardDefaults.cardElevation(MaterialTheme.dimens.small3)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(MaterialTheme.dimens.medium1),
            style = MaterialTheme.typography.bodyLarge,
            maxLines = MaterialTheme.uiConstants.reviewMaxLines,
            overflow = TextOverflow.Ellipsis
        )
    }
}


// Fading edge gradient
fun Modifier.fadingEdge(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }