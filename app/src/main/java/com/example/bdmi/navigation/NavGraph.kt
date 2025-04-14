package com.example.bdmi.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.bdmi.ui.friends.FriendSearch
import com.example.bdmi.ui.friends.UserProfile
import com.example.bdmi.ui.onboarding.LoginScreen
import com.example.bdmi.ui.onboarding.RegisterScreen
import com.example.bdmi.ui.onboarding.StartScreen
import com.example.bdmi.ui.screens.BookmarksScreen
import com.example.bdmi.ui.screens.HomeScreen
import com.example.bdmi.ui.notifications.NotificationsScreen
import com.example.bdmi.ui.screens.ProfileScreen
import com.example.bdmi.ui.screens.SearchScreen
import com.example.bdmi.ui.screens.MovieDetailScreen

// Bottom Bar Navigation basics: https://www.youtube.com/watch?v=gg-KBGH9T8s
@Composable
fun NavGraph(navController: NavHostController, loggedIn: Boolean) {
    val startDestination = if (loggedIn) {
        NavItem.Home
    } else {
        StartScreen
    }
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<NavItem.Home> {
            HomeScreen(
                onMovieClick = { movieId ->
                    navController.navigate(MovieDetailScreen(movieId))
                }
            )
        }
        composable<NavItem.Search> { SearchScreen() }
        composable<NavItem.Bookmarks> { BookmarksScreen() }
        composable<NavItem.Profile> {
            ProfileScreen(
                onLogoutClick = { navController.navigate(StartScreen) },
                navigateToUserSearch = { navController.navigate(FriendSearchScreen) }
            )
        }
        composable<NavItem.Notifications> {
            NotificationsScreen(onNavigateBack = { navController.navigateUp() })
        }
        composable<StartScreen> {
            StartScreen(
                onLoginClick = { navController.navigate(LoginScreen) },
                onRegisterClick = { navController.navigate(RegisterScreen) }
            )
        }
        composable<LoginScreen> {
            LoginScreen(
                onLoginClick = { navController.navigate(NavItem.Home) }
            )
        }
        composable<RegisterScreen> {
            RegisterScreen(
                onRegisterClick = { navController.navigate(NavItem.Home) }
            )
        }
        composable<FriendSearchScreen> {
            FriendSearch() { userId ->
                navController.navigate(UserProfileScreen(userId))
            }
        }
        composable<UserProfileScreen> { backStackEntry ->
            val screen = backStackEntry.toRoute<UserProfileScreen>()
            UserProfile(userId = screen.userId)

        composable<MovieDetailScreen> { backStackEntry ->
            val movieId = backStackEntry.toRoute<MovieDetailScreen>().movieId
            MovieDetailScreen(
                movieId = movieId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}