package com.example.bdmi

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val desc: String
) {
    data object Home : BottomNavItem(
        route = "home",
        icon = Icons.Default.Home,
        desc = "home"
    )

    data object Search : BottomNavItem(
        route = "search",
        icon = Icons.Default.Home,
        desc = "search"
    )

    data object Bookmarks : BottomNavItem(
        route = "bookmarks",
        icon = Icons.Default.Home,
        desc = "bookmarks"
    )

    data object Profile : BottomNavItem(
        route = "profile",
        icon = Icons.Default.Home,
        desc = "profile"
    )
}
