package com.example.bdmi.ui.composables.movie_detail.top

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.example.bdmi.ui.theme.dimens

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

