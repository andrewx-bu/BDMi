package com.example.bdmi.ui.friends

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.bdmi.ui.viewmodels.UserInfo
import com.example.bdmi.ui.viewmodels.UserViewModel

@Composable
fun UserProfile(userId: String = "") {
    val tag = "ProfileScreen"
    val friendViewModel: FriendViewModel = hiltViewModel()
    var userInfo by remember { mutableStateOf<UserInfo?>(null) }
    friendViewModel.loadFriendProfile(userId) {
        userInfo = it
    }
    if (userInfo == null) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            ProfilePicture(userInfo?.profilePicture.toString())
            Text(
                text = userInfo?.displayName.toString()
            )
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = userInfo?.friendCount.toString() + " Friends"
                )
                IconButton(
                    onClick = {
                        friendViewModel.sendFriendInvite(
                            ProfileBanner(
                                userId = userInfo?.userId.toString(),
                                displayName = userInfo?.displayName.toString(),
                                profilePicture = userInfo?.profilePicture.toString(),
                                friendCount = userInfo?.friendCount,
                                listCount = userInfo?.listCount,
                                reviewCount = userInfo?.reviewCount,
                                isPublic = userInfo?.isPublic
                            ),
                            userId,
                            onComplete = {
                                Log.d(tag, "Friend invite sent: $it")
                            }
                        )
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
        }
    }

}

@Composable
fun ProfilePicture(profileImageUrl: String) {
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
    }
}