package com.example.bdmi.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember

@Composable
fun ProvideAppUtils(
    dimens: Dimens,
    constants: UIConstants,
    content: @Composable () -> Unit
) {
    val appDimens = remember { dimens }
    val uiConstants = remember { constants }

    CompositionLocalProvider(
        LocalAppDimens provides appDimens,
        LocalUIConstants provides uiConstants
    ) {
        content()
    }
}

val LocalAppDimens = compositionLocalOf {
    MediumDimens
}

val LocalUIConstants = compositionLocalOf {
    MediumUIConstants
}