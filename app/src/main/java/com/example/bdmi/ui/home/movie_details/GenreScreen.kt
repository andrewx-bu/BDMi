package com.example.bdmi.ui.home.movie_details

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun GenreMovies(
    onNavigateBack: () -> Unit,
    onMovieClick: (Int) -> Unit,
    genreId: Int
) {
    LazyColumn {
        item { Text("$genreId") }
    }
}