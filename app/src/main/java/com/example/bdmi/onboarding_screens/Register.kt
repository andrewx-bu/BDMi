package com.example.bdmi.onboarding_screens

import android.util.Log
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun RegisterScreen(
    onRegisterClick: () -> Unit
) {
    Button(
        onClick = { onRegisterClick() }
    ) {
        Text(text = "Register")
    }
    Log.d("RegisterScreen", "Reached register screen")
}
