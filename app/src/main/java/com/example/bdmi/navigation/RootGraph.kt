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
import com.example.bdmi.SessionViewModel
import com.example.bdmi.ui.theme.AppTheme

@Composable
fun RootNavGraph(navController: NavHostController, loggedIn: Boolean, sessionViewModel: SessionViewModel) {
    val startDestination = if (loggedIn) {
        MainRoutes.Root.route
    } else {
        OnboardingRoutes.Root.route
    }
    // TODO: Implement dark theme into preferences
    val systemDark = isSystemInDarkTheme()
    var darkTheme by remember { mutableStateOf(systemDark) }
    //Force dark theme:
    //var darkTheme by remember { mutableStateOf(true) }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Onboarding nav graph that includes login and register
        onboardingNavGraph(navController, sessionViewModel)

        // Main nav graph that includes home, search, bookmarks, profile, and notifications
        //mainNavGraph(userViewModel)
        composable(MainRoutes.Root.route) {
            AppTheme(darkTheme = darkTheme) {
                MainScreen(
                    rootNavController = navController,
                    sessionViewModel = sessionViewModel,
                    darkTheme = darkTheme,
                    switchTheme = { darkTheme = !darkTheme }
                )
            }
        }
    }
}
