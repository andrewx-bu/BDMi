package com.example.bdmi.ui.composables.movie_detail.bottom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.bdmi.data.api.models.MoviesResponse
import com.example.bdmi.ui.composables.home.MoviePoster
import com.example.bdmi.ui.theme.dimens
import com.example.bdmi.ui.theme.uiConstants

@Composable
fun ExploreSection(
    similar: MoviesResponse,
    recommended: MoviesResponse,
    onMovieClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimens.medium2)
            .height(dimens.bottomColumnHeight),
    ) {
        item {
            SectionHeader("SIMILAR")
            LazyHorizontalGrid(
                rows = GridCells.Fixed(uiConstants.movieRows),
                horizontalArrangement = Arrangement.spacedBy(dimens.small3),
                verticalArrangement = Arrangement.spacedBy(dimens.small3),
                modifier = Modifier.height(dimens.exploreRowHeight)
            ) {
                items(similar.results.take(20)) { movie ->
                    MoviePoster(
                        title = movie.title,
                        posterPath = movie.posterPath,
                        onClick = { onMovieClick(movie.id) }
                    )
                }
            }
        }
        item {
            SectionHeader("RECOMMENDED")
            LazyHorizontalGrid(
                rows = GridCells.Fixed(uiConstants.movieRows),
                horizontalArrangement = Arrangement.spacedBy(dimens.small3),
                verticalArrangement = Arrangement.spacedBy(dimens.small3),
                modifier = Modifier.height(dimens.exploreRowHeight)
            ) {
                items(recommended.results.take(20)) { movie ->
                    MoviePoster(
                        title = movie.title,
                        posterPath = movie.posterPath,
                        onClick = { onMovieClick(movie.id) }
                    )
                }
            }
        }
    }
}