package com.example.bdmi.ui.screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.bdmi.ui.viewmodels.UserViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

private const val TAG = "ProfileScreen"

@Composable
fun ProfileScreen(
    userViewModel: UserViewModel,
    onLogoutClick: () -> Unit,
    navigateToUserSearch: () -> Unit
) {
    val userInfo by userViewModel.userInfo.collectAsState()
    val isLoggedIn by userViewModel.isLoggedIn.collectAsState()
    val tempImageURI by userViewModel.tempImageURI.collectAsState()

    Log.d(TAG, "ProfileScreen: $userInfo")
    Log.d(TAG, "isLoggedIn: $isLoggedIn")
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
            ProfilePicture(userInfo?.profilePicture.toString(), tempImageURI) {
                pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
            }
            Text(
                text = userInfo?.displayName.toString()
            )
            Row (
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = userInfo?.friendCount.toString()+" Friends"
                )
                IconButton(
                    onClick = {
                        navigateToUserSearch()
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.clip(CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Go to friend search",
                        tint = Color.Black
                    )
                }
            }

            Button(
                onClick = {
                    userViewModel.logout()
                    onLogoutClick()
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

fun ProfilePicture(profileImageUrl: String, tempImageUri: Uri?, onEditClick: () -> Unit) {
    val value by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing
            )
        )
    )

    // Animation provided by https://rodrigomartind.medium.com/the-art-of-small-animations-in-android-with-jetpack-compose-566caa94deba
    val colors = listOf(
        Color(0xFF405DE6),
        Color(0xFFC13584),
        Color(0xFFFD1D1D),
        Color(0xFFFFDC80)
    )
    val gradientBrush by remember {
        mutableStateOf(
            Brush.horizontalGradient(
                colors = colors,
                startX = -10.0f,
                endX = 400.0f,
                tileMode = TileMode.Repeated
            )
        )
    }
    Box(
        modifier = Modifier.size(300.dp),
        contentAlignment = Alignment.Center
    ) {
        Log.d("ProfilePicture", "Loading profileImageUrl: $profileImageUrl")

        // Profile Picture
        AsyncImage(
            model = tempImageUri ?: profileImageUrl,
            contentDescription = "Profile Picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(250.dp)
                .clip(CircleShape)
        )
        Box(modifier = Modifier
            .drawBehind {
                rotate(value) {
                    drawCircle(
                        gradientBrush, style = Stroke(width = 12.dp.value)
                    )
                }
            }
            .size(250.dp)
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

//    val sharedPreferences = LocalContext.current.getSharedPreferences("UserPref", Context.MODE_PRIVATE)
//    val userId = sharedPreferences.getString("userId", null)
//    LaunchedEffect(Unit) {
//        if (userId != null) {
//            userViewModel.loadUser(userId) {
//                Log.d(TAG, "Loaded user info: $it")
//            }
//        }
//    }