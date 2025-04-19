package com.example.bdmi.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bdmi.data.api.Movie
import com.example.bdmi.ui.theme.dimens
import com.example.bdmi.ui.theme.uiConstants
import com.example.bdmi.ui.viewmodels.HomeViewModel
import com.spr.jetpack_loading.components.indicators.BallPulseSyncIndicator

@Composable
fun HomeScreen(onMovieClick: (Int) -> Unit = {}) {
    val viewModel: HomeViewModel = hiltViewModel()
    val uiState by viewModel.homeUIState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshHome()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MaterialTheme.dimens.medium3)
    ) {
        Text(
            text = "Popular this week",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(MaterialTheme.dimens.small3))

        when {
            uiState.error != null -> {
                ErrorMessage(message = uiState.error.toString(), onRetry = { viewModel.refreshHome() })
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
                MovieGrid(
                    movies = uiState.movies.take(MaterialTheme.uiConstants.moviesShown),
                    onMovieClick = onMovieClick,
                )
            }
        }
    }
}

@Composable
fun MovieGrid(movies: List<Movie>, onMovieClick: (Int) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(MaterialTheme.uiConstants.movieColumns),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.small3),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.small3)
    ) {
        items(movies) { movie ->
            MoviePoster(
                title = movie.title,
                posterPath = movie.posterPath,
                onClick = { onMovieClick(movie.id) }
            )
        }
    }
}
