package com.example.bdmi.navigation

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
import com.example.bdmi.ui.profile.UserProfile
import com.example.bdmi.ui.notifications.NotificationsScreen
import com.example.bdmi.ui.home.HomeScreen
import com.example.bdmi.ui.home.MovieDetailScreen
import com.example.bdmi.ui.home.SearchScreen
import com.example.bdmi.ui.custom_lists.CustomListScreen
import com.example.bdmi.ui.custom_lists.WatchlistsScreen
import com.example.bdmi.ui.home.movie_details.AllReviews
import com.example.bdmi.ui.home.movie_details.CountryMovies
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
    data object Profile : MainRoutes(route = "user_profile")

    @Serializable
    data object Notifications : MainRoutes(route = "notifications")

    // ImageVector is not serializable so this helper function matches a route name with an ImageVector
    fun getIcon(): ImageVector {
        return when (route) {
            "home" -> Icons.Default.Home
            "search" -> Icons.Default.Search
            "watchlists" -> Icons.Default.Bookmark
            "user_profile" -> Icons.Default.AccountCircle
            "notifications" -> Icons.Default.Notifications
            else -> {
                Icons.Default.Accessibility
            }
        }
    }
}

@Composable
fun MainNestedNavGraph(
    rootNavController: NavHostController,
    navController: NavHostController,
    sessionViewModel: SessionViewModel,
    voiceToTextParser: VoiceToTextParser,
    showFilters: Boolean,
    onShowFiltersChanged: (Boolean) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = MainRoutes.Home.route,
        route = MainRoutes.Root.route
    ) {
        // Main routes
        composable(MainRoutes.Notifications.route) {
            NotificationsScreen(
                sessionViewModel = sessionViewModel,
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
            UserProfile(
                sessionViewModel = sessionViewModel,
                onLogoutClick = {
                    rootNavController.navigate(OnboardingRoutes.Root.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToFriendList = { userId ->
                    navController.navigate("friends/$userId")
                },
                onNavigateToWatchlists = { userId ->
                    navController.navigate("watchlists/$userId")
                },
                onNavigateToReviews = { userId ->
                    navController.navigate("user_reviews/$userId")
                },
                onMovieClick = { movieId ->
                    navController.navigate("movie_detail/$movieId") {
                        restoreState = true
                    }
                }
            )
        }

        composable("user_profile/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            UserProfile(
                profileUserId = userId,
                sessionViewModel = sessionViewModel,
                onNavigateToWatchlists = {
                    navController.navigate("watchlists/$userId")
                },
                onNavigateToFriendList = {
                    navController.navigate("friends/$userId")
                },
                onNavigateToReviews = {
                    navController.navigate("user_reviews/$userId")
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
                onAllReviewsClick = { movieId ->
                    navController.navigate("reviews/$movieId") {
                        restoreState = true
                    }
                },
                onPersonClick = { personID -> navController.navigate("person/$personID") },
                onGenreClick = { genreID -> navController.navigate("genre/$genreID") },
                onStudioClick = { studioID -> navController.navigate("studio/$studioID") },
                onCountryClick = { countryName -> navController.navigate("country/$countryName") }
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

        composable("person/{personId}") {
            PersonDetails(
                navController,
                onMovieClick = { movieId ->
                    navController.navigate("movie_detail/$movieId") {
                        restoreState = true
                    }
                },
                showFilters = showFilters,
                onShowFiltersChanged = onShowFiltersChanged
            )
        }

        composable("genre/{genreId}") {
            GenreMovies(
                navController,
                onMovieClick = { movieId ->
                    navController.navigate("movie_detail/$movieId") {
                        restoreState = true
                    }
                },
                showFilters = showFilters,
                onShowFiltersChanged = onShowFiltersChanged
            )
        }

        composable("studio/{studioId}") {
            StudioDetails(
                navController,
                onMovieClick = { movieId ->
                    navController.navigate("movie_detail/$movieId") {
                        restoreState = true
                    }
                },
                showFilters = showFilters,
                onShowFiltersChanged = onShowFiltersChanged
            )
        }

        composable("country/{countryCode}") {
            CountryMovies(
                navController,
                onMovieClick = { movieId ->
                    navController.navigate("movie_detail/$movieId") {
                        restoreState = true
                    }
                },
                showFilters = showFilters,
                onShowFiltersChanged = onShowFiltersChanged
            )
        }

        // Friend Journey
        composable("friends/{userId}") {
            val userId = it.arguments?.getString("userId").toString()
            FriendListScreen(
                userId = userId,
                onProfileClick = { userId ->
                    navController.navigate("user_profile/$userId") {
                        restoreState = true
                    }
                }
            )
        }

        composable("friend_search") {
            FriendSearch(
                onProfileClick = { userId ->
                    navController.navigate("user_profile/$userId") {
                        restoreState = true
                    }
                },
                currentUserId = sessionViewModel.userInfo.value?.userId ?: ""
            )
        }

        // Watchlist journey
        composable(MainRoutes.Watchlists.route) {
            WatchlistsScreen(
                sessionViewModel,
                loggedInUserId = sessionViewModel.userInfo.value?.userId ?: "",
                onListClick = { (userId, listId) ->
                    navController.navigate("watchlist/$userId/$listId")
                }
            )
        }

        composable("watchlists/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            WatchlistsScreen(
                userId = userId,
                loggedInUserId = sessionViewModel.userInfo.value?.userId ?: "",
                onListClick = { (userId, listId) ->
                    navController.navigate("watchlist/$userId/$listId")
                }
            )
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
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // User Reviews
        composable("user_reviews/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            UserReviews(
                userId = userId,
                onMovieClick = { movieId ->
                    navController.navigate("movie_detail/$movieId") {
                        restoreState = true
                    }
                }
            )
        }
    }
}