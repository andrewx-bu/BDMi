package com.example.bdmi.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.bdmi.ui.viewmodels.UserViewModel

@Composable
fun RootNavigation(userId: String?) {
    val navController = rememberNavController()
    val userViewModel: UserViewModel = hiltViewModel()
    val loggedIn = userViewModel.isLoggedIn.collectAsState()
    val isInitialized = userViewModel.isInitialized.collectAsState()
    Log.d("RootNavigation", "Reached RootNavigation")
    LaunchedEffect(userId) {
        Log.d("RootNavigation", "LaunchedEffect triggered with userId: $userId")
        userViewModel.loadUser(userId) {
            Log.d("RootNavigation", "Loaded user info: $it")
        }
    }

    if (!isInitialized.value) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        RootNavGraph(
            navController = navController,
            loggedIn = loggedIn.value,
            userViewModel = userViewModel
        )
    }
}