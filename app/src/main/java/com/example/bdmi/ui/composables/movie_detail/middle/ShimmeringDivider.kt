package com.example.bdmi.ui.composables.movie_detail.middle

import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.example.bdmi.ui.theme.dimens
import com.valentinilk.shimmer.LocalShimmerTheme
import com.valentinilk.shimmer.defaultShimmerTheme
import com.valentinilk.shimmer.shimmer

// Horizontal Divider with custom shimmer
@Composable
fun ShimmeringDivider(
    colorA: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    colorB: Color = MaterialTheme.colorScheme.tertiaryContainer,
    height: Dp = dimens.small2,
) {
    val shimmerTheme = defaultShimmerTheme.copy(
        shaderColors = listOf(colorA, colorB, colorA),
        blendMode = BlendMode.SrcOver
    )

    CompositionLocalProvider(LocalShimmerTheme provides shimmerTheme) {
        HorizontalDivider(modifier = Modifier
            .height(height)
            .shimmer())
    }
}
