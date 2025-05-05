package com.example.bdmi.ui.friends

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
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
import com.example.bdmi.SessionViewModel
import com.example.bdmi.data.repositories.FriendStatus
import com.example.bdmi.data.repositories.UserInfo
import com.example.bdmi.data.utils.formatReviewCount
import com.example.bdmi.ui.composables.LoadingIndicator
import kotlinx.coroutines.launch

private const val TAG = "ProfileScreen"

@Composable
fun UserProfile(
    profileUserId: String,
    sessionViewModel: SessionViewModel,
    onNavigateToFriendList: (String) -> Unit,
    onNavigateToWatchlists: (String) -> Unit,
    onNavigateToReviews: (String) -> Unit
    ) {
    val friendViewModel: FriendViewModel = hiltViewModel()
    val currentUserId = sessionViewModel.userInfo.collectAsState().value?.userId
    val profileInfo = friendViewModel.friendProfile.collectAsState().value
    val friendStatus = friendViewModel.friendState.collectAsState().value

    LaunchedEffect(profileUserId) {
        launch {
            if (currentUserId != profileUserId)
                friendViewModel.getFriendStatus(currentUserId.toString(), profileUserId)
        }
        launch { friendViewModel.loadProfile(profileUserId) {} }
    }

    if (profileInfo == null) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (currentUserId != profileUserId && !profileInfo.isPublic && friendStatus != FriendStatus.FRIEND) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Profile is private",
                tint = MaterialTheme.colorScheme.background
            )
        }
    }
    else {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            ProfilePicture(profileInfo.profilePicture.toString())
            Text(
                text = profileInfo.displayName.toString()
            )
            ProfileStatsRow(
                friendCount = profileInfo.friendCount,
                listCount = profileInfo.listCount,
                reviewCount = profileInfo.reviewCount,
                onFriendClick = { onNavigateToFriendList(profileUserId) },
                onListClick = { onNavigateToWatchlists(profileUserId) },
                onReviewClick = { onNavigateToReviews(profileUserId) }
            )
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = profileInfo.friendCount.toString() + " Friends"
                )
                if (currentUserId != profileUserId) {
                    FriendButton(
                        friendStatus,
                        profileUserId,
                        sessionViewModel.userInfo.collectAsState().value,
                        friendViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun FriendButton(
    friendStatus: FriendStatus?,
    profileUserId: String,
    currentUser: UserInfo?,
    friendViewModel: FriendViewModel
) {
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
                    if (currentUser != null) {
                        friendViewModel.sendFriendInvite(
                            senderInfo = currentUser,
                            recipientId = profileUserId,
                            onComplete = {
                                Log.d(TAG, "Friend invite sent: $it")
                            }
                        )
                    }
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
            LoadingIndicator()
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

@Composable
fun ProfileStatsRow(
    friendCount: Long,
    listCount: Long,
    reviewCount: Long,
    onFriendClick: () -> Unit,
    onListClick: () -> Unit,
    onReviewClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatItem(count = friendCount, label = "Friends", onClick = onFriendClick)

        VerticalDivider(Modifier.fillMaxHeight(.1f), thickness = 2.dp, color = Color.Gray)

        StatItem(count = listCount, label = "Lists", onClick = onListClick)

        VerticalDivider(Modifier.fillMaxHeight(.1f), thickness = 2.dp, color = Color.Gray)

        StatItem(count = reviewCount, label = "Reviews", onClick = onReviewClick)
    }
}

@Composable
fun StatItem(count: Long, label: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp)
            .fillMaxHeight(.1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = formatReviewCount(count.toInt()), style = MaterialTheme.typography.titleMedium)
        Text(text = label, style = MaterialTheme.typography.labelMedium)
    }
}