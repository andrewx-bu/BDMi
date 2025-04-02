package com.example.bdmi.onboarding_screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp

@Composable
fun StartScreen () {
    Log.d("AppStart", "Reached app start")
    Column (
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "BDMi",
            fontSize = 50.sp,
        )
        Button(
            onClick = {}
        ) {
            Text(text = "Login")
        }
        Button(
            onClick = {}
        ) {
            Text(text = "Register")
        }
    }
}