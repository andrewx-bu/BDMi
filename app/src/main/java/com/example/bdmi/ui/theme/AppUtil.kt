package com.example.bdmi.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration

// Guide from https://www.youtube.com/watch?v=Dj_X-RKL-c0
@Composable
fun ProvideAppUtils(
    dimens: Dimens,
    content: @Composable () -> Unit,
) {
    val appDimens = remember { dimens }
    CompositionLocalProvider(LocalAppDimens provides appDimens) {
        content()
    }
}

val LocalAppDimens = compositionLocalOf {
    MediumDimens
}

val ScreenOrientation
    @Composable
    get() = LocalConfiguration.current.orientation