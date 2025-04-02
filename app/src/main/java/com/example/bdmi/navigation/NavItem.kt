package com.example.bdmi.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavItem(
    val route: String,
    val icon: ImageVector,
    val desc: String
) {
    data object Home : NavItem(
        route = "home",
        icon = Icons.Default.Home,
        desc = "Home"
    )

    data object Search : NavItem(
        route = "search",
        icon = Icons.Default.Search,
        desc = "Search"
    )

    data object Bookmarks : NavItem(
        route = "bookmarks",
        icon = Icons.Default.Bookmark,
        desc = "Bookmarks"
    )

    data object Profile : NavItem(
        route = "profile",
        icon = Icons.Default.AccountCircle,
        desc = "Profile"
    )

    data object Notifications : NavItem(
        route = "notifications",
        icon = Icons.Default.Notifications,
        desc = "Notifications"
    )
}
