package com.example.bdmi.ui.home.movie_details

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun PersonDetails(
    navController: NavController,
    onMovieClick: (Int) -> Unit,
    showFilters: Boolean,
    onShowFiltersChanged: (Boolean) -> Unit
) {
    LazyColumn {
    }
}