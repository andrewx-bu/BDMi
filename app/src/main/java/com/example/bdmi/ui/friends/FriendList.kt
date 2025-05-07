package com.example.bdmi.ui.friends

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bdmi.ui.theme.dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendListScreen(
    userId: String,
    onProfileClick: (String) -> Unit
) {
    val friendViewModel: FriendViewModel = hiltViewModel()
    val friendList = friendViewModel.friends.collectAsState().value
    LaunchedEffect(userId) {
        friendViewModel.loadFriends(userId)
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
                text = "Friend List",
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.weight(1f)
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(friendList) { friend ->
                ProfileCard(friend, onProfileClick)
            }
        }
    }
}