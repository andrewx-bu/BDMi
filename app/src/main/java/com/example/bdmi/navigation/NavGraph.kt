package com.example.bdmi.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.bdmi.screens.BookmarksScreen
import com.example.bdmi.screens.HomeScreen
import com.example.bdmi.screens.NotificationsScreen
import com.example.bdmi.screens.ProfileScreen
import com.example.bdmi.screens.SearchScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavItem.Home.route
    ) {
        composable(route = NavItem.Home.route) {
            HomeScreen()
        }
        composable(route = NavItem.Search.route) {
            SearchScreen()
        }
        composable(route = NavItem.Bookmarks.route) {
            BookmarksScreen()
        }
        composable(route = NavItem.Profile.route) {
            ProfileScreen()
        }
        composable(route = "notifications") {
            NotificationsScreen(navController)
        }
    }
}