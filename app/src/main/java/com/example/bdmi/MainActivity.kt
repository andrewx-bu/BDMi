package com.example.bdmi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.bdmi.navigation.RootNavigation
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bdmi.ui.theme.AppTheme
import com.example.bdmi.ui.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve userId from shared preferences
        val sharedPref = getSharedPreferences("UserPref", MODE_PRIVATE)
        val userId = sharedPref.getString("userId", null)
        //setContent {
            //RootNavigation(userId)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            EntryPoint(userId)
        }
    }
}

/*
fun Wrapper(userId: String? = null) {
    Log.d("MainActivity", "Reached Wrapper")
    val userViewModel: UserViewModel = hiltViewModel() // A global UserViewModel instance
    var isLoggedIn = userViewModel.isLoggedIn.collectAsState()
@Composable
fun EntryPoint(userId: String? = null) {
    Log.d("MainActivity", "Reached EntryPoint")
    val userViewModel: UserViewModel = hiltViewModel()
    // Should recompose when isLoggedIn observes state change
    val isLoggedIn by userViewModel.isLoggedIn.collectAsState()
    if (userId != null && !isLoggedIn.value) {
        userViewModel.loadUser(userId) {
            if (it != null) {
              Log.d("MainActivity", "Loaded User in EntryPoint")
            }
        }
    }

    Log.d("MainActivity", "User loggedIn: $isLoggedIn")
    val rootController = rememberNavController()
    RootNavGraph(
        navController = rootController,
        loggedIn = isLoggedIn.value,
        userViewModel = userViewModel
    )
}

    val systemDark = isSystemInDarkTheme()
    var darkTheme by remember { mutableStateOf(systemDark) }
    AppTheme(darkTheme = darkTheme) {
        MainScreen(
            darkTheme = darkTheme,
            loggedIn = isLoggedIn,
            switchTheme = { darkTheme = !darkTheme }
        )
    }*/