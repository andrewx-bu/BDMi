package com.example.bdmi.ui.home.movie_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bdmi.FilterState
import com.example.bdmi.data.utils.GenreMappings
import com.example.bdmi.ui.composables.ErrorMessage
import com.example.bdmi.ui.composables.LoadingIndicator
import com.example.bdmi.ui.composables.home.MoviePoster
import com.example.bdmi.ui.theme.dimens
import com.example.bdmi.ui.theme.uiConstants

@Composable
fun GenreMovies(
    navController: NavController,
    onMovieClick: (Int) -> Unit,
    showFilters: Boolean,
    onShowFiltersChanged: (Boolean) -> Unit
) {
    val viewModel: GenreViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val gridState = rememberLazyGridState()
    val filters = viewModel.uiState.collectAsState().value.let {
        FilterState(
            it.sortBy,
            it.voteCountGte to it.voteCountLte,
            it.voteAverageGte to it.voteAverageLte
        )
    }

    LaunchedEffect(GenreMappings.getGenreName(viewModel.genreId.toInt())) {
        navController
            .currentBackStackEntry
            ?.savedStateHandle
            ?.set("movie_title", GenreMappings.getGenreName(viewModel.genreId.toInt()))
    }

    when {
        uiState.error != null -> {
            Column(modifier = Modifier.fillMaxHeight()) {
                ErrorMessage(
                    message = uiState.error.toString(),
                    onRetry = { viewModel.refresh() }
                )
            }
        }

        uiState.isLoading -> {
            LoadingIndicator()
        }

        else -> {
            if (showFilters) {
                FilterDialog(
                    current = filters,
                    onDismiss = { onShowFiltersChanged(false) },
                    onApply = { newFilters ->
                        viewModel.setSortBy(newFilters.sortBy)
                        viewModel.setVoteCountRange(
                            newFilters.voteCountRange.first,
                            newFilters.voteCountRange.second
                        )
                        viewModel.setVoteAverageRange(
                            newFilters.voteAvgRange.first,
                            newFilters.voteAvgRange.second
                        )
                        onShowFiltersChanged(false)
                    }
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(uiConstants.movieColumns),
                verticalArrangement = Arrangement.spacedBy(dimens.small3),
                horizontalArrangement = Arrangement.spacedBy(dimens.small3),
                state = gridState,
                modifier = Modifier.padding(horizontal = dimens.medium2)
            ) {
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
