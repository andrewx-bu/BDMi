package com.example.bdmi.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Dimens(
    // Spacing
    val small1: Dp,
    val small2: Dp,
    val small3: Dp,
    val medium1: Dp,
    val medium2: Dp,
    val medium3: Dp,
    val large1: Dp,
    val large2: Dp,
    val large3: Dp,

    // Icon sizes
    val iconTiny: Dp,
    val iconSmall: Dp,
    val iconMedium: Dp,
    val iconLarge: Dp,
    val iconHuge: Dp,

    // Button dimensions
    val buttonHeightSmall: Dp,
    val buttonHeightMedium: Dp,
    val buttonHeightLarge: Dp,
    val buttonWidthSmall: Dp,
    val buttonWidthMedium: Dp,
    val buttonWidthLarge: Dp,

    // Logo and images
    val logoSizeSmall: Dp,
    val logoSizeMedium: Dp,
    val logoSizeLarge: Dp,
    val posterSize: Dp,
    val noPosterIconSize: Dp,
    val backdropHeight: Dp,
    val backdropButtonSize: Dp,
    val backdropIconSize: Dp,

    // Specific UI components
    val topBarHeight: Dp,
    val bottomBarHeight: Dp,
    val topBarIconSize: Dp,
    val notificationBadgeSize: Dp,
    val reviewCardHeight: Dp,
    val carouselDotSize: Dp,
    val chipBorderWidth: Dp,

    // Offsets / alignment
    val contentOffset: Dp,
    val posterRowOffset: Dp,
    val midpointSpacer: Dp,
)

// TODO: Add dimens for Compact devices

val MediumDimens = Dimens(
    // Spacing
    small1 = 2.dp,
    small2 = 4.dp,
    small3 = 8.dp,
    medium1 = 10.dp,
    medium2 = 12.dp,
    medium3 = 16.dp,
    large1 = 20.dp,
    large2 = 24.dp,
    large3 = 32.dp,

    // Icon sizes
    iconTiny = 14.dp,
    iconSmall = 20.dp,
    iconMedium = 30.dp,
    iconLarge = 40.dp,
    iconHuge = 50.dp,

    // Button dimensions
    buttonHeightSmall = 30.dp,
    buttonHeightMedium = 40.dp,
    buttonHeightLarge = 56.dp,
    buttonWidthSmall = 100.dp,
    buttonWidthMedium = 150.dp,
    buttonWidthLarge = 200.dp,

    // Logo and images
    logoSizeSmall = 42.dp,
    logoSizeMedium = 75.dp,
    logoSizeLarge = 120.dp,
    posterSize = 225.dp,
    noPosterIconSize = 50.dp,
    backdropHeight = 180.dp,
    backdropButtonSize = 35.dp,
    backdropIconSize = 20.dp,

    // UI components
    topBarHeight = 100.dp,
    bottomBarHeight = 75.dp,
    topBarIconSize = 30.dp,
    notificationBadgeSize = 18.dp,
    reviewCardHeight = 95.dp,
    carouselDotSize = 10.dp,
    chipBorderWidth = 2.dp,

    // Offsets
    contentOffset = 50.dp,
    posterRowOffset = 225.dp,
    midpointSpacer = 180.dp
)

// TODO: Add dimens for Expanded devices (tablet)
val ExpandedDimens = Dimens(
    small1 = 0.dp,
    small2 = 0.dp,
    small3 = 0.dp,
    medium1 = 0.dp,
    medium2 = 0.dp,
    medium3 = 0.dp,
    large1 = 0.dp,
    large2 = 0.dp,
    large3 = 0.dp,
    iconTiny = 0.dp,
    iconSmall = 0.dp,
    iconMedium = 0.dp,
    iconLarge = 0.dp,
    iconHuge = 0.dp,
    buttonHeightSmall = 0.dp,
    buttonHeightMedium = 0.dp,
    buttonHeightLarge = 0.dp,
    buttonWidthSmall = 0.dp,
    buttonWidthMedium = 0.dp,
    buttonWidthLarge = 0.dp,
    logoSizeSmall = 0.dp,
    logoSizeMedium = 0.dp,
    logoSizeLarge = 0.dp,
    posterSize = 0.dp,
    noPosterIconSize = 0.dp,
    backdropHeight = 0.dp,
    backdropButtonSize = 0.dp,
    backdropIconSize = 0.dp,
    topBarHeight = 0.dp,
    bottomBarHeight = 0.dp,
    topBarIconSize = 0.dp,
    notificationBadgeSize = 0.dp,
    reviewCardHeight = 0.dp,
    carouselDotSize = 0.dp,
    chipBorderWidth = 0.dp,
    contentOffset = 0.dp,
    posterRowOffset = 0.dp,
    midpointSpacer = 0.dp,
)