package com.example.bdmi.navigation

import android.util.Log
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
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
import com.example.bdmi.SessionViewModel
import com.example.bdmi.ui.friends.FriendListScreen
import com.example.bdmi.ui.friends.FriendSearch
import com.example.bdmi.ui.friends.UserProfile
import com.example.bdmi.ui.notifications.NotificationsScreen
import com.example.bdmi.ui.screens.HomeScreen
import com.example.bdmi.ui.screens.MovieDetailScreen
import com.example.bdmi.ui.profile.ProfileScreen
import com.example.bdmi.ui.screens.SearchScreen
import com.example.bdmi.ui.custom_lists.CustomListScreen
import com.example.bdmi.ui.custom_lists.WatchlistsScreen
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

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainNestedNavGraph(
    rootNavController: NavHostController,
    navController: NavHostController,
    sessionViewModel: SessionViewModel,
) {
    Log.d("MainNestedNavGraph", "Reached MainNestedNavGraph")

    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = MainRoutes.Home.route,
            route = MainRoutes.Root.route
        ) {
            // Main routes
            composable(MainRoutes.Search.route) { SearchScreen() }
            composable(MainRoutes.Profile.route) {
                ProfileScreen(
                    sessionViewModel = sessionViewModel,
                    onLogoutClick = {
                        rootNavController.navigate(OnboardingRoutes.Root.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    navigateToUserSearch = {
                        navController.navigate("friend_search")
                    },
                    navigateToFriends = {
                        navController.navigate("friends")
                    }
                )
            }
            composable(MainRoutes.Notifications.route) {
                NotificationsScreen(
                    sessionViewModel = sessionViewModel,
                    onNavigateBack = { navController.navigateUp() }
                )
            }

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

            // Movie detail route
            composable("movie_detail/{movieId}") { backStackEntry ->
                val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull() ?: 0
                MovieDetailScreen(navController, sessionViewModel, movieId)
            }

            // Friend Journey
            composable("friends") {
                FriendListScreen(
                    sessionViewModel = sessionViewModel,
                    onNavigateBack = { navController.navigateUp() },
                    onProfileClick = { userId ->
                        navController.navigate("user_profile/$userId") {
                            restoreState = true
                        }
                    }
                )
            }

            composable("friend_search") {
                FriendSearch(
                    onNavigateBack = { navController.navigateUp() },
                    onProfileClick = { userId ->
                        navController.navigate("user_profile/$userId") {
                            restoreState = true
                        }
                    }
                )
            }

            composable("user_profile/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                UserProfile(userId, sessionViewModel)
            }

            // Watchlist journey
            composable(MainRoutes.Bookmarks.route) {
                WatchlistsScreen(sessionViewModel) { (userId, listId) ->
                    Log.d(
                        "WatchlistScreen",
                        "Clicked on watchlist with userId: $userId, listId: $listId"
                    )
                    navController.navigate("watchlist/$userId/$listId")
                }
            }

            composable("watchlist/{userId}/{listId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                val listId = backStackEntry.arguments?.getString("listId") ?: ""
                Log.d(
                    "WatchlistScreen",
                    "Loading CustomListScreen with userId: $userId, listId: $listId"
                )
                CustomListScreen(
                    sessionViewModel = sessionViewModel,
                    listId = listId,
                    userId = userId,
                    onMovieClick = { movieId ->
                        navController.navigate("movie_detail/$movieId") {
                            launchSingleTop = true
                        }
                    },
                    onNavigateBack = { navController.navigateUp() }
                )
            }
        }
    }
}