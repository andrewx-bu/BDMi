package com.example.bdmi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.layout.ContentScale
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.bdmi.data.api.CastMember
import com.example.bdmi.data.api.CrewMember
import com.example.bdmi.data.api.MovieDetails
import com.example.bdmi.data.utils.ImageURLHelper
import com.example.bdmi.ui.theme.Spacing
import com.example.bdmi.ui.theme.UIConstants
import com.example.bdmi.ui.viewmodels.HomeViewModel
import com.spr.jetpack_loading.components.indicators.PulsatingDot
import com.valentinilk.shimmer.LocalShimmerTheme
import com.valentinilk.shimmer.defaultShimmerTheme
import com.valentinilk.shimmer.shimmer

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
                    cast = creditsUIState.cast,
                    crew = creditsUIState.crew,
                    isLoading = detailUIState.isLoading || creditsUIState.isLoading,
                )
            }
        }
    }
}

@Composable
fun MovieBackdrop(movieDetails: MovieDetails?, onNavigateBack: () -> Unit, isLoading: Boolean) {
    val backdropURL = ImageURLHelper.getBackdropURL(movieDetails?.backdropPath)

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(UIConstants.BACKDROPASPECTRATIO)
        ) {
            when {
                isLoading -> {
                    val shimmerTheme = defaultShimmerTheme.copy(
                        shaderColors = listOf(
                            MaterialTheme.colorScheme.secondaryContainer,
                            MaterialTheme.colorScheme.tertiaryContainer,
                            MaterialTheme.colorScheme.inversePrimary,
                        ),
                        blendMode = BlendMode.SrcOver
                    )
                    CompositionLocalProvider(LocalShimmerTheme provides shimmerTheme) {
                        Box(
                            modifier = Modifier
                                .shimmer()
                                .fillMaxSize()
                        )
                    }
                    Box(
                        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        PulsatingDot(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            ballDiameter = UIConstants.LOADINGBALLDIAMETER,
                            horizontalSpace = UIConstants.LOADINGBALLHSPACE,
                            animationDuration = UIConstants.LOADINGBALLDURATION,
                            minAlpha = UIConstants.LOADINGBALLMINALPHA,
                            maxAlpha = UIConstants.LOADINGBALLMAXALPHA
                        )
                    }
                }

                backdropURL.isNotEmpty() -> {
                    AsyncImage(
                        model = backdropURL,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                else -> {
                    // No backdrop
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
                        .size(UIConstants.iconButtonSize)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.size(UIConstants.iconSize),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Menu Button
                IconButton(
                    onClick = { /* TODO: Add functionality */ },
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                            CircleShape
                        )
                        .size(UIConstants.iconButtonSize)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreHoriz,
                        contentDescription = "Menu",
                        modifier = Modifier.size(UIConstants.iconSize),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun PosterRow(
    movieDetails: MovieDetails?,
    cast: List<CastMember>,
    crew: List<CrewMember>,
    isLoading: Boolean
) {
    Row(
        modifier = Modifier
            .height(UIConstants.posterSize)
            .offset(y = UIConstants.posterRowOffset)
            .padding(horizontal = Spacing.large)
    ) {
        if (movieDetails != null) {
            MoviePoster(
                title = movieDetails.title,
                posterPath = movieDetails.posterPath,
                isLoading = isLoading
            ) { }
        } else {
            MoviePoster(
                title = "",
                posterPath = null,
                isLoading = true,
                onClick = {}
            )
        }
    }
}