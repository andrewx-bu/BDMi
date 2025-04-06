package com.example.bdmi.onboarding_screens

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bdmi.utils.hashPassword
import com.example.bdmi.viewmodels.UserViewModel

@Composable
fun RegisterScreen(
    onRegisterClick: () -> Unit
) {
    val TAG = "RegisterScreen"
    Log.d(TAG, "Reached register screen")
    val userViewModel: UserViewModel = hiltViewModel()
    var email by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(10.dp)
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.padding(5.dp)
        )
        TextField(
            value = displayName,
            onValueChange = { displayName = it },
            label = { Text("Username") },
            shape = RoundedCornerShape(10.dp),
        )
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.padding(5.dp),
            visualTransformation = PasswordVisualTransformation()
        )
        Button(
            onClick = {
                Log.d(TAG, "Create Account button clicked")
                val userInfo: HashMap<String, Any> = hashMapOf(
                    "email" to email,
                    "displayName" to displayName,
                    "password" to hashPassword(password),
                )
                userViewModel.register(userInfo) { accountCreated ->
                    if (accountCreated) {
                        Log.d(TAG, "Register successful")
                        userViewModel.loadUserInfo(userInfo as HashMap<String, Any?>?) // Saves user info into viewModel
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

@Preview
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(onRegisterClick = {})
}