package com.example.bdmi.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
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
import com.example.bdmi.data.utils.VoiceToTextParser
import com.example.bdmi.ui.friends.FriendListScreen
import com.example.bdmi.ui.friends.FriendSearch
import com.example.bdmi.ui.friends.UserProfile
import com.example.bdmi.ui.notifications.NotificationsScreen
import com.example.bdmi.ui.profile.ProfileScreen
import com.example.bdmi.ui.home.HomeScreen
import com.example.bdmi.ui.home.MovieDetailScreen
import com.example.bdmi.ui.home.SearchScreen
import com.example.bdmi.ui.custom_lists.CustomListScreen
import com.example.bdmi.ui.custom_lists.WatchlistsScreen
import com.example.bdmi.ui.home.movie_details.AllReviews
import com.example.bdmi.ui.home.movie_details.PersonDetails
import com.example.bdmi.ui.home.movie_details.GenreMovies
import com.example.bdmi.ui.home.movie_details.StudioDetails
import com.example.bdmi.ui.profile.UserReviews
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
    data object Watchlists : MainRoutes(route = "watchlists")

    @Serializable
    data object Profile : MainRoutes(route = "profile")

    @Serializable
    data object Notifications : MainRoutes(route = "notifications")

    // ImageVector is not serializable so this helper function matches a route name with an ImageVector
    fun getIcon(): ImageVector {
        return when (route) {
            "home" -> Icons.Default.Home
            "search" -> Icons.Default.Search
            "watchlists" -> Icons.Default.Bookmark
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
    voiceToTextParser: VoiceToTextParser
) {
    NavHost(
        navController = navController,
        startDestination = MainRoutes.Home.route,
        route = MainRoutes.Root.route
    ) {
        // Main routes
        composable(MainRoutes.Search.route) {
            SearchScreen(
                voiceToTextParser = voiceToTextParser,
                onMovieClick = { movieId ->
                    navController.navigate("movie_detail/$movieId") {
                        restoreState = true
                    }
                }
            )
        }

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
                navigateToFriends = { userId ->
                    navController.navigate("friends/$userId")
                },
                navigateToReviews = { userId ->
                    navController.navigate("user_reviews/$userId")
                }
            )
        }
        composable(MainRoutes.Notifications.route) {
            NotificationsScreen(
                sessionViewModel = sessionViewModel,
                onNavigateBack = { navController.navigateUp() },
                onProfileClick = { userId ->
                    navController.navigate("user_profile/$userId") {
                        restoreState = true
                    }
                },
                onMovieClick = { movieId ->
                    navController.navigate("movie_detail/$movieId") {
                        restoreState = true
                    }
                }
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
            MovieDetailScreen(
                navController,
                sessionViewModel,
                movieId,
                onMovieClick = { movieId ->
                    navController.navigate("movie_detail/$movieId") {
                        restoreState = true
                    }
                },
                onProfileClick = { userId ->
                    navController.navigate("user_profile/$userId") {
                        restoreState = true
                    }
                },
                // TODO: What does an actor route need
                onActorClick = { navController.navigate("person") },
                onAllReviewsClick = { movieId ->
                    navController.navigate("reviews/$movieId") {
                        restoreState = true
                    }
                }
            )
        }

        composable("reviews/{movieId}") { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId")?.toInt()
            AllReviews(
                sessionViewModel = sessionViewModel,
                movieId = movieId ?: 0,
                onProfileClick = { userId ->
                    navController.navigate("user_profile/$userId") {
                        restoreState = true
                    }
                },
            )
        }

        composable("person/{personId}") { backStackEntry ->
            PersonDetails(
                onNavigateBack = { navController.navigateUp() },
                onMovieClick = { movieId ->
                    navController.navigate("movie_detail/$movieId") {
                        restoreState = true
                    }
                }
            )
        }

        composable("genre/{genreId}") {
            GenreMovies(
                onNavigateBack = { navController.navigateUp() },
                onMovieClick = { movieId ->
                    navController.navigate("movie_detail/$movieId") {
                        restoreState = true
                    }
                }
            )
        }

        composable("studio/{studioId}") {
            StudioDetails(
                onNavigateBack = { navController.navigateUp() },
                onMovieClick = { movieId ->
                    navController.navigate("movie_detail/$movieId") {
                        restoreState = true
                    }
                }
            )
        }

        // Friend Journey
        composable("friends/{userId}") {
            val userId = it.arguments?.getString("userId").toString()
            FriendListScreen(
                userId = userId,
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
                },
                currentUserId = sessionViewModel.userInfo.value?.userId ?: ""
            )
        }

        composable("user_profile/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            UserProfile(
                profileUserId = userId,
                sessionViewModel = sessionViewModel,
                onNavigateToWatchlists = { navController.navigate("watchlists/$userId") },
                onNavigateToFriendList = { navController.navigate("friends/$userId") },
                onNavigateToReviews = {
                    //navController.navigate("user_reviews/$userId")
                }
            )
        }

        // Watchlist journey
        composable(MainRoutes.Watchlists.route) {
            WatchlistsScreen(
                sessionViewModel,
                onListClick = { (userId, listId) ->
                    navController.navigate("watchlist/$userId/$listId")
                }
            )
        }

        composable("watchlists/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            WatchlistsScreen(
                userId = userId,
                onListClick = { (userId, listId) ->
                    navController.navigate("watchlist/$userId/$listId")
                },
                onNavigateBack = { navController.navigateUp() })
        }

        composable("watchlist/{userId}/{listId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val listId = backStackEntry.arguments?.getString("listId") ?: ""
            CustomListScreen(
                currentUserId = sessionViewModel.userInfo.value?.userId ?: "",
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

        // User Reviews
        composable("user_reviews/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            UserReviews(
                sessionViewModel = sessionViewModel,
                userId = userId,
                onNavigateBack = { navController.navigateUp() },
                onMovieClick = { movieId ->
                    navController.navigate("movie_detail/$movieId") {
                        restoreState = true
                    }
                }
            )
        }
    }
}