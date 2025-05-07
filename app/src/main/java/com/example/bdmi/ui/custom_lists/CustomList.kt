package com.example.bdmi.ui.custom_lists

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bdmi.data.repositories.CustomList
import com.example.bdmi.data.repositories.MediaItem
import com.example.bdmi.ui.composables.ErrorMessage
import com.example.bdmi.ui.composables.home.MoviePoster
import com.example.bdmi.ui.theme.dimens
import com.example.bdmi.ui.theme.uiConstants
import com.spr.jetpack_loading.components.indicators.BallPulseSyncIndicator
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomListScreen(
    currentUserId: String,
    userId: String,
    listId: String,
    onMovieClick: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    val customListViewModel: CustomListViewModel = hiltViewModel()
    val uiState = customListViewModel.listUIState.collectAsState().value
    val listItems = customListViewModel.listItems.collectAsState().value
    val listInfo = customListViewModel.listInfo.collectAsState().value
    val displayGridView = customListViewModel.displayGridView.collectAsState().value
    val editPrivileges = customListViewModel.editPrivilege.collectAsState().value

    LaunchedEffect(listId) {
        launch { customListViewModel.loadList(userId, listId) }
        launch { customListViewModel.loadListInfo(userId, listId) }
        launch { customListViewModel.setEditPrivileges(currentUserId, userId) }
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
                text = listInfo?.name ?: "",
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.weight(1f)
            )
            if (editPrivileges && listInfo != null) {
                EditButton(
                    currentInfo = listInfo,
                    onClick = { newList: CustomList ->
                        customListViewModel.updateListInfo(userId, listId, newList)
                    },
                    onDelete = {
                        customListViewModel.deleteList(userId, listId)
                        onNavigateBack()
                    }
                )
            }

            MediaDisplaySwitchButton(
                isGridView = displayGridView,
                onToggle = { customListViewModel.toggleDisplay() }
            )
        }
            // Display list description
        Text(
            text = listInfo?.description ?: "",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(vertical = dimens.small3)
        )

        Spacer(Modifier.height(dimens.small3))

        when {
            uiState.error != null -> {
                ErrorMessage(
                    message = uiState.error.toString(),
                    onRetry = { customListViewModel.loadList(userId, listId) })
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
                        editPrivileges = editPrivileges,
                        onMovieClick = onMovieClick,
                        onDeleteClick = { itemId: Int ->
                            customListViewModel.removeItemFromList(currentUserId, listId, itemId)
                        }
                    )
                } else {
                    MediaList(
                        mediaItems = listItems,
                        editPrivileges = editPrivileges,
                        onMovieClick = onMovieClick,
                        onDeleteClick = { itemId: Int ->
                            customListViewModel.removeItemFromList(currentUserId, listId, itemId)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MediaGrid(
    mediaItems: List<MediaItem>,
    editPrivileges: Boolean = false,
    onMovieClick: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit
) {
    var selectedMedia by remember { mutableStateOf<MediaItem?>(null) }
    println(selectedMedia)
    if (editPrivileges && selectedMedia != null) {
        AlertDialog(
            onDismissRequest = { selectedMedia = null },
            title = { Text("Delete Movie") },
            text = { Text("Are you sure you want to delete this movie?") },
            confirmButton = {
                if (selectedMedia != null) {
                    TextButton(onClick = {
                        onDeleteClick(selectedMedia?.id!!)
                        selectedMedia = null
                    }) {
                        Text("Delete")
                    }
                }

            },
            dismissButton = {
                TextButton(onClick = { selectedMedia = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(uiConstants.movieColumns),
        verticalArrangement = Arrangement.spacedBy(dimens.small3),
        horizontalArrangement = Arrangement.spacedBy(dimens.small3)
    ) {
        items(mediaItems) { movie ->
            MoviePoster(
                title = movie.title,
                posterPath = movie.posterPath,
                onClick = { onMovieClick(movie.id) },
                onLongPress = {
                    if (editPrivileges)
                        selectedMedia = movie
                }
            )
        }
    }
}


@Composable
fun MediaList(
    mediaItems: List<MediaItem>,
    onMovieClick: (Int) -> Unit,
    editPrivileges: Boolean = false,
    onDeleteClick: (Int) -> Unit
) {
    var selectedMedia by remember { mutableStateOf<MediaItem?>(null) }

    if (editPrivileges && selectedMedia != null) {
        AlertDialog(
            onDismissRequest = { selectedMedia = null },
            title = { Text("Delete Movie") },
            text = { Text("Are you sure you want to delete this movie?") },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteClick(selectedMedia?.id!!)
                    selectedMedia = null
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedMedia = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(dimens.small3),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        items(mediaItems) { movie ->
            MediaListItem(
                media = movie,
                onMovieClick = onMovieClick,
                onLongPress = {
                    if (editPrivileges)
                        selectedMedia = it
                }
            )
        }
    }
}

@Composable
fun MediaListItem(
    media: MediaItem,
    onMovieClick: (Int) -> Unit,
    onLongPress: (MediaItem) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .combinedClickable(
                onClick = { onMovieClick(media.id) },
                onLongClick = { onLongPress(media) }
            )
            .fillMaxWidth()
            .height(75.dp)
    ) {
        MoviePoster(
            title = media.title,
            posterPath = media.posterPath,
            onClick = { onMovieClick(media.id) }
        )
        Text(
            text = media.title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = dimens.small3)
        )
        Text(
            text = media.releaseDate,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(start = dimens.small3)
        )
    }
}


// Edit button for changing list information
@Composable
fun EditButton(currentInfo: CustomList, onClick: (CustomList) -> Unit, onDelete: () -> Unit) {
    var name by remember { mutableStateOf(currentInfo.name) }
    var description by remember { mutableStateOf(currentInfo.description) }
    var isPublic by remember { mutableStateOf(currentInfo.isPublic) }
    var showDialog by remember { mutableStateOf(false) }

    IconButton(onClick = { showDialog = true }) {
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
                    Text("Public:")
                    Switch(
                        checked = isPublic,
                        onCheckedChange = { isPublic = it }
                    )
                }
            },
            confirmButton = {
                Row(

                ) {
                    Button(
                        onClick = {
                            onDelete()
                            showDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red, contentColor = Color.Black)
                    ) {
                        Text("Delete")
                    }

                    Spacer(modifier = Modifier.width(dimens.small3))

                    Button(
                        enabled = name != currentInfo.name || description != currentInfo.description || isPublic != currentInfo.isPublic,
                        onClick = {
                        val newInfo = CustomList(
                            listId = currentInfo.listId,
                            name = name,
                            description = description,
                            numOfItems = currentInfo.numOfItems,
                            timestamp = currentInfo.timestamp,
                            isPublic = isPublic
                        )
                        onClick(newInfo)
                        showDialog = false
                    }) {
                        Text("Confirm")
                    }
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


// Custom Switch button for switching between grid and list view
// ChatGPT helped with the horizontal sliding
@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun MediaDisplaySwitchButton(isGridView: Boolean, onToggle: () -> Unit) {
    val switchWidth = 80.dp
    val switchHeight = switchWidth / 2
    val roundedCorner = 10.dp

    // Animation states
    val offsetX by animateDpAsState(
        targetValue = if (isGridView) 0.dp else switchWidth / 2,
        label = "Switch Animation"
    )
    val startCornerRadius by animateDpAsState(
        targetValue = if (isGridView) 10.dp else 0.dp,
        label = "Corner Radius Animation"
    )
    val endCornerRadius by animateDpAsState(
        targetValue = if (isGridView) 0.dp else 10.dp,
        label = "Corner Radius Animation"
    )
    val cornerRadius = if (isGridView) startCornerRadius else endCornerRadius

    val shape = if (isGridView)
        RoundedCornerShape(topStart = cornerRadius, bottomStart = cornerRadius)
    else
        RoundedCornerShape(topEnd = cornerRadius, bottomEnd = cornerRadius)

    Box(
        modifier = Modifier
            .clickable { onToggle() }
            .width(switchWidth)
            .height(switchHeight)
            .background(
                MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(roundedCorner)
            )
            .clip(RoundedCornerShape(roundedCorner))
    ) {
        Surface(
            modifier = Modifier
                .offset(x = offsetX)
                .width(switchWidth / 2)
                .fillMaxHeight(),
            color = Color.Red,
            shape = shape,
            tonalElevation = 6.dp,
            shadowElevation = 6.dp
        ) {}

        // Icons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = Icons.Default.GridView,
                contentDescription = "Grid View",
                tint = if (isGridView) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .weight(1f)
                    .size(switchHeight * .7f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ViewList,
                contentDescription = "List View",
                tint = if (isGridView) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .weight(1f)
                    .size(switchHeight * .9f)
            )
        }
    }
}

@Preview
@Composable
fun ShowComposable() {
    MediaDisplaySwitchButton(isGridView = true, onToggle = {})
}