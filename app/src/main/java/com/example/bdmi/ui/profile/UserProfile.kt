package com.example.bdmi.ui.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bdmi.SessionViewModel
import com.example.bdmi.data.repositories.FriendStatus
import com.example.bdmi.data.repositories.UserInfo
import com.example.bdmi.data.utils.formatReviewCount
import com.example.bdmi.ui.composables.LoadingIndicator
import com.example.bdmi.ui.composables.movie_detail.middle.ShimmeringDivider
import com.example.bdmi.ui.composables.profile.ProfilePicture
import com.example.bdmi.ui.composables.profile.UserReviewCarousel
import com.example.bdmi.ui.theme.dimens
import kotlinx.coroutines.launch

@Composable
fun UserProfile(
    profileUserId: String? = null,
    sessionViewModel: SessionViewModel,
    onLogoutClick: () -> Unit = {},
    onNavigateToFriendList: (String) -> Unit,
    onNavigateToWatchlists: (String) -> Unit,
    onNavigateToReviews: (String) -> Unit,
    onMovieClick: (Int) -> Unit
) {
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val currentUserId = sessionViewModel.userInfo.collectAsState().value?.userId
    val profileId = profileUserId ?: currentUserId
    val profileInfo = profileViewModel.userInfo.collectAsState().value
    val friendStatus = profileViewModel.friendState.collectAsState().value
    val isPrivate =  profileInfo != null &&
            profileUserId != null &&
            currentUserId != profileUserId &&
            !profileInfo.isPublic &&
            friendStatus != FriendStatus.FRIEND
    val tempImageURI = profileViewModel.tempImageURI.collectAsState()
    val reviewCarousel = profileViewModel.reviewCarousel.collectAsState()
    val editPrivileges = profileUserId == null
    val isLoading = profileViewModel.isLoading.collectAsState().value
    val scrollState = rememberScrollState()
    val pickMedia = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
        if (uri != null) {
            profileViewModel.changeProfilePicture(profileInfo?.userId.toString(), uri)
        }
    }

    if (profileUserId == null) {
        LaunchedEffect(profileId) {
            val userInfo = sessionViewModel.userInfo.value
            if (userInfo != null)
                profileViewModel.setUserInfo(userInfo)
        }
    }

    if (profileUserId != null) {
        LaunchedEffect(profileId) {
            launch {
                if (currentUserId != profileId)
                    profileViewModel.getFriendStatus(currentUserId.toString(), profileId.toString())
            }
            launch { profileViewModel.loadProfile(profileId.toString()) {} }
        }
    }

    LaunchedEffect(profileInfo) {
        if (profileInfo != null) {
            profileViewModel.reviewCarousel()
        }
    }

    if (isLoading) {
        LoadingIndicator()
    } else {
        if (profileInfo == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LoadingIndicator()
            }
        } else if (isPrivate) { // Private Profile
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        state = scrollState
                    )
            ) {
                // Profile Picture
                ProfilePicture(
                    profileInfo.profilePicture.toString(),
                    tempImageURI.value,
                    editPrivileges = false
                ) {}

                // Display Name
                Text(
                    text = profileInfo.displayName.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = dimens.medium3)
                )

                // Friend Button
                FriendButton(
                    friendStatus,
                    profileId.toString(),
                    sessionViewModel.userInfo.collectAsState().value
                )

                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Profile is private",
                    tint = Color.White,
                    modifier = Modifier.size(200.dp)
                )
                Text(
                    text = "Profile is private",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        } else { // Display profile
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        state = scrollState
                    )
            ) {
                // Settings
                if (editPrivileges) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        SettingsButton(
                            sessionViewModel = sessionViewModel,
                            onLogoutClick = onLogoutClick
                        )
                    }
                }


                // Profile Picture
                ProfilePicture(
                    profileInfo.profilePicture.toString(),
                    tempImageURI.value,
                    editPrivileges
                ) {
                    if (editPrivileges)
                        pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
                }

                // Display Name
                Text(
                    text = profileInfo.displayName.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = dimens.medium3)
                )

                // Friend Button
                if (currentUserId != profileId) {
                    FriendButton(
                        friendStatus,
                        profileId.toString(),
                        sessionViewModel.userInfo.collectAsState().value
                    )
                }

                // Profile Stats
                ProfileStatsRow(
                    friendCount = profileInfo.friendCount,
                    listCount = profileInfo.listCount,
                    reviewCount = profileInfo.reviewCount,
                    onFriendClick = { onNavigateToFriendList(profileId.toString()) },
                    onListClick = { onNavigateToWatchlists(profileId.toString()) },
                    onReviewClick = { onNavigateToReviews(profileId.toString()) }
                )

                // Review Carousel
                val pagerState = rememberPagerState(initialPage = 0) { 5 }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .graphicsLayer { scaleX = -1f }
                    ) {
                        ShimmeringDivider()
                    }
                    Text(
                        text = "FEATURED REVIEWS",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        modifier = Modifier.padding(horizontal = dimens.small3)
                    )
                    Box(modifier = Modifier.weight(1f)) {
                        ShimmeringDivider()
                    }
                }
                UserReviewCarousel(
                    reviews = reviewCarousel.value,
                    onMovieClick = onMovieClick,
                    pagerState = pagerState
                )
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsButton(
    sessionViewModel: SessionViewModel,
    onLogoutClick: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var displayName by remember { mutableStateOf(sessionViewModel.userInfo.value?.displayName ?: "") }
    var isPublic by remember { mutableStateOf(sessionViewModel.userInfo.value?.isPublic != false) }

    IconButton(
        onClick = { expanded = true },
        modifier = Modifier.padding(end = dimens.medium3)
    ) {
        Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
    }

    if (expanded) {
        ModalBottomSheet(onDismissRequest = { expanded = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimens.medium3),
                verticalArrangement = Arrangement.spacedBy(dimens.medium2)
            ) {
                Text("Account Settings", style = MaterialTheme.typography.titleMedium)

                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Display Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                // TODO: Email field
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    enabled = false,
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Public Profile")
                    Switch(
                        checked = isPublic,
                        onCheckedChange = { isPublic = it }
                    )
                }

                Button(
                    onClick = {
                        if (displayName.isNotEmpty()) {
                            sessionViewModel.updateUserInfo(displayName, isPublic)
                            expanded = false
                        } else {
                            // TODO: Display error message
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Changes")
                }

                Button(
                    onClick = {
                        expanded = false
                        sessionViewModel.logout()
                        onLogoutClick()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = Color.Red,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Logout")
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
) {
    val profileViewModel: ProfileViewModel = hiltViewModel()

    when (friendStatus) {
        FriendStatus.FRIEND -> {
            TextButton(
                onClick = {
                    profileViewModel.removeFriend(
                        currentUser?.userId.toString(),
                        profileUserId,
                    )
                },
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                border = BorderStroke(dimens.small1, Color.White),
                shape = RoundedCornerShape(dimens.medium1),
                modifier = Modifier
                    .fillMaxWidth(.5f)
                    .fillMaxHeight(.1f)
                    .padding(vertical = dimens.medium3)
            ) {
                Text(text = "Unfriend")
            }
        }

        FriendStatus.PENDING -> {
            TextButton(
                onClick = {
                    profileViewModel.cancelFriendRequest(
                        currentUser?.userId.toString(),
                        profileUserId
                    )
                },
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                border = BorderStroke(dimens.small1, Color.White),
                shape = RoundedCornerShape(dimens.medium1),
                modifier = Modifier
                    .fillMaxWidth(.5f)
                    .fillMaxHeight(.1f)
                    .padding(vertical = dimens.medium3)
            ) {
                Text(text = "Requested")
            }
        }

        FriendStatus.NOT_FRIENDS -> {
            TextButton(
                onClick = {
                    if (currentUser != null) {
                        profileViewModel.sendFriendInvite(
                            senderInfo = currentUser,
                            profileUserId
                        )
                    }

                },
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color(0xFF0293A7),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(dimens.medium1),
                modifier = Modifier
                    .fillMaxWidth(.5f)
                    .fillMaxHeight(.1f)
                    .padding(vertical = dimens.medium3)
            ) {
                Text(text = "Add Friend")
            }
        }

        else -> {
            LoadingIndicator()
        }
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
            .padding(vertical = dimens.medium3),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatItem(count = friendCount, label = "Friends", onClick = onFriendClick)

        VerticalDivider(Modifier.fillMaxHeight(.1f), thickness = dimens.small1, color = Color.White)

        StatItem(count = listCount, label = "Lists", onClick = onListClick)

        VerticalDivider(Modifier.fillMaxHeight(.1f), thickness = dimens.small1, color = Color.White)

        StatItem(count = reviewCount, label = "Reviews", onClick = onReviewClick)
    }
}

@Composable
fun StatItem(count: Long, label: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = dimens.medium2)
            .fillMaxHeight(.1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = formatReviewCount(count.toInt()), style = MaterialTheme.typography.titleMedium)
        Text(text = label, style = MaterialTheme.typography.labelMedium)
    }
}