package com.example.bdmi

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.bdmi.screens.BookmarksScreen
import com.example.bdmi.screens.HomeScreen
import com.example.bdmi.screens.ProfileScreen
import com.example.bdmi.screens.SearchScreen

@Composable
fun BottomNavGraph(navController : NavHostController) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route
    ) {
        composable(route = BottomNavItem.Home.route) {
            HomeScreen()
        }
        composable(route = BottomNavItem.Search.route) {
            SearchScreen()
        }
        composable(route = BottomNavItem.Bookmarks.route) {
            BookmarksScreen()
        }
        composable(route = BottomNavItem.Profile.route) {
            ProfileScreen()
        }
    }
}