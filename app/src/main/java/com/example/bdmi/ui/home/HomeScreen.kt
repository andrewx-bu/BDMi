package com.example.bdmi.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bdmi.ui.composables.ErrorMessage
import com.example.bdmi.ui.composables.LoadingIndicator
import com.example.bdmi.ui.composables.home.MovieGrid
import com.example.bdmi.ui.theme.dimens
import com.example.bdmi.ui.theme.uiConstants

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
            .padding(MaterialTheme.dimens.medium2)
    ) {
        // TODO: Make this a row, add a menu button top right
        Text(
            text = "Popular this week",
            style = MaterialTheme.typography.displaySmall
        )

        Spacer(Modifier.height(MaterialTheme.dimens.small3))

        when {
            uiState.error != null -> {
                ErrorMessage(
                    message = uiState.error.toString(),
                    onRetry = { viewModel.refreshHome() }
                )
            }

            uiState.isLoading -> {
                LoadingIndicator()
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
