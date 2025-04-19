package com.example.bdmi.ui.theme

import android.widget.Scroller

data class UIConstants(
    // Home Screen
    val movieColumns: Int,
    val moviesShown: Int,
    // Images
    val posterAspectRatio: Float,
    val backdropAspectRatio: Float,
    // Movie Detail Screen
    val reviewMaxLines: Int,
    val reviewScrollDelay: Long
)

// TODO: Add constants for Compact devices

val MediumUIConstants = UIConstants(
    movieColumns = 3,
    moviesShown = 18,
    posterAspectRatio = 2f / 3f,
    backdropAspectRatio = 3f / 2f,
    reviewMaxLines = 3,
    reviewScrollDelay = 8000L,
)

// TODO: Add constants for Expanded devices (tablet)
val ExpandedUIConstants = UIConstants(
    movieColumns = 3,
    moviesShown = 18,
    posterAspectRatio = 2f / 3f,
    backdropAspectRatio = 3f / 2f,
    reviewMaxLines = 3,
    reviewScrollDelay = 8000L,
)