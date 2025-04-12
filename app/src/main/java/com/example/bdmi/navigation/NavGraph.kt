package com.example.bdmi.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.bdmi.ui.onboarding.LoginScreen
import com.example.bdmi.ui.onboarding.RegisterScreen
import com.example.bdmi.ui.onboarding.StartScreen
import com.example.bdmi.ui.screens.BookmarksScreen
import com.example.bdmi.ui.screens.HomeScreen
import com.example.bdmi.ui.screens.NotificationsScreen
import com.example.bdmi.ui.screens.ProfileScreen
import com.example.bdmi.ui.screens.SearchScreen

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
        composable<NavItem.Home> { HomeScreen() }
        composable<NavItem.Search> { SearchScreen() }
        composable<NavItem.Bookmarks> { BookmarksScreen() }
        composable<NavItem.Profile> { ProfileScreen(onLogoutClick = { navController.navigate(StartScreen) }) }
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
    }
}