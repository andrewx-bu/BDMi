package com.example.bdmi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bdmi.bottomNavBar.BottomNavGraph
import com.example.bdmi.bottomNavBar.BottomNavItem
import com.example.bdmi.utils.isDarkTheme

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        bottomBar = { BottomBar(navController = navController) }) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            BottomNavGraph(navController = navController)
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    val screens = listOf(
        BottomNavItem.Home, BottomNavItem.Search, BottomNavItem.Bookmarks, BottomNavItem.Profile
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val destination = navBackStackEntry?.destination
    val backgroundColor = if (isDarkTheme()) {
        Color.Black
    } else {
        Color.Gray
    }

    NavigationBar(
        modifier = Modifier
            .height(90.dp)
            .offset(y = 15.dp), containerColor = backgroundColor
    ) {
        screens.forEach { screen ->
            AddItem(screen, destination, navController)
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomNavItem, destination: NavDestination?, navController: NavHostController
) {
    NavigationBarItem(
        icon = { Icon(imageVector = screen.icon, contentDescription = screen.desc) },
        selected = destination?.route == screen.route,
        onClick = {
            navController.navigate(screen.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    )
}