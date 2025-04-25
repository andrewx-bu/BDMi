package com.example.bdmi.ui.theme

data class UIConstants(
    // Home Screen
    val movieColumns: Int,
    val moviesShown: Int,
    // Images
    val posterAspectRatio: Float,
    val backdropAspectRatio: Float,
    // Movie Detail Screen
    val descriptionMaxLines: Int,
    val reviewMaxLines: Int,
    val reviewScrollDelay: Long,
    val movieRows: Int,
)

val MediumUIConstants = UIConstants(
    movieColumns = 3,
    moviesShown = 18,
    posterAspectRatio = 2f / 3f,
    backdropAspectRatio = 3f / 2f,
    descriptionMaxLines = 3,
    reviewMaxLines = 4,
    reviewScrollDelay = 8000L,
    movieRows = 1
)

// TODO: Add constants for Expanded devices in portrait
val LargeUIConstants = UIConstants(
    movieColumns = 3,
    moviesShown = 18,
    posterAspectRatio = 2f / 3f,
    backdropAspectRatio = 3f / 2f,
    descriptionMaxLines = 3,
    reviewMaxLines = 4,
    reviewScrollDelay = 8000L,
    movieRows = 1
)

// TODO: Add constants for Expanded devices in landscape
val ExpandedUIConstants = UIConstants(
    movieColumns = 3,
    moviesShown = 18,
    posterAspectRatio = 2f / 3f,
    backdropAspectRatio = 3f / 2f,
    descriptionMaxLines = 3,
    reviewMaxLines = 4,
    reviewScrollDelay = 8000L,
    movieRows = 1
)