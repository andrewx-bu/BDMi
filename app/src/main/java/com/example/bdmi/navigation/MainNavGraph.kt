package com.example.bdmi.navigation

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.bdmi.ui.friends.FriendSearch
import com.example.bdmi.ui.friends.UserProfile
import com.example.bdmi.ui.notifications.NotificationsScreen
import com.example.bdmi.ui.screens.BookmarksScreen
import com.example.bdmi.ui.screens.HomeScreen
import com.example.bdmi.ui.screens.MovieDetailScreen
import com.example.bdmi.ui.screens.ProfileScreen
import com.example.bdmi.ui.screens.SearchScreen
import com.example.bdmi.ui.viewmodels.UserViewModel
import kotlinx.serialization.Serializable

@Serializable
sealed class MainRoutes(val route: String) {
    // Root route for the main graph
    @Serializable
    data object Root : MainRoutes(route = "root")

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

@Composable
fun MainNestedNavGraph(rootNavController: NavHostController, navController: NavHostController, userViewModel: UserViewModel) {
    Log.d("MainNestedNavGraph", "Reached MainNestedNavGraph")

    NavHost(
        navController = navController,
        startDestination = MainRoutes.Home.route,
        route = MainRoutes.Root.route
    ) {
        composable(MainRoutes.Home.route) {
            HomeScreen(
                onMovieClick = { movieId ->
                    navController.navigate("movie_detail/$movieId") {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable(MainRoutes.Search.route) { SearchScreen() }
        composable(MainRoutes.Bookmarks.route) { BookmarksScreen() }
        composable(MainRoutes.Profile.route) {
            ProfileScreen(
                userViewModel = userViewModel,
                onLogoutClick = {
                    rootNavController.navigate(OnboardingRoutes.Root.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                navigateToUserSearch = {
                    navController.navigate("friend_search")
                }
            )
        }
        composable(MainRoutes.Notifications.route) {
            NotificationsScreen(
                userViewModel = userViewModel,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        // Deeper screens can go here too
        composable("movie_detail/{movieId}") { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull() ?: 0
            MovieDetailScreen(movieId) { navController.popBackStack() }
        }

        composable("friend_search") {
            FriendSearch { userId ->
                navController.navigate("user_profile/$userId") {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    restoreState = true
                }
            }
        }

        composable("user_profile/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            UserProfile(userId, userViewModel)
        }
    }
}