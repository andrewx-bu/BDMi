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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.bdmi.data.api.CrewMember
import com.example.bdmi.data.api.MovieDetails
import com.example.bdmi.data.utils.ImageURLHelper
import com.example.bdmi.ui.theme.Spacing
import com.example.bdmi.ui.theme.UIConstants
import com.example.bdmi.ui.viewmodels.HomeViewModel
import androidx.core.net.toUri
import com.spr.jetpack_loading.components.indicators.PulsatingDot

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
                message = detailUIState.error!!,
                onRetry = { viewModel.refreshDetails(movieId) })
        }

        else -> {
            Box(
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            ) {
                MovieBackdrop(
                    detailUIState.movieDetails,
                    onNavigateBack,
                    isLoading = detailUIState.isLoading
                )
                PosterRow(
                    movieDetails = detailUIState.movieDetails,
                    crew = creditsUIState.crew,
                    isLoading = detailUIState.isLoading || creditsUIState.isLoading,
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
fun MovieBackdrop(movieDetails: MovieDetails?, onNavigateBack: () -> Unit, isLoading: Boolean) {
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
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.onSecondary),
                    contentAlignment = Alignment.Center
                ) {
                    PulsatingDot(color = MaterialTheme.colorScheme.secondary)
                }
            }

            backdropURL.isNotEmpty() -> {
                AsyncImage(
                    model = backdropURL,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .fadingEdge(fadeBrush),
                    contentScale = ContentScale.Crop
                )
            }

            else -> {
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
                        MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                        CircleShape
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
                        MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                        CircleShape
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
}

@Composable
fun PosterRow(
    movieDetails: MovieDetails?,
    crew: List<CrewMember>,
    isLoading: Boolean
) {
    // TODO: Handle multiple directors
    val director = remember(crew) {
        crew.firstOrNull { it.job.lowercase() == "director" }?.name ?: "Unknown"
    }

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
                isLoading = isLoading,
                onClick = {}
            )
        } else {
            MoviePoster(
                title = "",
                posterPath = null,
                isLoading = true,
                onClick = {}
            )
        }

        Spacer(modifier = Modifier.width(Spacing.medium))

        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .padding(top = Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.extraSmall)
        ) {
            if (movieDetails != null) {
                item {
                    Spacer(Modifier.height(Spacing.medium))
                }

                item {
                    Text(
                        text = movieDetails.title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(Modifier.height(Spacing.extraSmall))

                    Text(
                        text = "${movieDetails.releaseDate} | DIRECTED BY",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    )

                    Spacer(Modifier.height(Spacing.extraSmall))

                    Text(
                        text = director,
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
                            shape = RoundedCornerShape(Spacing.small),
                            contentPadding = PaddingValues(
                                start = Spacing.extraSmall,
                                end = Spacing.small
                            ),
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                modifier = Modifier.size(UIConstants.trailerButtonSize)
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