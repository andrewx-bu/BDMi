package com.example.bdmi

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bdmi.ui.theme.AppTheme
import com.example.bdmi.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // Don't need to initialize as it's done by Hilt
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Remembers the user's login status
        // SharedPreferences process recommended and written by Copilot
        val sharedPref = getSharedPreferences("UserPref", MODE_PRIVATE)
        val userId = sharedPref.getString("userId", null)

        // State and Structure of this code written by Gemini
        setContent {
            val userInfo by userViewModel.userInfo.collectAsStateWithLifecycle()
            val isLoggedIn by userViewModel.isLoggedIn.collectAsStateWithLifecycle()

            Log.d("MainActivity", "[Before logging in] User: ${userInfo?.displayName ?: "null"}, LoggedIn: $isLoggedIn")
            if (userId != null && userInfo == null) {
                userViewModel.loadUser(userId) {}
                Log.d("MainActivity", "[After logging in] User: ${userInfo ?: "null"}, LoggedIn: $isLoggedIn")
            }


            Wrapper(loggedIn = isLoggedIn)
        }
    }
}

@Composable
fun Wrapper(loggedIn: Boolean) {
    val systemDark = isSystemInDarkTheme()
    var darkTheme by remember { mutableStateOf(systemDark) }
    AppTheme(darkTheme = darkTheme) {
        MainScreen(
            darkTheme = darkTheme,
            loggedIn = loggedIn,
            switchTheme = { darkTheme = !darkTheme }
        )
    }
}