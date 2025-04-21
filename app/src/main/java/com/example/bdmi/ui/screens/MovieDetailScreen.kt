package com.example.bdmi.ui.screens

import android.content.Intent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.bdmi.UserViewModel
import com.example.bdmi.data.api.ImageURLHelper
import com.example.bdmi.data.api.MovieDetails
import com.example.bdmi.ui.theme.dimens
import com.example.bdmi.ui.theme.uiConstants
import com.example.bdmi.data.repositories.MediaItem
import com.example.bdmi.ui.DotsIndicator
import com.example.bdmi.ui.ErrorMessage
import com.example.bdmi.ui.GenreChip
import com.example.bdmi.ui.ReviewCard
import com.example.bdmi.ui.ShimmeringDivider
import com.example.bdmi.ui.fadingEdge
import com.spr.jetpack_loading.components.indicators.BallPulseSyncIndicator
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MovieDetailScreen(
    userViewModel: UserViewModel? = null,
    movieId: Int,
    onNavigateBack: () -> Unit
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val uiState by viewModel.detailUIState.collectAsState()
    var userPrivileges by remember { mutableStateOf(false) }

    LaunchedEffect(movieId) {
        launch {viewModel.refreshDetails(movieId)}
        if (userViewModel != null) {
            if (userViewModel.userInfo.value != null) {
                userPrivileges = true
            }
        }
    }

    val details = uiState.details
    val error = uiState.error
    val isLoading = uiState.isLoading

    when {
        error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ErrorMessage(
                    message = error.toString(),
                    onRetry = { viewModel.refreshDetails(movieId) }
                )
            }
        }

        isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = MaterialTheme.dimens.loadingOffset),
                contentAlignment = Alignment.Center
            ) {
                BallPulseSyncIndicator(color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }

        details != null -> {
            // String mangling done by ChatGPT
            val hasBackdrop = details.backdropPath?.isNotEmpty() == true
            // Handle multiple directors
            val directors = details.credits.crew
                .filter { it.job.equals("director", ignoreCase = true) }
                .joinToString(", ") { it.name }
                .ifEmpty { "Unknown" }
            // en-US by default. Extract YT key from most recent official trailer
            val trailerKey = details.videos.results.filter {
                it.site.equals("YouTube", ignoreCase = true) &&
                        it.type.equals("Trailer", ignoreCase = true) && it.official
            }
                .maxByOrNull { it.publishedAt }?.key
            // Extract MPAA certification from US release
            val us = details.releaseDates.results.firstOrNull { it.iso31661 == "US" }
            val certification =
                us?.releaseDates?.firstOrNull()?.certification.takeUnless { it?.isBlank() == true }
                    ?: "NR"

            LazyColumn {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        TopSection(
                            details = details,
                            hasBackdrop = hasBackdrop,
                            directors = directors,
                            trailerKey = trailerKey,
                            certification = certification
                        )
                        TempTopBar(userPrivileges, userViewModel, details, onNavigateBack)
                    }
                }

                item {
                    Spacer(
                        modifier = Modifier.height(
                            if (hasBackdrop) MaterialTheme.dimens.posterRowSpacer
                            else MaterialTheme.dimens.posterRowSpacerAlt
                        )
                    )
                    MovieDescription()
                    ShimmeringDivider()
                }

                item {
                    val reviews = listOf(
                        "Chicken Jockey Chicken Jockey Chicken Jockey Chicken Jockey " +
                                "Chicken Jockey CHICKEN JOCKEY CHICKEN JOCKEY CHICKEN JOCKEY",
                        "Flint and Steel",
                        "Ender Pearl",
                        "Water Bucket Release",
                        "Diamond Armor, Full Set"
                    )

                    ReviewCarousel(
                        reviews = reviews,
                        autoScrollDelay = MaterialTheme.uiConstants.reviewScrollDelay
                    )
                }
            }
        }

        else -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ErrorMessage(
                    message = "An internal error occurred. No details available",
                    onRetry = { viewModel.refreshDetails(movieId) }
                )
            }
        }
    }
}

// TODO: Integrate with scaffold
@Composable
fun TempTopBar(userPrivileges: Boolean, userViewModel: UserViewModel?, movieDetails: MovieDetails?, onNavigateBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.dimens.medium3),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back Button
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.background.copy(alpha = 0.5f), CircleShape
                )
                .size(MaterialTheme.dimens.iconLarge)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(MaterialTheme.dimens.iconSmall),
            )
        }

        // Menu Button
        MenuButton(userPrivileges, userViewModel, movieDetails)
    }
}

