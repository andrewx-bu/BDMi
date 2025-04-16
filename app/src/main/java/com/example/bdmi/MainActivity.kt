package com.example.bdmi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.bdmi.navigation.RootNavigation
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import com.example.bdmi.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve userId from shared preferences
        val sharedPref = getSharedPreferences("BDMi_Prefs", MODE_PRIVATE)
        val userId = sharedPref.getString("userId", null)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            AppTheme(darkTheme = true) {
                RootNavigation(userId)
            }
        }
    }
}
