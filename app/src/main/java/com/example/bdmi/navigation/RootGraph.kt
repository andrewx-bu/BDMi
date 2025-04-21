package com.example.bdmi.navigation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.bdmi.MainScreen
import com.example.bdmi.ui.theme.AppTheme
import com.example.bdmi.UserViewModel

@Composable
fun RootNavGraph(navController: NavHostController, loggedIn: Boolean, userViewModel: UserViewModel) {
    val startDestination = if (loggedIn) {
        MainRoutes.Root.route
    } else {
        OnboardingRoutes.Root.route
    }
    val systemDark = isSystemInDarkTheme()
    //var darkTheme by remember { mutableStateOf(systemDark) }
    var darkTheme by remember { mutableStateOf(true) }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Onboarding nav graph that includes login and register
        onboardingNavGraph(navController, userViewModel)

        // Main nav graph that includes home, search, bookmarks, profile, and notifications
        //mainNavGraph(userViewModel)
        composable(MainRoutes.Root.route) {
            AppTheme(darkTheme = darkTheme) {
                MainScreen(
                    rootNavController = navController,
                    userViewModel = userViewModel,
                    darkTheme = darkTheme,
                    switchTheme = { darkTheme = !darkTheme }
                )
            }
        }
    }
}
