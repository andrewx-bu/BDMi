package com.example.bdmi.ui.onboarding

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.bdmi.SessionViewModel
import com.example.bdmi.ui.theme.dimens

private const val TAG = "LoginScreen"

@Composable
fun LoginScreen(
    sessionViewModel: SessionViewModel,
    onLoginClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(dimens.medium1)
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            shape = RoundedCornerShape(dimens.medium1),
            modifier = Modifier.padding(dimens.small2)
        )
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            shape = RoundedCornerShape(dimens.medium1),
            modifier = Modifier.padding(dimens.small2),
            visualTransformation = PasswordVisualTransformation()
        )
        Button(
            onClick = {
                Log.d(TAG, "Login button clicked")
                sessionViewModel.login(email, password) { userInfo ->
                    if (userInfo != null) {
                        onLoginClick()
                    } else {
                        Log.d(TAG, "Login failed")
                    }
                }
            }
        ) {
            Text(text = "Login")
        }
    }
}