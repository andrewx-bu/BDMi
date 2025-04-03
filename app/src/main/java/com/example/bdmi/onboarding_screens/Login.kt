package com.example.bdmi.onboarding_screens

import android.util.Log
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit
) {
    Button(
        onClick = { onLoginClick() }
    ) {
        Text(text = "Login")
    }
    Log.d("LoginScreen", "Reached login screen")
}
