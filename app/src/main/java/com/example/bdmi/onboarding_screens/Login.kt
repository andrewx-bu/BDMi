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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bdmi.viewmodels.UserViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit
) {
    val TAG = "LoginScreen"
    Log.d(TAG, "Reached login screen")
    val userViewModel: UserViewModel = hiltViewModel()
    var email = ""
    var password = ""
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(10.dp)
    ) {
        TextField(
            value = "",
            onValueChange = { email = it },
            label = { Text("Email") },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.padding(5.dp)
        )
        TextField(
            value = "",
            onValueChange = { password = it },
            label = { Text("Password") },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.padding(5.dp)
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

// Returns the password hash in SHA-256
fun hashPassword(password: String): String {
    val digest = java.security.MessageDigest.getInstance("SHA-256")
    val hash = digest.digest(password.toByteArray())
    return hash.joinToString("") { "%02x".format(it) }
}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen(onLoginClick = {})
}