package com.example.bdmi.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.bdmi.screens.HomeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeScreen() {
    val viewModel: HomeViewModel = viewModel()
    val movies by viewModel.movies.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(movies) { movie ->
            Text(text = movie.title)
        }
    }
}