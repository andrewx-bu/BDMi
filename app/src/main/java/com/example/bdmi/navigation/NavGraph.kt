package com.example.bdmi.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.bdmi.onboarding_screens.LoginScreen
import com.example.bdmi.onboarding_screens.RegisterScreen
import com.example.bdmi.onboarding_screens.StartScreen
import com.example.bdmi.screens.BookmarksScreen
import com.example.bdmi.screens.HomeScreen
import com.example.bdmi.screens.NotificationsScreen
import com.example.bdmi.screens.ProfileScreen
import com.example.bdmi.screens.SearchScreen

// Bottom Bar Navigation basics: https://www.youtube.com/watch?v=gg-KBGH9T8s
@Composable
fun NavGraph(navController: NavHostController, loggedIn: Boolean) {
    val startDestination = if (loggedIn) {
        NavItem.Home.route
    } else {
        StartScreen.route
    }
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = NavItem.Home.route) { HomeScreen() }
        composable(route = NavItem.Search.route) { SearchScreen() }
        composable(route = NavItem.Bookmarks.route) { BookmarksScreen() }
        composable(route = NavItem.Profile.route) { ProfileScreen() }
        composable(route = NavItem.Notifications.route) {
            NotificationsScreen(onNavigateBack = { navController.navigateUp() })
        }
        composable(route = "start") {
            StartScreen(
                onLoginClick = { navController.navigate("login") },
                onRegisterClick = { navController.navigate("register") }
            )
        }
        composable(route = "login") {
            LoginScreen(
                onLoginClick = { navController.navigate(NavItem.Home.route) }
            )
        }
        composable(route = "register") {
            RegisterScreen(
                onRegisterClick = { navController.navigate(NavItem.Home.route) }
            )
        }
    }
}