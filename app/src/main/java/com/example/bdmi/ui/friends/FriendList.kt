package com.example.bdmi.ui.friends

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bdmi.SessionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendListScreen(
    sessionViewModel: SessionViewModel,
    onNavigateBack: () -> Unit,
    onProfileClick: (String) -> Unit
) {
    val friendViewModel: FriendViewModel = hiltViewModel()
    val friendList = friendViewModel.friends.collectAsState().value as MutableList<ProfileBanner>
    val userId = sessionViewModel.userInfo.collectAsState().value?.userId
    LaunchedEffect(userId) {
        friendViewModel.loadFriends(userId!!)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Friend List") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            items(friendList) { friend ->
                ProfileCard(friend, onProfileClick)
            }
        }
    }
}