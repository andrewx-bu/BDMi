package com.example.bdmi.ui.composables.movie_detail

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bdmi.SessionViewModel
import com.example.bdmi.data.api.models.MovieDetails
import com.example.bdmi.data.repositories.CustomList
import com.example.bdmi.data.repositories.MediaItem
import com.example.bdmi.data.repositories.MovieReview
import com.example.bdmi.data.repositories.Review
import com.example.bdmi.data.utils.createReviewObjects
import com.example.bdmi.ui.home.MovieDetailViewModel
import com.example.bdmi.ui.theme.dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuButton(
    sessionViewModel: SessionViewModel,
) {
    val movieDetailViewModel: MovieDetailViewModel = hiltViewModel()
    var expanded by remember { mutableStateOf(false) }
    var showWatchlists by remember { mutableStateOf(false) }
    var showWriteReviewSheet by remember { mutableStateOf(false) }
    var showRatingSheet by remember { mutableStateOf(false) }
    val watchlists = sessionViewModel.watchlists.collectAsState()
    val userId = sessionViewModel.userInfo.collectAsState().value?.userId
    val userInfo = sessionViewModel.userInfo.collectAsState().value
    val loggedIn = sessionViewModel.isLoggedIn.collectAsState().value
    val movieDetails = sessionViewModel.selectedMovie.collectAsState().value
    val userReview = sessionViewModel.selectedMovieReview.collectAsState().value

    if (movieDetails != null) {
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
                    expanded = false
                    showRatingSheet = true
                }
            )
            DropdownMenuItem(
                text = { Text(text = if (userReview != null) "Edit Review" else "Add Review") },
                onClick = {
                    expanded = false
                    showWriteReviewSheet = true
                }
            )
            if (userReview != null) {
                DropdownMenuItem(
                    text = { Text("Delete Review") },
                    onClick = {
                        expanded = false
                        movieDetailViewModel.deleteReview(
                            userId.toString(),
                            movieDetails.id
                        ) {
                            if (it) {
                                Log.d("MenuButton", "Review deleted successfully")
                            }
                        }
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
        WatchlistDropdown(userId.toString(), watchlists.value, movieDetails) {
            showWatchlists = false
        }
    }

    if (showWriteReviewSheet && movieDetails != null && userInfo != null) {
        ModalBottomSheet(
            onDismissRequest = { showWriteReviewSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            WriteReview(
                onConfirm = { review ->
                    Log.d("WriteReview", "Create view button clicked")
                    showWriteReviewSheet = false
                    val (uReview, movieReview) = createReviewObjects(
                        userInfo = userInfo,
                        movieDetails = movieDetails,
                        review = review
                    )
                    movieDetailViewModel.createReview(
                        userId.toString(),
                        movieDetails.id,
                        userReview = uReview,
                        movieReview = movieReview
                    ) {
                        if (it) {
                            Log.d("WriteReview", "Review created successfully")
                        }
                    }
                },
                onDismiss = {
                    showWriteReviewSheet = false
                }
            )
        }
    }

    if (showRatingSheet && movieDetails != null) {
        ModalBottomSheet(
            onDismissRequest = { showRatingSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            WriteRating(
                onConfirm = { rating ->
                    showWriteReviewSheet = false
                    movieDetailViewModel.createRating(
                        userId.toString(),
                        movieDetails.id,
                        rating
                    ) {}
                },
                onDismiss = {
                    showWriteReviewSheet = false
                }
            )
        }
    }

}

@Composable
fun WatchlistDropdown(
    userId: String, watchlists: List<CustomList>, movieDetails: MovieDetails,
    onWatchlistClose: () -> Unit
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
                        userId,
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

@Composable
fun WriteReview(
    userReview: MovieReview? = null,
    onConfirm: (Review) -> Unit, // title, review, rating, spoiler
    onDismiss: () -> Unit = {}
) {
    val title = remember {
        if (userReview != null) mutableStateOf(userReview.reviewTitle)
        else mutableStateOf("")
    }
    val review = remember {
        if (userReview != null) mutableStateOf(userReview.reviewText)
        else mutableStateOf("")
    }
    val rating = remember {
        if (userReview != null) mutableFloatStateOf(userReview.rating)
        else mutableFloatStateOf(0f)
    }
    val spoiler = remember {
        if (userReview != null) mutableStateOf(userReview.spoiler)
        else mutableStateOf(false)
    }
    val isReviewValid = review.value.length >= 2
    val isTitleValid = title.value.isNotBlank()
    val isRatingValid = rating.floatValue > 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimens.medium3),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Write a Review", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(dimens.medium3))

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

        Spacer(modifier = Modifier.height(dimens.medium3))

        TextField(
            value = review.value,
            onValueChange = { review.value = it },
            label = { Text("Review (2+ characters)") },
            modifier = Modifier
                .fillMaxWidth()
                .height(dimens.movieRowHeight),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            )
        )

        Text(
            text = "${review.value.length}/2",
            style = MaterialTheme.typography.titleMedium,
            color = if (isReviewValid) Color.Green else Color.Red,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimens.small2)
        )

        Spacer(modifier = Modifier.height(dimens.medium3))

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
                .padding(vertical = dimens.small3)
        ) {
            Text("Spoiler?", modifier = Modifier.weight(1f))
            Switch(
                checked = spoiler.value,
                onCheckedChange = { spoiler.value = it }
            )
        }

        Spacer(modifier = Modifier.height(dimens.medium3))

        Button(
            onClick = {
                onConfirm(
                    Review(
                        reviewTitle = title.value.trim(),
                        reviewText = review.value.trim(),
                        rating = rating.floatValue,
                        spoiler = spoiler.value
                    )
                )
                onDismiss()
            },
            enabled = isReviewValid && isTitleValid && isRatingValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Confirm")
        }
    }
}

@Composable
fun WriteRating(
    onConfirm: (Float) -> Unit,
    onDismiss: () -> Unit = {}
) {
    val rating = remember { mutableFloatStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimens.medium3),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Write a Rating", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(dimens.medium3))
        Text("Rating: ${rating.floatValue}")
        StarRating(
            rating = 0f,
            onRatingChanged = { rating.floatValue = it },
            modifier = Modifier.fillMaxWidth(),
            starSize = 48
        )
        Button(
            onClick = {
                onConfirm(rating.floatValue)
                onDismiss()
            }
        ) {
            Text("Confirm")
        }
    }
}

@Preview
@Composable
fun PreviewUI() {
    WriteReview(onConfirm = {})
}