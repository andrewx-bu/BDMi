package com.example.bdmi.ui.friends

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.bdmi.data.repositories.UserInfo
import com.example.bdmi.ui.notifications.UserStats
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun FriendSearch(
    onNavigateBack: () -> Unit,
    onProfileClick: (String) -> Unit,
    currentUserId: String
) {
    val friendViewModel: FriendViewModel = hiltViewModel()
    var searchQuery by remember { mutableStateOf("") }
    val searchResults = friendViewModel.searchResults.collectAsState().value

    // ChatGPT assisted with this for debouncing
    LaunchedEffect(searchQuery) {
        snapshotFlow { searchQuery }
            .map { it.trim() }
            .distinctUntilChanged()
            .debounce(1000)
            .collect { query ->
                friendViewModel.searchUsers(currentUserId,query)
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Friend Search") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Enter display name") },
                singleLine = true,
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(searchResults) { user ->
                    ProfileCard(user, onProfileClick)
                }
            }
        }
    }
}

@Composable
fun ProfileCard(user: UserInfo, onProfileClick: (String) -> Unit) {
    Log.d("ProfileCard", "Displaying user: $user")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable {
                onProfileClick(user.userId)
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
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
                    model = user.profilePicture,
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
                    text = user.displayName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    color = Color.White
                )
                Row {
                    UserStats("Friends", user.friendCount.toString())
                    UserStats("Lists", user.listCount.toString())
                    UserStats("Reviews", user.reviewCount.toString())
                }
            }
        }
    }
}