package com.example.bdmi.ui.home.movie_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bdmi.ui.composables.ErrorMessage
import com.example.bdmi.ui.composables.LoadingIndicator
import com.example.bdmi.ui.composables.home.MoviePoster
import com.example.bdmi.ui.theme.dimens
import com.example.bdmi.ui.theme.uiConstants

@Composable
fun StudioDetails(
    navController: NavController,
    onMovieClick: (Int) -> Unit
) {
    val viewModel: StudioViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val gridState = rememberLazyGridState()

    LaunchedEffect(uiState.company) {
        uiState.company?.name?.let { name ->
            navController
                .currentBackStackEntry
                ?.savedStateHandle
                ?.set("movie_title", name)
        }
    }

    when {
        uiState.error != null -> {
            ErrorMessage(
                message = uiState.error.toString(),
                onRetry = { viewModel.refresh() }
            )
        }

        uiState.isLoading -> {
            LoadingIndicator()
        }

        else -> {
            LazyVerticalGrid(
                columns = GridCells.Fixed(uiConstants.movieColumns),
                verticalArrangement = Arrangement.spacedBy(dimens.small3),
                horizontalArrangement = Arrangement.spacedBy(dimens.small3),
                state = gridState,
            ) {
                uiState.company?.let { company ->
                    item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                        Text(
                            text = company.name,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                        )
                    }
                }

                items(uiState.movies) { movie ->
                    MoviePoster(
                        title = movie.title,
                        posterPath = movie.posterPath,
                        onClick = { onMovieClick(movie.id) }
                    )
                }
            }

            // From ChatGPT: Used for pagination.
            // When within 3 items of the end, fire loadNextPage()
            val shouldLoadMore by remember {
                derivedStateOf {
                    val lastVisible = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                    lastVisible >= uiState.movies.lastIndex - 2 &&
                            !uiState.isLoading &&
                            uiState.page < uiState.totalPages
                }
            }

            LaunchedEffect(shouldLoadMore) {
                if (shouldLoadMore) {
                    viewModel.loadNextPage()
                }
            }
        }
    }
}
