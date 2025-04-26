package com.example.bdmi.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.bdmi.SessionViewModel
import com.example.bdmi.ui.onboarding.LoginScreen
import com.example.bdmi.ui.onboarding.RegisterScreen
import com.example.bdmi.ui.onboarding.StartScreen
import kotlinx.serialization.Serializable

@Serializable
sealed class OnboardingRoutes(val route: String) {
    @Serializable
    object Root : OnboardingRoutes("onboarding")
    @Serializable
    object Start : OnboardingRoutes("start")
    @Serializable
    object Login : OnboardingRoutes("login")
    @Serializable
    object Register : OnboardingRoutes("register")
}

fun NavGraphBuilder.onboardingNavGraph(navController: NavHostController, sessionViewModel: SessionViewModel) {
    navigation(
        startDestination = OnboardingRoutes.Start.route,
        route = OnboardingRoutes.Root.route
    ) {
        composable(OnboardingRoutes.Start.route) {
            StartScreen(
                onLoginClick = { navController.navigate(OnboardingRoutes.Login.route) },
                onRegisterClick = { navController.navigate(OnboardingRoutes.Register.route) }
            )
        }

        composable(OnboardingRoutes.Login.route) {
            LoginScreen(
                sessionViewModel = sessionViewModel,
                onLoginClick = {
                    navController.navigate(MainRoutes.Root.route) {
                        popUpTo(OnboardingRoutes.Root.route) { inclusive = true }
                    }
                }
            )
        }

        composable(OnboardingRoutes.Register.route) {
            RegisterScreen(
                sessionViewModel = sessionViewModel,
                onRegisterClick = {
                    navController.navigate(MainRoutes.Root.route)  {
                        popUpTo(OnboardingRoutes.Root.route) { inclusive = true }
                    }
                }
            )
        }
    }
}