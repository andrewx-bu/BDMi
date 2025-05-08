package com.example.bdmi.ui.custom_lists

import android.graphics.fonts.FontStyle
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bdmi.SessionViewModel
import com.example.bdmi.data.repositories.CustomList
import com.example.bdmi.ui.theme.dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistsScreen(
    sessionViewModel: SessionViewModel? = null,
    loggedInUserId: String,
    userId: String? = null,
    onListClick: (Pair<String, String>) -> Unit,
) {
    val currentUserId =
        if (sessionViewModel != null) loggedInUserId
        else userId
    val watchlistViewModel: WatchlistViewModel = hiltViewModel()
    val lists = watchlistViewModel.lists.collectAsState()

    LaunchedEffect(currentUserId) {
        currentUserId?.let { uid ->
            val isOwnProfile = uid == sessionViewModel?.userInfo?.value?.userId
            val publicOnly = !isOwnProfile
            Log.d("WatchlistsScreen", "isOwnProfile: $isOwnProfile, publicOnly: $publicOnly")
            watchlistViewModel.getLists(uid, publicOnly = publicOnly)
        }
    }

    if (sessionViewModel != null) {
        LaunchedEffect(lists.value) {
            if (lists.value.isNotEmpty())
                sessionViewModel.updateWatchlists(lists.value)
        }
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
                text = "Watchlists",
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.weight(1f)
            )
            if (sessionViewModel != null || currentUserId == loggedInUserId) {
                AddListButton { list: CustomList ->
                    watchlistViewModel.createList(currentUserId.toString(), list)
                }
            }
        }
        WatchlistList(
            currentUserId.toString(),
            modifier = Modifier,
            lists.value,
            onListClick
        )
    }
}

@Composable
fun WatchlistList(
    userId: String,
    modifier: Modifier,
    lists: List<CustomList>,
    onListClick: (Pair<String, String>) -> Unit
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(lists) { list: CustomList ->
            WatchlistItem(list) {
                onListClick(Pair(userId, list.listId))
            }
        }
    }
}

@Composable
fun WatchlistItem(
    list: CustomList,
    onListClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimens.small2)
            .clickable { onListClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(dimens.medium2)) {
            Text(
                text = list.name,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(dimens.small1))

            Text(
                text = "${list.numOfItems} ${if (list.numOfItems == 1) "movie/show" else "movies/shows"}",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            )

            if (list.description.isNotBlank()) {
                Spacer(Modifier.height(dimens.small2))
                Text(
                    text = list.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (list.isPublic) {
                Spacer(Modifier.height(dimens.small3))
                Text(
                    text = "Public List",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}


@Composable
fun AddListButton(onClick: (CustomList) -> Unit) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isPublic by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val toast = remember {
        Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT)
    }

    IconButton(onClick = { showDialog = true }) {
        Icon(Icons.Default.Add, contentDescription = "Add List")
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Create Watchlist") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(dimens.medium2),
                    modifier = Modifier.padding(vertical = dimens.medium1)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("List Name") },
                        singleLine = true,
                        isError = name.isBlank()
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description (optional)") },
                        maxLines = 3
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Make Public")
                        Switch(
                            checked = isPublic,
                            onCheckedChange = { isPublic = it }
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (name.isNotBlank()) {
                        onClick(
                            CustomList(
                                name = name.trim(),
                                description = description.trim(),
                                isPublic = isPublic
                            )
                        )
                        // Reset and close
                        name = ""
                        description = ""
                        isPublic = false
                        showDialog = false
                    } else {
                        toast.show()
                    }
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showDialog = false
                    name = ""
                    description = ""
                    isPublic = false
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}
