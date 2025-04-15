package com.example.bdmi.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.bdmi.MainScreen
import com.example.bdmi.ui.viewmodels.UserViewModel
import kotlinx.serialization.Serializable

@Serializable
sealed class MainRoutes(val route: String) {
    @Serializable
    data object Root : MainRoutes(route = "root")

    // Main screen
    @Serializable
    data object Main : MainRoutes(route = "main")

    // Main routes in the bottom and top bars
    @Serializable
    data object Home : MainRoutes(route = "home")
    @Serializable
    data object Search : MainRoutes(route = "search")
    @Serializable
    data object Bookmarks : MainRoutes(route = "bookmarks")
    @Serializable
    data object Profile : MainRoutes(route = "profile")
    @Serializable
    data object Notifications : MainRoutes(route = "notifications")

    // ImageVector is not serializable so this helper function matches a route name with an ImageVector
    fun getIcon(): ImageVector {
        return when (route) {
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

fun NavGraphBuilder.mainNavGraph(userViewModel: UserViewModel) {
    navigation(
        startDestination = MainRoutes.Main.route,
        route = MainRoutes.Root.route
    ) {
        composable(MainRoutes.Main.route) {
            MainScreen(
                userViewModel = userViewModel,
                darkTheme = true,
                switchTheme = {  }
            )
        }
//        composable(MainRoutes.Home.route) {
//            HomeScreen(
//                onMovieClick = { movieId ->
//                    navController.navigate(MovieDetailScreen(movieId))
//                }
//            )
//        }
//        composable(MainRoutes.Search.route) { SearchScreen() }
//
//        composable(MainRoutes.Bookmarks.route) { BookmarksScreen() }
//        composable(MainRoutes.Profile.route) {
//            ProfileScreen(
//                onLogoutClick = {
//                    navController.navigate(OnboardingRoutes.Start.route) {
//                        popUpTo(MainRoutes.Main.route)
//                    }
//                },
//                navigateToUserSearch = { navController.navigate(FriendSearchScreen) }
//            )
//        }
//        composable(MainRoutes.Notifications.route) {
//            NotificationsScreen(onNavigateBack = { navController.navigateUp() })
//        }
    }
}