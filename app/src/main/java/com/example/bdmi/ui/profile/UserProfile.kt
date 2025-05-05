package com.example.bdmi.ui.profile

import android.util.Log
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
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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

private const val TAG = "ProfileScreen"

@Composable
fun UserProfile(
    profileUserId: String? = null,
    sessionViewModel: SessionViewModel,
    onLogoutClick: () -> Unit = {},
    navigateToUserSearch: () -> Unit = {},
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
        } else if (currentUserId != profileUserId &&
            !profileInfo.isPublic &&
            friendStatus != FriendStatus.FRIEND
        ) { // Checks if public profile or not
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
        else { // Display profile
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        state = scrollState
                    )
            ) {
                // Profile Picture
                ProfilePicture(profileInfo.profilePicture.toString(), tempImageURI.value, editPrivileges) {
                    if (editPrivileges)
                        pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
                }

                // Display Name
                Text(
                    text = profileInfo.displayName.toString()
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
                border = BorderStroke(1.dp, Color.White),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth(.5f).fillMaxHeight(.1f).padding(vertical = 15.dp)
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
                border = BorderStroke(1.dp, Color.White),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth(.5f).fillMaxHeight(.1f).padding(vertical = 15.dp)
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
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth(.5f).fillMaxHeight(.1f).padding(vertical = 15.dp)
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
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatItem(count = friendCount, label = "Friends", onClick = onFriendClick)

        VerticalDivider(Modifier.fillMaxHeight(.1f), thickness = 2.dp, color = Color.White)

        StatItem(count = listCount, label = "Lists", onClick = onListClick)

        VerticalDivider(Modifier.fillMaxHeight(.1f), thickness = 2.dp, color = Color.White)

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