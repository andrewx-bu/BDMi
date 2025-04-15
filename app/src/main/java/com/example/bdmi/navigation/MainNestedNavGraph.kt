package com.example.bdmi.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.bdmi.ui.notifications.NotificationsScreen
import com.example.bdmi.ui.screens.BookmarksScreen
import com.example.bdmi.ui.screens.HomeScreen
import com.example.bdmi.ui.screens.ProfileScreen
import com.example.bdmi.ui.screens.SearchScreen
import com.example.bdmi.ui.viewmodels.UserViewModel

@Composable
fun MainNestedNavGraph(navController: NavHostController, userViewModel: UserViewModel) {
    Log.d("MainNestedNavGraph", "Reached MainNestedNavGraph")

    NavHost(
        navController = navController,
        startDestination = MainRoutes.Home.route
    ) {
        composable(MainRoutes.Home.route) {
            HomeScreen(
                onMovieClick = { movieId ->
                    navController.navigate("movie_detail/$movieId")
                }
            )
        }
        composable(MainRoutes.Search.route) { SearchScreen() }
        composable(MainRoutes.Bookmarks.route) { BookmarksScreen() }
        composable(MainRoutes.Profile.route) {
            ProfileScreen(
                onLogoutClick = {
                    navController.navigate(OnboardingRoutes.Start.route) {
                        popUpTo(MainRoutes.Root.route) { inclusive = true }
                    }
                },
                navigateToUserSearch = {
                    navController.navigate("friend_search")
                }
            )
        }
        composable(MainRoutes.Notifications.route) {
            NotificationsScreen(onNavigateBack = { navController.navigateUp() })
        }



//        // Deeper screens can go here too
//        composable("movie_detail/{movieId}") { backStackEntry ->
//            val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull() ?: 0
//            MovieDetailScreen(movieId) { navController.popBackStack() }
//        }
//
//        composable("friend_search") {
//            FriendSearch { userId ->
//                navController.navigate("user_profile/$userId")
//            }
//        }
//
//        composable("user_profile/{userId}") { backStackEntry ->
//            val userId = backStackEntry.arguments?.getString("userId") ?: ""
//            UserProfile(userId)
//        }
    }
}