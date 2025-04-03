package com.example.bdmi.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
sealed class NavItem(val route: String) {
    @Serializable
    data object Home : NavItem(route = "home")

    @Serializable
    data object Search : NavItem(route = "search")

    @Serializable
    data object Bookmarks : NavItem(route = "bookmarks")

    @Serializable
    data object Profile : NavItem(route = "profile")

    @Serializable
    data object Notifications : NavItem(route = "notifications")

    //ImageVector is not serializable so this helper function matches a route name with an ImageVector
    fun getIcon(): ImageVector {
        return when (route) {
            "home" -> Icons.Default.Home
            "search" -> Icons.Default.Search
            "bookmarks" -> Icons.Default.Bookmark
            "profile" -> Icons.Default.AccountCircle
            "notifications" -> Icons.Default.Notifications
            else -> {Icons.Default.Accessibility}
        }
    }
}

//More recent way of making navigation routes
//https://www.youtube.com/watch?v=8m1W4PyYMYQ&ab_channel=AndroidDevelopers
@Serializable
data object StartScreen {
    const val route = "start"
}

@Serializable
data object LoginScreen {
    const val route = "login"
}

@Serializable
data object RegisterScreen {
    const val route = "register"
}