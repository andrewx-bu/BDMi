package com.example.bdmi.ui.notifications

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.bdmi.ui.viewmodels.FriendViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Updates") },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        NotificationList(modifier = Modifier.padding(padding))
    }
}

@Composable
fun NotificationList(modifier: Modifier = Modifier) {
    val notificationViewModel: NotificationViewModel = hiltViewModel()
    val notificationList = notificationViewModel.notificationList.collectAsState()
    val numOfNotifications = notificationViewModel.numOfNotifications.collectAsState()
    val sharedPreferences = LocalContext.current.getSharedPreferences("UserPref", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", null)
    LaunchedEffect(Unit) {
        if (userId != null)
            notificationViewModel.getNotifications(userId)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        if (numOfNotifications.value > 0) {
            Log.d("NotificationList", "Notification list: ${notificationList.value}")
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                items(notificationList.value) { notification ->
                    NotificationItem(notification = notification)
                }
            }
        } else {
            Text(
                text = "Notifications",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }

}

@Composable
fun NotificationItem(modifier: Modifier = Modifier, notification: Notification) {
    val notificationViewModel: NotificationViewModel = hiltViewModel()
    val sharedPreferences = LocalContext.current.getSharedPreferences("UserPref", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", null)
    var isRead by remember { mutableStateOf(notification.read) }
    var cardVisibility = if (isRead) 0.5f else 1f
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable(enabled = !isRead) {
                notificationViewModel.readNotification(userId.toString(), notification.notificationId)
            }
            .graphicsLayer(alpha = cardVisibility)
    ) {
        val notificationType = when (notification.type) {
            "friend_request" -> "Friend Request"
            else -> "Unknown"
        }
        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.fillMaxWidth()
        ) {
            Text(
                text = notificationType,
                style = MaterialTheme.typography.titleMedium,
                color = Color.DarkGray,
                modifier = modifier.padding(horizontal = 10.dp)
            )
            IconButton(
                onClick = {
                    isRead = true
                    notificationViewModel.deleteNotification(userId.toString(), notification.notificationId)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove Notification"
                )
            }
        }

        HorizontalDivider(
            color = Color.LightGray,
            thickness = 1.dp,
            modifier = modifier.padding(horizontal = 10.dp)
        )

        Column {
            when (notification.type) {
                "friend_request" -> FriendRequestNotification(notification.notificationId, notification.data as NotificationType.FriendRequest)
                // Add more notification types here
            }
        }

    }
}

@Composable
fun FriendRequestNotification(notificationId: String, data: NotificationType.FriendRequest) {
    val notificationViewModel: NotificationViewModel = hiltViewModel()
    val sharedPreferences = LocalContext.current.getSharedPreferences("UserPref", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", null)
    val friendViewModel: FriendViewModel = hiltViewModel()
    Row(
        modifier = Modifier.fillMaxWidth().padding(5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .border(2.dp, Color.Black, CircleShape)
                    .padding(4.dp)
                    .clip(CircleShape)
            ) {
                AsyncImage(
                    model = data.profilePicture,
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            }
            Column(
                modifier = Modifier.padding(start = 10.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = data.displayName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    color = Color.Black
                )
                Row {
                    UserStats("Friends", data.friendCount.toString())
                    UserStats("Lists", data.listCount.toString())
                    UserStats("Reviews", data.reviewCount.toString())
                }
            }
        }
        Column (
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding()
        ) {
            IconButton(
                onClick = {
                    friendViewModel.acceptInvite(userId.toString(), data.userId) {
                        if (it) {
                            Log.d("NotificationItem", "Invite accepted")
                        } else {
                            Log.d("NotificationItem", "Error accepting invite")
                        }
                    }
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Green,
                    contentColor = Color.Black
                ),
                modifier = Modifier.clip(CircleShape).size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Accept Friend Request"
                )
            }
            Spacer(modifier = Modifier.padding(2.dp))
            IconButton(
                onClick = {
                    notificationViewModel.deleteNotification(userId.toString(), notificationId)
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Red,
                    contentColor = Color.Black
                ),
                modifier = Modifier.clip(CircleShape).size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Decline Friend Request"
                )
            }
        }
    }

}

@Composable
fun UserStats(text: String, stat: String) {
    Column (
        modifier = Modifier.padding(end = 10.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = stat, color = Color.Gray)
        Text(text = text, fontWeight = FontWeight.Bold, color = Color.Gray)
    }
}

@Preview
@Composable
fun NotificationPreview() {
    val notification = Notification(
        type = "friend_request",
        data = NotificationType.FriendRequest(
            userId = "123",
            displayName = "John Doe",
            profilePicture = "https://example.com/profile.jpg",
            friendCount = 10,
            listCount = 5,
            reviewCount = 2,
            isPublic = true
        )
    )
    NotificationItem(notification = notification)

}