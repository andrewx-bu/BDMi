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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.bdmi.data.api.Movie
import com.example.bdmi.data.utils.ImageURLHelper
import com.example.bdmi.ui.viewmodels.HomeViewModel

@Composable
fun HomeScreen(onMovieClick: (Int) -> Unit = {}) {
    val viewModel: HomeViewModel = hiltViewModel()
    val movies by viewModel.movies.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadMovies()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        Text(
            text = "Popular this week",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(8.dp))

        if (movies.isNotEmpty()) {
            MovieGrid(movies = movies.take(18), onMovieClick = onMovieClick)
        }
    }
}

@Composable
fun MovieGrid(movies: List<Movie>, onMovieClick: (Int) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(movies) { movie ->
            MovieItem(
                title = movie.title,
                posterPath = movie.posterPath,
                onClick = { onMovieClick(movie.id) }
            )
        }
    }
}

@Composable
fun MovieItem(title: String, posterPath: String?, onClick: () -> Unit) {
    val imageUrl = ImageURLHelper.getPosterURL(posterPath)

    Box(
        modifier = Modifier
            .aspectRatio(2 / 3f)
            .clickable(onClick = onClick)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl.isNotEmpty()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Movie,
                    contentDescription = "No poster available",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(50.dp)
                )
                Text(
                    text = title,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(5.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}