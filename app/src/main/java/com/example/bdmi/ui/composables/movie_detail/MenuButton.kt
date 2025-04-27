package com.example.bdmi.ui.composables.movie_detail

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bdmi.SessionViewModel
import com.example.bdmi.data.api.models.MovieDetails
import com.example.bdmi.data.repositories.MediaItem
import com.example.bdmi.ui.home.MovieDetailViewModel
import com.example.bdmi.ui.theme.dimens
import com.example.bdmi.data.repositories.CustomList
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

// TODO: Integrate with outer scaffold
// TODO: Issue with dropdown menu overlapping
@Composable
fun MenuButton(
    sessionViewModel: SessionViewModel,
) {
    val movieDetailViewModel: MovieDetailViewModel = hiltViewModel()
    var expanded by remember { mutableStateOf(false) }
    var showWatchlists by remember { mutableStateOf(false) }
    val watchlists = sessionViewModel.watchlists.collectAsState()
    val userId = sessionViewModel.userInfo.collectAsState().value?.userId
    val loggedIn = sessionViewModel.isLoggedIn.collectAsState().value
    val movieDetails = sessionViewModel.selectedMovie.collectAsState().value
    val userReview = sessionViewModel.selectedMovieReview.collectAsState().value

    Log.d("MenuButton", "Movie Details: $movieDetails")
    IconButton(
        onClick = { expanded = true },
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.background.copy(alpha = 0.5f), CircleShape
            )
            .size(dimens.iconLarge)
    ) {
        Icon(
            imageVector = Icons.Default.MoreHoriz,
            contentDescription = "Menu",
            modifier = Modifier.size(dimens.iconSmall),
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
        if (loggedIn && movieDetails != null) {
            DropdownMenuItem(
                text = { Text("Add to Watchlist") },
                onClick = {
                    // Close current dropdown and open watchlist dropdown
                    expanded = false
                    showWatchlists = true
                }
            )
            DropdownMenuItem(
                text = { Text("Add Rating") },
                onClick = {
                    // TODO: Add rating functionality
                }
            )
            DropdownMenuItem(
                text = { Text(text = if (userReview != null) "Edit Review" else "Add Review") },
                onClick = {
                    // TODO: Add review functionality
                }
            )
            if (userReview != null) {
                DropdownMenuItem(
                    text = { Text("Delete Review") },
                    onClick = {
                        // TODO: Delete review functionality
                    }
                )
            }

        } else {
            // TODO: Add future support for non-logged in users
            DropdownMenuItem(
                text = { Text("Login/Register") },
                onClick = {
                    expanded = false
                }
            )
        }
    }

    if (showWatchlists && movieDetails != null) {
        WatchlistDropdown(userId.toString(), watchlists.value, movieDetails) { showWatchlists = false }
    }
}

@Composable
fun WatchlistDropdown(
    userId: String, watchlists: List<CustomList>, movieDetails: MovieDetails,
    onWatchlistClose: () -> Unit,
    ) {
    val movieDetailViewModel: MovieDetailViewModel = hiltViewModel()

    DropdownMenu(
        expanded = true,
        onDismissRequest = { onWatchlistClose() },
    ) {
        watchlists.forEach { list ->
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
                    onWatchlistClose()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteReview(
    onConfirm: (String, String, Float, Boolean) -> Unit, // title, review, rating, spoiler
    onDismiss: () -> Unit = {}
) {
    val title = remember { mutableStateOf("") }
    val review = remember { mutableStateOf("") }
    val rating = remember { mutableFloatStateOf(0f) }
    val spoiler = remember { mutableStateOf(false) }
    val isReviewValid = review.value.length >= 100

    BottomSheetScaffold(
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Write a Review", style = MaterialTheme.typography.titleMedium)

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = title.value,
                    onValueChange = { title.value = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy( // What is this?
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = review.value,
                    onValueChange = { review.value = it },
                    label = { Text("Review (100+ characters)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    )
                )

                Text(
                    text = "${review.value.length}/100",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isReviewValid) Color.Green else Color.Red,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Rating: ${rating.floatValue}")

                StarRating(
                    rating = 0f,
                    onRatingChanged = { rating.floatValue = it },
                    modifier = Modifier.fillMaxWidth(),
                    starSize = 48
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text("Spoiler?", modifier = Modifier.weight(1f))
                    Switch(
                        checked = spoiler.value,
                        onCheckedChange = { spoiler.value = it }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onConfirm(title.value.trim(), review.value.trim(), rating.floatValue, spoiler.value)
                        onDismiss()
                    },
                    enabled = isReviewValid,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Confirm")
                }
            }
        },
        sheetPeekHeight = 0.dp,
        scaffoldState = rememberBottomSheetScaffoldState()
    ) {
        // Empty content behind the bottom sheet
    }
}

@Composable
fun WriteRating() {

}

@Preview
@Composable
fun PreviewUI() {
    WriteReview(onConfirm = { _, _, _, _ -> })
}