@Composable
fun TopSection(
    details: MovieDetails,
    hasBackdrop: Boolean,
    directors: String,
    trailerKey: String?,
    certification: String
) {
    val backdropURL = ImageURLHelper.getBackdropURL(details.backdropPath)

    // Image fades at the bottom
    val bottomFadeBrush = Brush.verticalGradient(
        0.75f to Color.Black,
        1f to Color.Transparent.copy(alpha = 0.2f)
    )

    // Backdrop box
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(MaterialTheme.uiConstants.backdropAspectRatio)
    ) {
        if (hasBackdrop) {
            AsyncImage(
                model = backdropURL,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .fadingEdge(bottomFadeBrush),
                contentScale = ContentScale.Crop
            )
        }
    }

    // Poster row
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(MaterialTheme.dimens.posterSize)
            .offset(
                y = if (hasBackdrop) MaterialTheme.dimens.posterRowOffset
                else MaterialTheme.dimens.posterRowOffsetAlt
            )
            .padding(
                start = MaterialTheme.dimens.medium3,
                end = MaterialTheme.dimens.small3
            )
    ) {
        MoviePoster(
            title = details.title,
            posterPath = details.posterPath,
            onClick = {}
        )

        Spacer(modifier = Modifier.width(MaterialTheme.dimens.medium3))

        // Column fades upwards into the backdrop
        val topFadeBrush = Brush.verticalGradient(
            colorStops = arrayOf(
                0f to Color.Transparent.copy(alpha = 0.2f),
                0.2f to Color.Black,
            )
        )

        // Details column
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(top = MaterialTheme.dimens.large3)
                .fadingEdge(topFadeBrush)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.small2)
        ) {
            Spacer(Modifier.height(MaterialTheme.dimens.medium3))

            // Movie Title
            Text(
                text = details.title,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Genre Chips
            LazyRow(
                modifier = Modifier.padding(bottom = MaterialTheme.dimens.small2),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.small3)
            ) {
                items(details.genres) { genre ->
                    GenreChip(name = genre.name, onClick = {})
                }
            }

            Spacer(Modifier.height(MaterialTheme.dimens.small1))

            // Release date, director
            Text(
                text = "${details.releaseDate} | DIRECTED BY",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            )

            Text(
                text = directors,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Trailer button, runtime, MPAA rating
            Row(verticalAlignment = Alignment.CenterVertically) {
                val context = LocalContext.current

                // Only shimmer if trailer available
                val iconModifier = Modifier
                    .size(MaterialTheme.dimens.iconTiny)
                    .let { base -> if (trailerKey != null) base.shimmer() else base }

                Button(
                    onClick = {
                        val url = "https://www.youtube.com/watch?v=$trailerKey"
                        val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent, null)
                    },
                    enabled = trailerKey != null,
                    modifier = Modifier
                        .size(
                            width = MaterialTheme.dimens.buttonWidthSmall,
                            height = MaterialTheme.dimens.buttonHeightSmall
                        ),
                    contentPadding = PaddingValues(
                        start = MaterialTheme.dimens.small2,
                        end = MaterialTheme.dimens.small3
                    ),
                    shape = RoundedCornerShape(MaterialTheme.dimens.small3),
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.errorContainer,
                        disabledContentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        modifier = iconModifier
                    )
                    Text("TRAILER", style = MaterialTheme.typography.bodyLarge)
                }

                Spacer(modifier = Modifier.width(MaterialTheme.dimens.small3))

                Text(
                    text = "${details.runtime} min | $certification",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                )
            }
        }
    }
}

// TODO: Implement
@Composable
fun MovieDescription() {

}

// TODO: Implement horizontal pager functionality?
@Composable
fun ReviewCarousel(
    reviews: List<String>,
    currentIndex: Int = 0,
    onIndexChanged: (Int) -> Unit = {},
    autoScrollDelay: Long
) {
    var selectedIndex by remember { mutableIntStateOf(currentIndex) }

    LaunchedEffect(selectedIndex) {
        while (true) {
            delay(autoScrollDelay)
            selectedIndex = (selectedIndex + 1) % reviews.size
            onIndexChanged(selectedIndex)
        }
    }

    Spacer(Modifier.height(MaterialTheme.dimens.medium3))

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Crossfade(targetState = reviews[selectedIndex]) { reviewText ->
            ReviewCard(text = reviewText)
        }

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.small3))

        DotsIndicator(
            numDots = reviews.size,
            currentIndex = selectedIndex,
            onDotClick = { index ->
                selectedIndex = index
                onIndexChanged(index)
            }
        )
    }
}

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
                                releaseDate = movieDetails.releaseDate.toString()
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

/*
@Composable
fun MovieBackdrop(movieDetails: MovieDetails?, onNavigateBack: () -> Unit) {
    val backdropURL = ImageURLHelper.getBackdropURL(movieDetails?.backdropPath)

    val fadeBrush = Brush.verticalGradient(
        0.75f to Color.Black,
        1f to Color.Transparent.copy(alpha = 0.2f)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(UIConstants.BACKDROPASPECTRATIO)
    ) {
        if (backdropURL.isNotEmpty()) {
            AsyncImage(
                model = backdropURL,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .fadingEdge(fadeBrush),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        imageVector = Icons.Default.Movie,
                        contentDescription = "No backdrop available",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(UIConstants.noBackdropIconSize)
                    )
                    Spacer(modifier = Modifier.height(Spacing.small))
                    if (movieDetails != null) {
                        Text(
                            text = movieDetails.title,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(horizontal = Spacing.small),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.medium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back Button
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.background.copy(alpha = 0.5f), CircleShape
                )
                .size(UIConstants.backdropButtonSize)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(UIConstants.backdropIconSize),
            )
        }
 */