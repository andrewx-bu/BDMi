package com.example.bdmi.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.spr.jetpack_loading.components.indicators.BallPulseSyncIndicator

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) { BallPulseSyncIndicator(color = MaterialTheme.colorScheme.onPrimaryContainer) }
}