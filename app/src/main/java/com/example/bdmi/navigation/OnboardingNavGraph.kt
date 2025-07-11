package com.example.bdmi.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.bdmi.SessionViewModel
import com.example.bdmi.ui.onboarding.LoginScreen
import com.example.bdmi.ui.onboarding.RegisterScreen
import com.example.bdmi.ui.onboarding.StartScreen
import com.example.bdmi.ui.theme.AppTheme
import kotlinx.serialization.Serializable

@Serializable
sealed class OnboardingRoutes(val route: String) {
    @Serializable
    data object Root : OnboardingRoutes("onboarding")

    @Serializable
    data object Start : OnboardingRoutes("start")

    @Serializable
    data object Login : OnboardingRoutes("login")

    @Serializable
    data object Register : OnboardingRoutes("register")
}

fun NavGraphBuilder.onboardingNavGraph(
    navController: NavHostController,
    sessionViewModel: SessionViewModel,
    darkTheme: Boolean
) {
    navigation(
        startDestination = OnboardingRoutes.Start.route,
        route = OnboardingRoutes.Root.route
    ) {
        composable(OnboardingRoutes.Start.route) {
            AppTheme(darkTheme = darkTheme) {
                StartScreen(
                    onLoginClick = { navController.navigate(OnboardingRoutes.Login.route) },
                    onRegisterClick = { navController.navigate(OnboardingRoutes.Register.route) }
                )
            }
        }

        composable(OnboardingRoutes.Login.route) {
            AppTheme(darkTheme = darkTheme) {
                LoginScreen(
                    sessionViewModel = sessionViewModel,
                    onLoginClick = {
                        navController.navigate(MainRoutes.Root.route) {
                            popUpTo(OnboardingRoutes.Root.route) { inclusive = true }
                        }
                    }
                )
            }

        }

        composable(OnboardingRoutes.Register.route) {
            AppTheme(darkTheme = darkTheme) {
                RegisterScreen(
                    sessionViewModel = sessionViewModel,
                    onRegisterClick = {
                        navController.navigate(MainRoutes.Root.route) {
                            popUpTo(OnboardingRoutes.Root.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}