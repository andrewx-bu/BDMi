package com.example.bdmi.ui.onboarding

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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

private const val TAG = "RegisterScreen"

@Composable
fun RegisterScreen(
    sessionViewModel: SessionViewModel,
    onRegisterClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(dimens.small3)
        ) {
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                shape = RoundedCornerShape(dimens.small3),
                modifier = Modifier.padding(dimens.small2)
            )
            TextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text("Username") },
                shape = RoundedCornerShape(dimens.small3),
            )
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                shape = RoundedCornerShape(dimens.small3),
                modifier = Modifier.padding(dimens.small2),
                visualTransformation = PasswordVisualTransformation()
            )
            Button(
                onClick = {
                    Log.d(TAG, "Create Account button clicked")
                    val userInfo: HashMap<String, Any> = hashMapOf(
                        "email" to email,
                        "displayName" to displayName,
                        "password" to password,
                    )
                    sessionViewModel.register(userInfo) { success ->
                        if (success) {
                            Log.d(TAG, "Register successful")
                            onRegisterClick()
                        } else {
                            Log.d(TAG, "Register failed")
                        }
                    }
                }
            ) {
                Text(text = "Create Account")
            }
        }
    }

}

