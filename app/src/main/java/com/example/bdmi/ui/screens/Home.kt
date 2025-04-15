package com.example.bdmi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.bdmi.data.api.Movie
import com.example.bdmi.data.utils.ImageURLHelper
import com.example.bdmi.ui.theme.Spacing
import com.example.bdmi.ui.theme.UIConstants
import com.example.bdmi.ui.viewmodels.HomeViewModel
import com.valentinilk.shimmer.shimmer

@Composable
fun HomeScreen(onMovieClick: (Int) -> Unit = {}) {
    val viewModel: HomeViewModel = hiltViewModel()
    val movies by viewModel.movies.collectAsState()
    val isLoading by viewModel.isLoadingMovies.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadMovies()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.medium)
    ) {
        Text(
            text = "Popular this week",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(Spacing.small))

        MovieGrid(movies = movies.take(UIConstants.MOVIESSHOWN), onMovieClick = onMovieClick, isLoading = isLoading)
    }
}

@Composable
fun MovieGrid(movies: List<Movie>, onMovieClick: (Int) -> Unit, isLoading: Boolean) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(UIConstants.MOVIECOLUMNS),
        verticalArrangement = Arrangement.spacedBy(Spacing.small),
        horizontalArrangement = Arrangement.spacedBy(Spacing.small)
    ) {
        items(movies) { movie ->
            MovieItem(
                title = movie.title,
                posterPath = movie.posterPath,
                isLoading = isLoading,
                onClick = { onMovieClick(movie.id) }
            )
        }
    }
}

@Composable
fun MovieItem(title: String, posterPath: String?, isLoading: Boolean, onClick: () -> Unit) {
    val imageUrl = ImageURLHelper.getPosterURL(posterPath)

    val modifier = Modifier
        .aspectRatio(UIConstants.POSTERSASPECTRATIO)
        .clip(RoundedCornerShape(Spacing.medium))
        .background(MaterialTheme.colorScheme.surfaceVariant)
        .clickable(enabled = !isLoading) { onClick() }

    Box(
        modifier = if (isLoading) modifier.shimmer().then(modifier) else modifier,
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize().background(Color.LightGray))
            }

            imageUrl.isNotEmpty() -> {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            else -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Movie,
                        contentDescription = "No poster available",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(UIConstants.noPosterIconSize)
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(Spacing.extraSmall),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}