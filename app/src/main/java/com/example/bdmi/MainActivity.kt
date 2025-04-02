package com.example.bdmi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.bdmi.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // Don't need to initialize as it's done by Hilt
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        setContent {
            AppTheme {
                MainScreen()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    AppTheme {
        MainScreen()
    }
}