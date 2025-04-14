package com.example.bdmi.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
sealed class NavItem(val iconStr: String) {
    @Serializable
    data object Home : NavItem(iconStr = "home")
    @Serializable
    data object Search : NavItem(iconStr = "search")
    @Serializable
    data object Bookmarks : NavItem(iconStr = "bookmarks")
    @Serializable
    data object Profile : NavItem(iconStr = "profile")
    @Serializable
    data object Notifications : NavItem(iconStr = "notifications")

    // ImageVector is not serializable so this helper function matches a route name with an ImageVector
    fun getIcon(): ImageVector {
        return when (iconStr) {
            "home" -> Icons.Default.Home
            "search" -> Icons.Default.Search
            "bookmarks" -> Icons.Default.Bookmark
            "profile" -> Icons.Default.AccountCircle
            "notifications" -> Icons.Default.Notifications
            else -> {
                Icons.Default.Accessibility
            }
        }
    }
}

// More recent way of making navigation routes
// https://www.youtube.com/watch?v=8m1W4PyYMYQ&ab_channel=AndroidDevelopers
@Serializable
data object StartScreen
@Serializable
data object LoginScreen
@Serializable
data object RegisterScreen
@Serializable
data object FriendSearchScreen
@Serializable
data class UserProfileScreen(val userId: String)