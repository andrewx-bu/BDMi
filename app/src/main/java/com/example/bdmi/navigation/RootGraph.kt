package com.example.bdmi.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.bdmi.ui.viewmodels.UserViewModel

@Composable
fun RootNavGraph(navController: NavHostController, loggedIn: Boolean, userViewModel: UserViewModel) {
    val startDestination = if (loggedIn) {
        MainRoutes.Root.route
    } else {
        OnboardingRoutes.Root.route
    }
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Onboarding nav graph that includes login and register
        onboardingNavGraph(navController, userViewModel)

        // Main nav graph that includes home, search, bookmarks, profile, and notifications
        mainNavGraph(userViewModel)
    }
}