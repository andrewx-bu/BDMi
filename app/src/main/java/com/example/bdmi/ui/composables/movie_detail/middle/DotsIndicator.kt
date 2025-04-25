package com.example.bdmi.ui.composables.movie_detail.middle

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.bdmi.ui.theme.dimens

@Composable
fun DotsIndicator(numDots: Int, currentIndex: Int, onDotClick: (Int) -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.align(Alignment.Center),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until numDots) {
                Dot(index = i, isSelected = i == currentIndex, onDotClick)
                if (i != numDots - 1) {
                    Spacer(modifier = Modifier.width(MaterialTheme.dimens.medium3))
                }
            }
        }

        Button(
            onClick = { /* TODO: Move to Reviews Screen */ },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(MaterialTheme.dimens.buttonWidthSmall)
                .height(MaterialTheme.dimens.buttonHeightSmall),
            contentPadding = PaddingValues(horizontal = MaterialTheme.dimens.small1),
            shape = RoundedCornerShape(MaterialTheme.dimens.medium2),
            colors = ButtonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.errorContainer,
                disabledContentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Text(text = "See More")
        }
    }
}

@Composable
fun Dot(index: Int, isSelected: Boolean, onDotClick: (Int) -> Unit) {
    // Transform selected dot
    val baseSize = MaterialTheme.dimens.carouselDotSize
    val width by animateDpAsState(targetValue = if (isSelected) baseSize * 3f else baseSize)
    val height by animateDpAsState(targetValue = if (isSelected) baseSize * 0.8f else baseSize)

    val color = if (isSelected) MaterialTheme.colorScheme.tertiaryContainer
    else MaterialTheme.colorScheme.secondaryContainer

    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .background(color = color, shape = CircleShape)
            .clickable { onDotClick(index) }
    )
}
