package com.example.bdmi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import coil3.compose.AsyncImage
import com.example.bdmi.data.utils.ImageURLHelper
import com.example.bdmi.ui.theme.Spacing
import com.example.bdmi.ui.theme.UIConstants
import com.valentinilk.shimmer.LocalShimmerTheme
import com.valentinilk.shimmer.defaultShimmerTheme
import com.valentinilk.shimmer.shimmer

@Composable
fun MoviePoster(title: String, posterPath: String?, isLoading: Boolean, onClick: () -> Unit) {
    val imageUrl = ImageURLHelper.getPosterURL(posterPath)

    Box(
        modifier = Modifier
            .aspectRatio(UIConstants.POSTERSASPECTRATIO)
            .clip(RoundedCornerShape(Spacing.medium))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(enabled = !isLoading) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                val shimmerTheme = defaultShimmerTheme.copy(
                    shaderColors = listOf(
                        MaterialTheme.colorScheme.onPrimaryContainer,
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    blendMode = BlendMode.SrcOver
                )
                CompositionLocalProvider(LocalShimmerTheme provides shimmerTheme) {
                    Box(
                        modifier = Modifier
                            .shimmer()
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.onPrimaryContainer)
                    )
                }
            }

            imageUrl.isNotEmpty() -> {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            else -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Movie,
                        contentDescription = "No poster available",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(UIConstants.noPosterIconSize)
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(Spacing.extraSmall),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
