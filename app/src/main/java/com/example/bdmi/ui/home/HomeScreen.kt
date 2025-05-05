package com.example.bdmi.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
    var showMenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.refreshHome() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimens.medium2)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = uiState.category.displayName,
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.weight(1f)
            )
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Select category",
                        modifier = Modifier.size(dimens.iconMedium)
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    MovieCategory.entries.forEach { category ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = category.displayName,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            onClick = {
                                viewModel.setCategory(category)
                                showMenu = false
                            })
                    }
                }
            }
        }

        Spacer(Modifier.height(dimens.small3))

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
                    movies = uiState.movies.take(uiConstants.moviesShown),
                    onMovieClick = onMovieClick,
                )
            }
        }
    }
}
