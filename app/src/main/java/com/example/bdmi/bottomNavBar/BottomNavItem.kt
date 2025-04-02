package com.example.bdmi.bottomNavBar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val desc: String
) {
    data object Home : BottomNavItem(
        route = "home",
        icon = Icons.Default.Home,
        desc = "Home"
    )

    data object Search : BottomNavItem(
        route = "search",
        icon = Icons.Default.Search,
        desc = "Search"
    )

    data object Bookmarks : BottomNavItem(
        route = "bookmarks",
        icon = Icons.Default.Bookmark,
        desc = "Bookmarks"
    )

    data object Profile : BottomNavItem(
        route = "profile",
        icon = Icons.Default.AccountCircle,
        desc = "Profile"
    )
}
