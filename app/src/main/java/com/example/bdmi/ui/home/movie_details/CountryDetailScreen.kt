package com.example.bdmi.ui.home.movie_details

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun CountryDetailScreen(
    onMovieClick: (Int) -> Unit,
    countryName: String
) {
    LazyColumn {
        item { Text(countryName) }
    }
}