package com.example.bdmi.ui.custom_lists

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bdmi.UserViewModel
import com.example.bdmi.data.repositories.CustomList
import com.example.bdmi.data.repositories.MediaItem
import com.example.bdmi.ui.theme.Spacing
import com.example.bdmi.ui.ErrorMessage
import com.example.bdmi.ui.screens.MoviePoster
import com.example.bdmi.ui.theme.UIConstants
import com.spr.jetpack_loading.components.indicators.BallPulseSyncIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomListScreen(
    userViewModel: UserViewModel = hiltViewModel(),
    listId: String,
    userId: String,
    customListInfo: CustomList,
    onMovieClick: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    val currentUserId = userViewModel.userInfo.collectAsState().value?.userId
    val customListViewModel: CustomListViewModel = hiltViewModel()
    val uiState = customListViewModel.listUIState.collectAsState().value
    val listItems = customListViewModel.listItems.collectAsState().value
    val listInfo = customListViewModel.listInfo.collectAsState().value
    val displayGridView = customListViewModel.displayGridView.collectAsState().value
    val editPrivileges = customListViewModel.editPrivilege.collectAsState().value

    LaunchedEffect(listId) {
        customListViewModel.loadList(userId, listId)
        customListViewModel.setListInfo(customListInfo)
        customListViewModel.setEditPrivileges(currentUserId!!, userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = listInfo?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                    }
                },
                // TODO: Toggle grid and list view. Add sliding animation
                actions = {
                    if (editPrivileges) {
                        EditButton(
                            currentInfo = listInfo!!,
                            onClick = { newList: CustomList ->
                                customListViewModel.updateListInfo(userId, listId, newList)
                            }
                        )
                    }

                    IconButton(onClick = { customListViewModel.toggleDisplay() }) {
                        Icon(
                            imageVector = if (displayGridView) Icons.Default.GridView else Icons.AutoMirrored.Filled.ViewList,
                            contentDescription = "Toggle View"
                        )
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)

        ) {
            // Display list description
            Text(
                text = listInfo?.description ?: "",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(Spacing.small))

            when {
                uiState.error != null -> {
                    ErrorMessage(message = uiState.error.toString(), onRetry = { customListViewModel.loadList(userId, listId) })
                }

                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        BallPulseSyncIndicator(color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }

                else -> {
                    if (displayGridView) {
                        MediaGrid(
                            mediaItems = listItems,
                            onMovieClick = onMovieClick,
                        )
                    } else {
                        MediaList(
                            mediaItems = listItems,
                            onMovieClick = onMovieClick,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MediaGrid(mediaItems: List<MediaItem>, onMovieClick: (Int) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(UIConstants.MOVIECOLUMNS),
        verticalArrangement = Arrangement.spacedBy(Spacing.small),
        horizontalArrangement = Arrangement.spacedBy(Spacing.small)
    ) {
        items(mediaItems) { movie ->
            MoviePoster(
                title = movie.title,
                posterPath = movie.posterPath,
                onClick = { onMovieClick(movie.id) }
            )
        }
    }
}

@Composable
fun MediaList(mediaItems: List<MediaItem>, onMovieClick: (Int) -> Unit) {
    LazyColumn() {

        items(mediaItems) { movie : MediaItem ->
            MediaListItem(
                media = movie,
                onMovieClick = onMovieClick
            )
        }
    }
}

@Composable
fun MediaListItem(media: MediaItem, onMovieClick: (Int) -> Unit) {
    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .padding(Spacing.small)
            .clickable { onMovieClick(media.id) }
            .fillMaxWidth()
            .height(UIConstants.movieListItemHeight)
    ) {
            MoviePoster(
                title = media.title,
                posterPath = media.posterPath,
                onClick = { onMovieClick(media.id) }
            )
        Text(
            text = media.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = media.releaseDate,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun EditButton(currentInfo: CustomList, onClick: (CustomList) -> Unit) {
    var name by remember { mutableStateOf(currentInfo.name) }
    var description by remember { mutableStateOf(currentInfo.description) }
    var isPublic by remember { mutableStateOf(currentInfo.isPublic) }
    var showDialog by remember { mutableStateOf(false) }

    IconButton(onClick = { onClick(currentInfo) }) {
        Icon(Icons.Default.Edit, contentDescription = "Edit")
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Update List Info") },
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
                    // TODO: Add switch for public/private
                    Text("Public:")
                    Switch(
                        checked = isPublic,
                        onCheckedChange = { isPublic = it }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    onClick(
                        CustomList(
                            listId = currentInfo.listId,
                            name = name,
                            description = description,
                            timestamp = currentInfo.timestamp,
                            isPublic = isPublic
                        )
                    )
                    showDialog = false
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    name = currentInfo.name
                    description = currentInfo.description
                    isPublic = currentInfo.isPublic
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}