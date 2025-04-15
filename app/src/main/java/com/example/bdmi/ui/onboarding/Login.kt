package com.example.bdmi.ui.onboarding

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bdmi.ui.viewmodels.UserViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bdmi.data.utils.hashPassword
import androidx.core.content.edit

private const val TAG = "LoginScreen"

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit
) {
    val userViewModel: UserViewModel = hiltViewModel()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
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
                userViewModel.login(loginInfo) { userInfo ->
                    if (userInfo != null) {
                        // Saves user ID to shared preferences
                        val sharedPreferences = context.getSharedPreferences("UserPref", Context.MODE_PRIVATE)
                        sharedPreferences.edit { putString("userId", userInfo.userId) }

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

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen(onLoginClick = {})
}