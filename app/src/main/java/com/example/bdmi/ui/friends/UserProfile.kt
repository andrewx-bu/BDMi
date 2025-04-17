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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.bdmi.ui.viewmodels.FriendStatus
import com.example.bdmi.ui.viewmodels.FriendViewModel
import com.example.bdmi.ui.viewmodels.ProfileBanner
import com.example.bdmi.ui.viewmodels.UserInfo
import com.example.bdmi.ui.viewmodels.UserViewModel

private const val TAG = "ProfileScreen"

@Composable
fun UserProfile(profileUserId: String = "", userViewModel: UserViewModel) {
    val friendViewModel: FriendViewModel = hiltViewModel()
    var currentUser = userViewModel.userInfo.collectAsState()
    var profileInfo = friendViewModel.friendProfile.collectAsState()
    var friendButtonState = friendViewModel.friendState.collectAsState()
    LaunchedEffect(profileUserId) {
        friendViewModel.loadProfile(profileUserId) { userInfo ->
        }
        friendViewModel.getFriendStatus(currentUser.value?.userId.toString(), profileUserId)
    }


    if (profileInfo.value == null) {
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
            ProfilePicture(profileInfo.value?.profilePicture.toString())
            Text(
                text = profileInfo.value?.displayName.toString()
            )
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = profileInfo.value?.friendCount.toString() + " Friends"
                )
                FriendButton(friendButtonState.value, profileUserId, currentUser.value, friendViewModel)
            }
        }
    }
}

@Composable
fun FriendButton(friendStatus: FriendStatus?, profileUserId: String, currentUser: UserInfo?, friendViewModel: FriendViewModel) {
    when (friendStatus) {
        FriendStatus.FRIEND -> {
            IconButton(
                onClick = {
                    friendViewModel.removeFriend(
                        currentUser?.userId.toString(),
                        profileUserId,
                        onComplete = {
                            Log.d(TAG, "Friend removed: $it")
                        }
                    )
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Red,
                    contentColor = Color.Black
                ),
                modifier = Modifier.clip(CircleShape)
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Remove Friend")
            }
        }
        FriendStatus.PENDING -> {
            IconButton(
                onClick = {
                    friendViewModel.cancelFriendRequest(
                        currentUser?.userId.toString(),
                        profileUserId
                    )
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.LightGray,
                    contentColor = Color.Black
                ),
                modifier = Modifier.clip(CircleShape)
            ) {
                Icon(imageVector = Icons.Default.AccessTime, contentDescription = "Remove Friend")
            }
        }
        FriendStatus.NOT_FRIENDS -> {
            IconButton(
                onClick = {
                    friendViewModel.sendFriendInvite(
                        senderInfo = ProfileBanner(
                            userId = currentUser?.userId.toString(),
                            displayName = currentUser?.displayName.toString(),
                            profilePicture = currentUser?.profilePicture.toString(),
                            friendCount = currentUser?.friendCount,
                            listCount = currentUser?.listCount,
                            reviewCount = currentUser?.reviewCount,
                            isPublic = currentUser?.isPublic
                                ),
                            recipientId = profileUserId,
                            onComplete = {
                                Log.d(TAG, "Friend invite sent: $it")
                            }
                    )
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color(0xFF00BCD4),
                    contentColor = Color.Black
                ),
                modifier = Modifier.clip(CircleShape)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Friend")
            }
        }
        else -> {
            Text(text = "Loading...")
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