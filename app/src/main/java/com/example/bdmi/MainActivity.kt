package com.example.bdmi

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bdmi.ui.theme.AppTheme
import com.example.bdmi.viewmodels.UserViewModel
import com.example.bdmi.screens.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // Don't need to initialize as it's done by Hilt
    private val userViewModel: UserViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Remembers the user's login status
        // SharedPreferences process recommended and written by Copilot
        val sharedPref = getSharedPreferences("UserPref", MODE_PRIVATE)
        val userId = sharedPref.getString("userId", null)

        // State and Structure of this code written by Gemini
        setContent {
            Wrapper(userId)
        }
    }
}

@Composable
fun Wrapper(userId: String? = null) {
    Log.d("MainActivity", "Reached Wrapper")
    val userViewModel: UserViewModel = hiltViewModel()
    val isLoggedIn by userViewModel.isLoggedIn.collectAsState() // Should recompose when isLoggedIn observes state change

    if (userId != null) {
        Log.d("MainActivity", "[Before loading user] User: LoggedIn: $isLoggedIn")
        userViewModel.loadUser(userId) {
            if (it != null) {
                Log.d("MainActivity", "Loaded User in Wrapper")
            }
        }
    }

    val systemDark = isSystemInDarkTheme()
    var darkTheme by remember { mutableStateOf(systemDark) }
    AppTheme(darkTheme = darkTheme) {
        MainScreen(
            darkTheme = darkTheme,
            loggedIn = isLoggedIn,
            switchTheme = { darkTheme = !darkTheme }
        )
    }
}