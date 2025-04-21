package com.example.bdmi

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bdmi.navigation.MainNestedNavGraph
import com.example.bdmi.navigation.MainRoutes
import com.example.bdmi.ui.theme.dimens

@Composable
fun MainScreen(
    rootNavController: NavHostController,
    navController: NavHostController = rememberNavController(),
    userViewModel: UserViewModel,
    darkTheme: Boolean,
    switchTheme: () -> Unit,
) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            // Hide outer top bar if onboarding or moving to full screen
            AnimatedVisibility(
                // TODO: More elegant route checking
                visible = currentRoute != null && (!currentRoute.contains("movie_detail")),
                enter = fadeIn() + slideInVertically { -it },
                exit = fadeOut() + slideOutVertically { -it }
            ) {
                TopBar(
                    darkTheme = darkTheme,
                    onThemeClick = switchTheme,
                    onNotificationClick = {
                        navController.navigate(MainRoutes.Notifications.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
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
                    onItemClicked = { route: String ->
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
        Box(
            modifier = Modifier
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            MainNestedNavGraph(
                rootNavController = rootNavController,
                navController = navController,
                userViewModel = userViewModel
            )
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

    // TODO: Top Bar States
    TopAppBar(
        title = {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "BDMi",
                modifier = Modifier
                    .size(MaterialTheme.dimens.logoSizeMedium)
                    .clip(RoundedCornerShape(MaterialTheme.dimens.small3))
            )
        },
        actions = {
            BadgedBox(
                badge = {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier
                            .size(MaterialTheme.dimens.notificationBadgeSize)
                            .offset(
                                x = -MaterialTheme.dimens.small3,
                                y = MaterialTheme.dimens.small2
                            )
                    ) {
                        // TODO: Implement Notifications #
                        Text("9")
                    }
                }
            ) {
                IconButton(onClick = onNotificationClick) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(MaterialTheme.dimens.iconMedium)
                    )
                }
            }
            Spacer(Modifier.width(MaterialTheme.dimens.medium3))

            IconButton(onClick = onThemeClick) {
                Icon(
                    imageVector = if (darkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Theme",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .rotate(rotation)
                        .size(MaterialTheme.dimens.iconMedium)
                )
            }

            Spacer(Modifier.width(MaterialTheme.dimens.medium3))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier.height(MaterialTheme.dimens.topBarHeight)
    )
}

@Composable
fun BottomBar(currentRoute: String?, onItemClicked: (String) -> Unit) {
    val screens = listOf(
        MainRoutes.Home,
        MainRoutes.Search,
        MainRoutes.Bookmarks,
        MainRoutes.Profile
    )

    NavigationBar(
        modifier = Modifier.height(MaterialTheme.dimens.bottomBarHeight),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
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
    onItemClicked: (String) -> Unit
) {
    val iconColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.secondary
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
        onClick = { onItemClicked(screen.route) },
        modifier = Modifier.offset(y = MaterialTheme.dimens.small2)
    )
}
