package com.example.bdmi.ui.screens

import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bdmi.ui.viewmodels.UserViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import coil.compose.AsyncImage

@Composable
fun ProfileScreen() {
    val tag = "ProfileScreen"
    val userViewModel: UserViewModel = hiltViewModel()
    val userInfo by userViewModel.userInfo.collectAsState()
    val isLoggedIn by userViewModel.isLoggedIn.collectAsState()

    val sharedPreferences = LocalContext.current.getSharedPreferences("UserPref", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", null)
    Log.d(tag, "ProfileScreen (Before Launch): LoggedIn: $isLoggedIn, UserInfo: $userInfo, UserId (Prefs): $userId")
    // Load user info once we know the user should be logged in but the info is missing.
    LaunchedEffect(isLoggedIn) {
        Log.d(tag, "ProfileScreen (Inside Launch): LoggedIn: $isLoggedIn, UserInfo: $userInfo, UserId (Prefs): $userId")
        if (userInfo == null && userId != null) { // Load user from database if user info is missing
            Log.d(tag, "Loading user info for userId: $userId")
            userViewModel.loadUser(userId) {}
        }
    }

    if (isLoggedIn && userInfo != null) {
        // Followed android docs for this
        val pickMedia = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                userViewModel.changeProfilePicture(userInfo?.userId.toString(), uri) {
                }
            } else {
                Log.d("PhotoPicker", "No photo selected")
            }
        }

        Column (
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            ProfilePicture(userInfo?.profilePicture.toString()) {
                pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
            }
            Text(
                text = userInfo?.displayName.toString()
            )
            Text(
                text = userInfo?.friendCount.toString()+" Friends"
            )
            Button(
                onClick = {
                    userViewModel.logout()
                    sharedPreferences.edit { remove("userId") }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = "Logout",
                    color = Color.Black
                )
            }
        }

    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Profile",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun ProfilePicture(profileImageUrl: String, onEditClick: () -> Unit) {
    Box(
        modifier = Modifier.size(300.dp),
        contentAlignment = Alignment.Center
    ) {
        Log.d("ProfilePicture", "Loading profileImageUrl: $profileImageUrl")

        // Profile Picture
        AsyncImage(
            model = profileImageUrl,
            contentDescription = "Profile Picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(250.dp)
                .clip(CircleShape)
        )

        // Edit Icon in Bottom-Right
        IconButton(
            onClick = { onEditClick() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Black)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Profile Picture",
                tint = Color.White // Icon color
            )
        }
    }
}