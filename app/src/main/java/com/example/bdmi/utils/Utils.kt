package com.example.bdmi.utils

import android.app.Activity
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun isDarkTheme(): Boolean {
    val context = LocalContext.current
    val activity = context as? Activity

    val currentNightMode =
        activity?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)

    return currentNightMode == Configuration.UI_MODE_NIGHT_YES
}

