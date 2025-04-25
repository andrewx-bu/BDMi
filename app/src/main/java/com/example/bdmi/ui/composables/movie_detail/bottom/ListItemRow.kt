package com.example.bdmi.ui.composables.movie_detail.bottom

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.example.bdmi.ui.theme.dimens

@Composable
fun ListItemRow(
    height: Dp,
    onClick: () -> Unit,
    leading: @Composable () -> Unit,
    title: String,
    subtitle: String? = null,
) {
    Row(
        modifier = Modifier
            .height(height)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leading()

        Spacer(Modifier.width(MaterialTheme.dimens.small3))

        Column {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
            subtitle?.let {
                Spacer(Modifier.height(MaterialTheme.dimens.small1))
                Text(text = it, style = MaterialTheme.typography.titleMedium)
            }
        }

        Spacer(Modifier.weight(1f))

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            modifier = Modifier.size(MaterialTheme.dimens.iconMedium)
        )
    }
}