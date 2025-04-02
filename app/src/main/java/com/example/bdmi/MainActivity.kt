package com.example.bdmi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.bdmi.onboarding_screens.StartScreen
import com.example.bdmi.ui.theme.AppTheme
import com.example.bdmi.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // Don't need to initialize as it's done by Hilt
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Remembers the user's login status
        //SharedPreferences process recommended and written by Copilot
        val sharedPref = getSharedPreferences("UserPref", MODE_PRIVATE)
        val userId = sharedPref.getString("userId", null)
        if (userId != null) {
            userViewModel.loadUser(userId) {
                userViewModel.loadUserInfo(it)
            }
        }
        setContent {
            if (userViewModel.userInfo != null) {
                Wrapper(loggedIn = true)
            } else {
                Wrapper(loggedIn = false)
            }
            Wrapper()
        }
    }
}

@Composable
fun Wrapper(loggedIn : Boolean = false) {
    val systemDark = isSystemInDarkTheme()
    var darkTheme by remember { mutableStateOf(systemDark) }
    AppTheme(darkTheme = darkTheme) {
        if (loggedIn) {
            MainScreen(
                darkTheme = darkTheme,
                switchTheme = { darkTheme = !darkTheme }
            )
        } else {
            StartScreen(
            )
        }

    }
}

//Basic test to register a user
/*val userInfo : HashMap<String, Any> = hashMapOf(
    "name" to "",
    "email" to "",
    "password" to "",
    "displayName" to ""
)
userViewModel.register(userInfo) {
    if (it) {
        Log.d("MainActivity", "User registered successfully")
    } else {
        Log.d("MainActivity", "User registration failed")
    }
} */