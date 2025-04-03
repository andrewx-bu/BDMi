package com.example.bdmi

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
            val isLoggedIn by remember { derivedStateOf { userViewModel.userInfo != null } }
            Wrapper(loggedIn = isLoggedIn)
        }
    }
}

@Composable
fun Wrapper(loggedIn: Boolean) {
    val systemDark = isSystemInDarkTheme()
    var darkTheme by remember { mutableStateOf(systemDark) }
    Log.d("Wrapper", "Logged in: $loggedIn")
    AppTheme(darkTheme = darkTheme) {
        MainScreen(
            darkTheme = darkTheme,
            loggedIn = loggedIn,
            switchTheme = { darkTheme = !darkTheme }
        )
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