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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.text.style.TextAlign
import com.example.bdmi.ui.theme.Spacing
import com.example.bdmi.ui.theme.UIConstants
import com.valentinilk.shimmer.LocalShimmerTheme
import com.valentinilk.shimmer.defaultShimmerTheme
import com.valentinilk.shimmer.shimmer

@Composable
fun ShimmeringDivider() {
    val shimmerTheme = defaultShimmerTheme.copy(
        shaderColors = listOf(
            MaterialTheme.colorScheme.onPrimaryContainer,
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        ),
        blendMode = BlendMode.SrcOver
    )

    CompositionLocalProvider(LocalShimmerTheme provides shimmerTheme) {
        Row(
            modifier = Modifier
                .height(Spacing.extraSmall)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.onPrimaryContainer)
                .shimmer()
        ) {
            Text("")
        }
    }
}

@Composable
fun GenreChip(name: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(Spacing.small))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .border(
                width = UIConstants.genreChipBorder,
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(Spacing.small)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.medium, vertical = Spacing.extraSmall)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
fun DotsIndicator(numDots: Int, currentIndex: Int, onDotClick: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.small),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until numDots) {
            Dot(index = i, isSelected = i == currentIndex, onDotClick)
            Spacer(modifier = Modifier.width(Spacing.medium))
        }
    }
}

// TODO: Increase size on selected dot
@Composable
fun Dot(index: Int, isSelected: Boolean, onDotClick: (Int) -> Unit) {
    val color =
        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
    Box(
        modifier = Modifier
            .size(UIConstants.carouselDotSize)
            .background(color = color, shape = CircleShape)
            .clickable {
                onDotClick(index)
            }
    )
}

@Composable
fun ErrorMessage(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(Spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onErrorContainer,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.small))

        IconButton(
            onClick = onRetry,
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Retry",
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(UIConstants.backdropButtonSize)
            )
        }
    }
}