package com.example.bdmi.ui.custom_lists

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bdmi.SessionViewModel
import com.example.bdmi.data.repositories.CustomList
import com.example.bdmi.ui.theme.dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistsScreen(
    sessionViewModel: SessionViewModel? = null,
    userId: String? = null,
    onListClick: (Pair<String, String>) -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    val currentUserId =
        if (sessionViewModel != null) sessionViewModel.userInfo.collectAsState().value?.userId
        else userId
    val watchlistViewModel: WatchlistViewModel = hiltViewModel()
    val lists = watchlistViewModel.lists.collectAsState()
//    val editPrivileges = userId != null

    if (sessionViewModel != null) {
        LaunchedEffect(currentUserId) {
            if (currentUserId != null) {
                watchlistViewModel.loadLists(sessionViewModel.watchlists.value)
            }
        }
        LaunchedEffect(lists) {
            if (lists.value.isNotEmpty())
                sessionViewModel.updateWatchlists(lists.value)
        }
    } else {
        LaunchedEffect(currentUserId) {
            if (currentUserId != null) {
                watchlistViewModel.getLists(currentUserId)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Watchlists") },
                actions = {
                    if (sessionViewModel != null) {
                        AddListButton { list: CustomList ->
                            watchlistViewModel.createList(currentUserId.toString(), list)
                        }
                    }
                },
                // Only display if visiting a user's watchlist page
                navigationIcon = {
                    if (userId != null) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        WatchlistList(
            currentUserId.toString(),
            modifier = Modifier.padding(innerPadding),
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
fun WatchlistItem(list: CustomList, onListClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(dimens.medium3)
            .clickable {
                onListClick()
            }
            .fillMaxWidth()
            .height(dimens.movieRowHeight)
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(text = "${list.name} | ${list.numOfItems} movies/shows")
            Text(
                text = list.description,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun AddListButton(onClick: (CustomList) -> Unit) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isPublic by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    val toast = Toast.makeText(LocalContext.current, "Name cannot be empty", Toast.LENGTH_SHORT)
    IconButton(
        onClick = { showDialog = true }
    ) {
        Icon(Icons.Default.Add, contentDescription = "Add List")
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Create List") },
            text = {
                Column {
                    Text("Name:")
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                    )
                    Text("Description:")
                    TextField(
                        value = description,
                        onValueChange = { description = it },
                    )
                    Text("Public:")
                    Switch(
                        checked = isPublic,
                        onCheckedChange = { isPublic = it }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (name != "") {
                        onClick(
                            CustomList(
                                name = name,
                                description = description,
                                isPublic = isPublic
                            )
                        )
                        showDialog = false
                    } else {
                        toast.show()
                    }

                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = {
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