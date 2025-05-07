package com.example.bdmi.ui.notifications

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.bdmi.SessionViewModel
import com.example.bdmi.data.repositories.Notification
import com.example.bdmi.data.repositories.NotificationType
import com.example.bdmi.ui.theme.dimens

// TODO: Improve notification screen and add more notification types
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    sessionViewModel: SessionViewModel,
    onProfileClick: (String) -> Unit,
    onMovieClick: (Int) -> Unit
) {
    val notificationViewModel: NotificationViewModel = hiltViewModel()
    val userId = sessionViewModel.userInfo.collectAsState().value?.userId
    val notifications = notificationViewModel.notificationList.collectAsState().value

    LaunchedEffect(userId) {
        notificationViewModel.getNotifications(userId.toString())
    }

    LaunchedEffect(notifications) {
        val unreadCount = notifications.count { !it.read }
        sessionViewModel.loadNumOfUnreadNotifications(unreadCount)
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(dimens.medium2)
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Notifications",
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.weight(1f)
            )
            ElevatedButton(
                elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = dimens.medium1),
                onClick = {
                    notificationViewModel.deleteAllNotifications(userId.toString())
                }
            ) {
                Text(
                    text = "Clear All"
                )
            }
        }
        NotificationList(
            userId = userId,
            notifications = notifications,
            onProfileClick = onProfileClick,
        )
    }
}

@Composable
fun NotificationList(
    modifier: Modifier = Modifier,
    userId: String? = null,
    notifications: List<Notification> = emptyList(),
    onProfileClick: (String) -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        if (notifications.isNotEmpty()) {
            Log.d("NotificationList", "Notification list: $notifications")
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                items(notifications) { notification ->
                    NotificationItem(
                        notification = notification,
                        userId = userId,
                        onProfileClick = onProfileClick,
                    )
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
fun NotificationItem(
    modifier: Modifier = Modifier,
    userId: String? = null,
    notification: Notification,
    onProfileClick: (String) -> Unit
) {
    val notificationViewModel: NotificationViewModel = hiltViewModel()
    val cardVisibility = if (notification.read) 0.5f else 1f
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = dimens.medium1),
        shape = RoundedCornerShape(dimens.small2),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = dimens.medium1)
            .clickable {
                notificationViewModel.readNotification(
                    userId.toString(),
                    notification.notificationId
                )
            }
            .graphicsLayer(alpha = cardVisibility)
    ) {
        val notificationType = when (notification.type) {
            "friend_request" -> "Friend Request"
            else -> "Unknown"
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.fillMaxWidth()
        ) {
            Text(
                text = notificationType,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = modifier.padding(horizontal = dimens.medium1)
            )
            // Delete a notification
            IconButton(
                onClick = {
                    if (notification.type == "friend_request") {
                        val friendData = notification.data as NotificationType.FriendRequest
                        if (!friendData.responded) {
                            notificationViewModel.declineInvite(
                                userId.toString(),
                                friendData.userId
                            ) {
                                notificationViewModel.deleteNotification(
                                    userId.toString(),
                                    notification.notificationId
                                )
                            }
                        } else {
                            notificationViewModel.deleteNotification(
                                userId.toString(),
                                notification.notificationId
                            )
                        }
                    } else {
                        notificationViewModel.deleteNotification(
                            userId.toString(),
                            notification.notificationId
                        )
                    }
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
            thickness = dimens.small1,
            modifier = modifier.padding(horizontal = dimens.medium1)
        )

        Column {
            when (notification.type) {
                "friend_request" -> FriendRequestNotification(
                    notification.notificationId,
                    notification.data as NotificationType.FriendRequest,
                    userId,
                    visibility = cardVisibility,
                    onProfileClick = onProfileClick
                )
                // Add more notification types here
            }
        }

    }
}

@Composable
fun FriendRequestNotification(
    notificationId: String,
    data: NotificationType.FriendRequest,
    userId: String?,
    visibility: Float = 1f,
    onProfileClick: (String) -> Unit
) {
    val notificationViewModel: NotificationViewModel = hiltViewModel()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimens.small2)
            .graphicsLayer(alpha = visibility),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(dimens.friendProfileSize)
                    .border(dimens.small1, Color.Black, CircleShape)
                    .padding(dimens.small2)
                    .clip(CircleShape)
                    .clickable {
                        onProfileClick(data.userId)
                    }
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
                modifier = Modifier.padding(start = dimens.medium1),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = data.displayName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Row {
                    UserStats("Friends", data.friendCount.toString())
                    UserStats("Lists", data.listCount.toString())
                    UserStats("Reviews", data.reviewCount.toString())
                }
            }
        }
        if (!data.responded) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding()
            ) {
                IconButton(
                    onClick = {
                        notificationViewModel.acceptInvite(userId.toString(), data.userId) {
                            if (it) {
                                Log.d("NotificationItem", "Invite accepted")
                            } else {
                                Log.d("NotificationItem", "Error accepting invite")
                            }
                        }
                        notificationViewModel.friendRequestResponse(
                            userId.toString(),
                            notificationId
                        )
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Green,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(dimens.iconMedium)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Accept Friend Request"
                    )
                }
                Spacer(modifier = Modifier.padding(dimens.small1))
                // If user hasn't responded to the friend request display the buttons
                IconButton(
                    onClick = {
                        notificationViewModel.declineInvite(userId.toString(), data.userId) {
                            if (it) {
                                Log.d("NotificationItem", "Invite declined")
                            } else {
                                Log.d("NotificationItem", "Error declining invite")
                            }
                        }
                        notificationViewModel.friendRequestResponse(
                            userId.toString(),
                            notificationId
                        )
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Red,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(dimens.iconMedium)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Decline Friend Request"
                    )
                }
            }
        }
    }
}

@Composable
fun UserStats(text: String, stat: String) {
    Column(
        modifier = Modifier.padding(end = dimens.medium1),
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = stat, color = MaterialTheme.colorScheme.primary)
        Text(text = text, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    }
}