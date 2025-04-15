package com.example.bdmi

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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

        // Remembers the user's login status
        // SharedPreferences process recommended by Copilot
        val sharedPref = getSharedPreferences("UserPref", MODE_PRIVATE)
        val userId = sharedPref.getString("userId", null)

        // State and structure of this code written by Gemini
        setContent {
            EntryPoint(userId)
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }
}

@Composable
fun EntryPoint(userId: String? = null) {
    Log.d("MainActivity", "Reached Wrapper")
    val userViewModel: UserViewModel = hiltViewModel()
    // Should recompose when isLoggedIn observes state change
    val isLoggedIn by userViewModel.isLoggedIn.collectAsState()

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