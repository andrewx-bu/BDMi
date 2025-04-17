package com.example.bdmi.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.bdmi.data.api.ImageURLHelper
import com.example.bdmi.data.api.MovieDetails
import com.example.bdmi.ui.theme.Spacing
import com.example.bdmi.ui.theme.UIConstants
import com.example.bdmi.ui.viewmodels.HomeViewModel
import com.spr.jetpack_loading.components.indicators.BallPulseSyncIndicator

@Composable
fun MovieDetailScreen(
    movieId: Int,
    onNavigateBack: () -> Unit
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val detailUIState by viewModel.detailUIState.collectAsState()
    val creditsUIState by viewModel.creditsUIState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshDetails(movieId)
        viewModel.refreshCredits(movieId)
    }

    when {
        detailUIState.error != null -> {
            ErrorMessage(
                message = detailUIState.error.toString(),
                onRetry = { viewModel.refreshDetails(movieId) }
            )
        }

        detailUIState.isLoading || creditsUIState.isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = UIConstants.loadingOffset),
                contentAlignment = Alignment.Center
            ) {
                BallPulseSyncIndicator(color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }

        else -> {
            Box(
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            ) {
                MovieBackdrop(
                    detailUIState.movieDetails,
                    onNavigateBack
                )
                PosterRow(
                    creditsState = creditsUIState,
                    movieDetails = detailUIState.movieDetails
                )
            }
        }
    }
}

fun Modifier.fadingEdge(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }

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

        // Menu Button
        IconButton(
            onClick = { /* TODO: Implement functionality */ },
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.background.copy(alpha = 0.5f), CircleShape
                )
                .size(UIConstants.backdropButtonSize)
        ) {
            Icon(
                imageVector = Icons.Default.MoreHoriz,
                contentDescription = "Menu",
                modifier = Modifier.size(UIConstants.backdropIconSize),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun PosterRow(
    creditsState: HomeViewModel.CreditsUIState,
    movieDetails: MovieDetails?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(UIConstants.posterSize)
            .offset(y = UIConstants.posterRowOffset)
            .padding(
                start = Spacing.medium,
                end = Spacing.small
            )
    ) {
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

        Spacer(modifier = Modifier.width(Spacing.medium))

        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .padding(top = Spacing.extraLarge),
            verticalArrangement = Arrangement.spacedBy(Spacing.extraSmall)
        ) {
            if (movieDetails != null) {
                item {
                    Text(
                        text = movieDetails.title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    LazyRow(
                        modifier = Modifier.padding(bottom = Spacing.extraSmall),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.small)
                    ) {
                        items(movieDetails.genres) { genre ->
                            GenreChip(name = genre.name, onClick = {})
                        }
                    }

                    Spacer(Modifier.height(Spacing.extraSmall))

                    Text(
                        text = "${movieDetails.releaseDate} | DIRECTED BY",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    )

                    Spacer(Modifier.height(Spacing.extraSmall))

                    Text(
                        text = creditsState.directors,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                item {
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
                                    width = UIConstants.trailerButtonWidth,
                                    height = UIConstants.trailerButtonHeight
                                ),
                            contentPadding = PaddingValues(
                                start = Spacing.extraSmall,
                                end = Spacing.small
                            ),
                            shape = RoundedCornerShape(Spacing.small),
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
                                modifier = Modifier.size(UIConstants.trailerIconSize)
                            )
                            Text("TRAILER", style = MaterialTheme.typography.labelMedium)
                        }

                        Spacer(modifier = Modifier.width(Spacing.small))

                        Text(
                            // TODO: Add MPAA Rating Endpoint
                            text = "${movieDetails.runtime} min | R",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        )
                    }
                }
            }
        }
    }
}