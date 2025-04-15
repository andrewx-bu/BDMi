package com.example.bdmi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve userId from shared preferences
        val sharedPref = getSharedPreferences("UserPref", MODE_PRIVATE)
        val userId = sharedPref.getString("userId", null)

        setContent {
            RootNavigation(userId)
        }
    }
}

/*
@Composable
fun Wrapper(userId: String? = null) {
    Log.d("MainActivity", "Reached Wrapper")
    val userViewModel: UserViewModel = hiltViewModel() // A global UserViewModel instance
    var isLoggedIn = userViewModel.isLoggedIn.collectAsState()

    if (userId != null && !isLoggedIn.value) {
        userViewModel.loadUser(userId) {
            if (it != null) {
                Log.d("MainActivity", "Loaded User in Wrapper: $it")
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