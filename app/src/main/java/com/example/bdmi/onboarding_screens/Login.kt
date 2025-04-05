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
import com.example.bdmi.viewmodels.UserViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bdmi.utils.hashPassword

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit
) {
    val TAG = "LoginScreen"
    Log.d(TAG, "Reached login screen")
    val userViewModel: UserViewModel = hiltViewModel()
    var email by remember { mutableStateOf("") }
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
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.padding(5.dp),
            visualTransformation = PasswordVisualTransformation()
        )
        Button(
            onClick = {
                Log.d(TAG, "Login button clicked")
                val loginInfo: HashMap<String, String> = hashMapOf(
                    "email" to email,
                    "password" to hashPassword(password)
                )
                userViewModel.login(loginInfo) {
                    if (it != null) {
                        Log.d(TAG, "Login successful")
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

    Log.d("LoginScreen", "Reached login screen")
}



@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen(onLoginClick = {})
}