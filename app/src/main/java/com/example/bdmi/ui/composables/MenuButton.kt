package com.example.bdmi.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bdmi.UserViewModel
import com.example.bdmi.data.api.models.MovieDetails
import com.example.bdmi.data.repositories.MediaItem
import com.example.bdmi.ui.home.MovieDetailViewModel
import com.example.bdmi.ui.theme.dimens

// TODO: Integrate with outer scaffold
// TODO: Issue with dropdown menu overlapping
@Composable
fun MenuButton(
    userPrivileges: Boolean,
    userViewModel: UserViewModel?,
    movieDetails: MovieDetails?,
) {
    val movieDetailViewModel: MovieDetailViewModel = hiltViewModel()
    var expanded by remember { mutableStateOf(false) }
    var showWatchlists by remember { mutableStateOf(false) }
    val watchlists = movieDetailViewModel.lists.collectAsState()
    val userId = userViewModel?.userInfo?.collectAsState()?.value?.userId
    LaunchedEffect(Unit) {
        if (userId != null)
            movieDetailViewModel.getLists(userId.toString())
    }
    IconButton(
        onClick = { expanded = true },
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.background.copy(alpha = 0.5f), CircleShape
            )
            .size(MaterialTheme.dimens.iconLarge)
    ) {
        Icon(
            imageVector = Icons.Default.MoreHoriz,
            contentDescription = "Menu",
            modifier = Modifier.size(MaterialTheme.dimens.iconSmall),
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = {
            expanded = false
            showWatchlists = false
        }
    ) {
        if (userPrivileges) {
            DropdownMenuItem(
                text = { Text("Add to Watchlist") },
                onClick = {
                    showWatchlists = true
                }
            )
        }
    }

    if (showWatchlists && movieDetails != null) {
        DropdownMenu(
            expanded = true,
            onDismissRequest = { showWatchlists = false },
        ) {
            watchlists.value.forEach { list ->
                DropdownMenuItem(
                    text = { Text(list.name) },
                    onClick = {
                        movieDetailViewModel.addToWatchlist(
                            userId.toString(),
                            list.listId,
                            MediaItem(
                                id = movieDetails.id,
                                title = movieDetails.title,
                                posterPath = movieDetails.posterPath.toString(),
                                releaseDate = movieDetails.releaseDate
                            )
                        )
                        showWatchlists = false
                        expanded = false
                    }
                )
            }
        }
    }
}