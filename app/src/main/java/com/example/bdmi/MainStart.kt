package com.example.bdmi

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bdmi.navigation.MainNestedNavGraph
import com.example.bdmi.navigation.MainRoutes
import com.example.bdmi.ui.viewmodels.UserViewModel

@Composable
fun MainScreen(
    userViewModel: UserViewModel,
    darkTheme: Boolean,
    switchTheme: () -> Unit,
) {
    // Keep navigation vars in parent composable
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            // Hide outer top bar if onboarding or moving to notifications
            AnimatedVisibility(
                visible = currentRoute != null && (!currentRoute.contains("MovieDetailScreen")),
                enter = fadeIn() + slideInVertically { -it },
                exit = fadeOut() + slideOutVertically { -it }
            ) {
                TopBar(
                    darkTheme = darkTheme,
                    onThemeClick = switchTheme,
                    onNotificationClick = { navController.navigate(MainRoutes.Notifications) }
                )
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it }
            ) {
                BottomBar(
                    currentRoute = currentRoute,
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
            MainNestedNavGraph(navController = navController, userViewModel = userViewModel)
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

//            IconButton(onClick = onThemeClick) {
//                Icon(
//                    imageVector = if (darkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
//                    contentDescription = "Toggle Theme",
//                    tint = MaterialTheme.colorScheme.primary,
//                    modifier = Modifier.rotate(rotation)
//                )
//            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier.height(75.dp)
    )
}

@Composable
fun BottomBar(currentRoute: String?, onItemClicked: (MainRoutes) -> Unit) {
    val screens = listOf(
        MainRoutes.Home, MainRoutes.Search, MainRoutes.Bookmarks, MainRoutes.Profile
    )

    NavigationBar(
        modifier = Modifier
            .height(75.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        screens.forEach { screen ->
            AddItem(
                screen = screen,
                isSelected = currentRoute == screen.route,
                onItemClicked = onItemClicked
            )
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: MainRoutes,
    isSelected: Boolean,
    onItemClicked: (MainRoutes) -> Unit
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
                contentDescription = screen.route,
                tint = iconColor
            )
        },
        selected = isSelected,
        onClick = { onItemClicked(screen) },
        modifier = Modifier.offset(y = 10.dp)
    )
}
