package com.example.bdmi.ui.onboarding

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun StartScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Log.d("AppStart", "Reached app start")
    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "BDMi",
            fontSize = 50.sp,
        )
        Button(
            onClick = { onLoginClick() }
        ) {
            Text(text = "Login")
        }
        Button(
            onClick = { onRegisterClick() }
        ) {
            Text(text = "Register")
        }
    }
}