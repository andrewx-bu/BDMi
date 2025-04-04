package com.example.bdmi

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bdmi.navigation.LoginScreen
import com.example.bdmi.navigation.NavGraph
import com.example.bdmi.navigation.NavItem
import com.example.bdmi.navigation.RegisterScreen
import com.example.bdmi.navigation.StartScreen

@Composable
fun MainScreen(
    darkTheme: Boolean,
    loggedIn: Boolean,
    switchTheme: () -> Unit,
) {
    // Keep navigation vars in parent composable
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val routeName = currentRoute?.substringAfterLast('.')
    val onboardingRoutes = listOf(
        StartScreen::class.simpleName,
        LoginScreen::class.simpleName,
        RegisterScreen::class.simpleName
    )
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            // Hide outer top bar if onboarding or moving to notifications
            if (routeName != "notifications" && routeName !in onboardingRoutes) {
                TopBar(
                    darkTheme = darkTheme,
                    onThemeClick = switchTheme,
                    onNotificationClick = { navController.navigate(NavItem.Notifications) }
                )
            }
        },
        bottomBar = {
            // Hide bottom bar if onboarding
            if (routeName !in onboardingRoutes) {
                BottomBar(
                    currentRoute = routeName,
                    onItemClicked = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            NavGraph(navController = navController, loggedIn = loggedIn)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    darkTheme: Boolean,
    onThemeClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (darkTheme) 180f else 0f,
        label = "Theme Rotation"
    )

    TopAppBar(
        title = {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "BDMi",
                modifier = Modifier.size(75.dp)
            )
        },
        actions = {
            IconButton(onClick = onNotificationClick) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = onThemeClick) {
                Icon(
                    imageVector = if (darkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Theme",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.rotate(rotation)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier.height(75.dp)
    )
}

@Composable
fun BottomBar(currentRoute: String?, onItemClicked: (NavItem) -> Unit) {
    val screens = listOf(
        NavItem.Home, NavItem.Search, NavItem.Bookmarks, NavItem.Profile
    )

    NavigationBar(
        modifier = Modifier
            .height(75.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        screens.forEach { screen ->
            AddItem(
                screen,
                isSelected = currentRoute == screen::class.simpleName,
                onItemClicked = onItemClicked
            )
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: NavItem,
    isSelected: Boolean,
    onItemClicked: (NavItem) -> Unit
) {
    val iconColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.tertiary
    }

    NavigationBarItem(
        icon = {
            Icon(
                imageVector = screen.getIcon(),
                contentDescription = screen.iconStr,
                tint = iconColor
            )
        },
        selected = isSelected,
        onClick = { onItemClicked(screen) },
        modifier = Modifier.offset(y = 10.dp)
    )
}

