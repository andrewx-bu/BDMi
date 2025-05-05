package com.example.bdmi.ui.profile

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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.bdmi.SessionViewModel

private const val TAG = "ProfileScreen"

@Composable
fun ProfileScreen(
    sessionViewModel: SessionViewModel,
    onLogoutClick: () -> Unit,
    navigateToUserSearch: () -> Unit,
    navigateToFriends: (String) -> Unit,
    navigateToReviews: (String) -> Unit
) {
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val isLoggedIn by sessionViewModel.isLoggedIn.collectAsState()
    val userInfo by profileViewModel.userInfo.collectAsState()
    val tempImageURI by profileViewModel.tempImageURI.collectAsState()


    if (isLoggedIn) {
        // Followed android docs for this
        val pickMedia = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
            if (uri != null) {
                profileViewModel.changeProfilePicture(userInfo?.userId.toString(), uri)
            }
        }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = userInfo?.displayName.toString()
            )
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = userInfo?.friendCount.toString() + " Friends",
                    modifier = Modifier.clickable(
                        enabled = true
                    ) {
                        navigateToFriends(userInfo?.userId.toString())
                    }
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
                    sessionViewModel.logout()
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