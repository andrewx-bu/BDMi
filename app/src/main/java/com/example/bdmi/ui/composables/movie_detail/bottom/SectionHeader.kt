package com.example.bdmi.ui.composables.movie_detail.bottom

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.bdmi.ui.theme.dimens

@Composable
fun SectionHeader(title: String) {
    Row(
        modifier = Modifier.padding(vertical = dimens.small3),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            Modifier.weight(0.25f),
            color = MaterialTheme.colorScheme.tertiaryContainer
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(dimens.small3)
        )
        HorizontalDivider(
            Modifier.weight(1f),
            color = MaterialTheme.colorScheme.tertiaryContainer
        )
    }
}
