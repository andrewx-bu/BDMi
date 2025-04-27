package com.example.bdmi.navigation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.bdmi.MainScreen
import com.example.bdmi.SessionViewModel
import com.example.bdmi.ui.theme.AppTheme

@Composable
fun RootNavGraph(
    navController: NavHostController,
    loggedIn: Boolean, sessionViewModel:
    SessionViewModel) {
    val startDestination = if (loggedIn) {
        MainRoutes.Root.route
    } else {
        OnboardingRoutes.Root.route
    }
    val darkTheme = sessionViewModel.darkMode.collectAsState()
    if (isSystemInDarkTheme() && darkTheme.value == false) {
        sessionViewModel.switchTheme()
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Onboarding nav graph that includes login and register
        onboardingNavGraph(navController, sessionViewModel, darkTheme.value)

        // Main nav graph that includes home, search, bookmarks, profile, and notifications
        composable(MainRoutes.Root.route) {
            AppTheme(darkTheme = darkTheme.value) {
                MainScreen(
                    rootNavController = navController,
                    sessionViewModel = sessionViewModel,
                )
            }
        }
    }
}
