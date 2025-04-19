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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.bdmi.data.api.ImageURLHelper
import com.example.bdmi.ui.theme.dimens
import com.example.bdmi.ui.theme.uiConstants
import com.example.bdmi.ui.viewmodels.HomeViewModel
import com.spr.jetpack_loading.components.indicators.BallPulseSyncIndicator
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.delay

@Composable
fun MovieDetailScreen(
    movieId: Int,
    onNavigateBack: () -> Unit
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val detailUIState by viewModel.detailUIState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshDetails(movieId)
    }

    when {
        detailUIState.error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ErrorMessage(
                    message = detailUIState.error.toString(),
                    onRetry = { viewModel.refreshDetails(movieId) }
                )
            }
        }

        detailUIState.isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = MaterialTheme.dimens.loadingOffset),
                contentAlignment = Alignment.Center
            ) {
                BallPulseSyncIndicator(color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }

        else -> {
            val hasBackdrop = detailUIState.hasBackdrop
            LazyColumn {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        TopSection(detailState = detailUIState, hasBackdrop = hasBackdrop)
                        TempTopBar(onNavigateBack)
                    }
                }

                item {
                    Spacer(
                        modifier = Modifier.height(
                            if (hasBackdrop == true) MaterialTheme.dimens.posterRowSpacer
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
    }
}

// TODO: Integrate with scaffold
@Composable
fun TempTopBar(onNavigateBack: () -> Unit) {
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
        IconButton(
            onClick = { /* TODO: Implement functionality */ },
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
    }
}

@Composable
fun TopSection(detailState: HomeViewModel.DetailUIState, hasBackdrop: Boolean?) {
    val movieDetails = detailState.movieDetails
    val backdropURL = ImageURLHelper.getBackdropURL(movieDetails?.backdropPath)

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
        if (hasBackdrop == true) {
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
                y = if (hasBackdrop == true) MaterialTheme.dimens.posterRowOffset
                else MaterialTheme.dimens.posterRowOffsetAlt
            )
            .padding(
                start = MaterialTheme.dimens.medium3,
                end = MaterialTheme.dimens.small3
            )
    ) {
        // Display poster if exists, otherwise placeholder
        if (movieDetails != null) {
            MoviePoster(
                title = movieDetails.title,
                posterPath = movieDetails.posterPath,
                onClick = {}
            )
        } else {
            MoviePoster(
                title = "",
                posterPath = null,
                onClick = {}
            )
        }

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
            if (movieDetails != null) {
                Spacer(Modifier.height(MaterialTheme.dimens.medium3))

                // Movie Title
                Text(
                    text = movieDetails.title,
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Genre Chips
                LazyRow(
                    modifier = Modifier.padding(bottom = MaterialTheme.dimens.small2),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.small3)
                ) {
                    items(movieDetails.genres) { genre ->
                        GenreChip(name = genre.name, onClick = {})
                    }
                }

                Spacer(Modifier.height(MaterialTheme.dimens.small1))

                // Release date, director
                Text(
                    text = "${movieDetails.releaseDate} | DIRECTED BY",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                )

                Text(
                    text = detailState.directors,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Trailer button, runtime, MPAA rating
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val context = LocalContext.current

                    Button(
                        // TODO: Add Videos Endpoint
                        onClick = {
                            val intent =
                                Intent(Intent.ACTION_VIEW, "https://youtube.com".toUri())
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent, null)
                        },
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
                            disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            modifier = Modifier
                                .size(MaterialTheme.dimens.iconTiny)
                                .shimmer()
                        )
                        Text("TRAILER", style = MaterialTheme.typography.bodyLarge)
                    }

                    Spacer(modifier = Modifier.width(MaterialTheme.dimens.small3))

                    Text(
                        // TODO: Add MPAA Rating Endpoint
                        text = "${movieDetails.runtime} min | R",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    )
                }
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
