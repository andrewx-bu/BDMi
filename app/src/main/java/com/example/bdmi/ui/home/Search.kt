package com.example.bdmi.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FilterListOff
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bdmi.data.api.models.Movie
import com.example.bdmi.data.utils.GenreMappings
import com.example.bdmi.data.utils.VoiceToTextParser
import com.example.bdmi.data.utils.formatReviewCount
import com.example.bdmi.data.utils.formatStarRating
import com.example.bdmi.ui.composables.ErrorMessage
import com.example.bdmi.ui.composables.home.MoviePoster
import com.example.bdmi.ui.theme.dimens

@Composable
fun SearchScreen(
    voiceToTextParser: VoiceToTextParser,
    onMovieClick: (Int) -> Unit
) {
    val viewModel: SearchViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val searchText by viewModel.searchText.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    var showFilters by remember { mutableStateOf(false) }
    val state by voiceToTextParser.state.collectAsState()

    LaunchedEffect(state.spokenText) {
        if (state.spokenText.isNotEmpty() && state.spokenText != searchText) {
            viewModel.onSearchTextChange(state.spokenText)
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(dimens.medium2)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = searchText,
                onValueChange = viewModel::onSearchTextChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = dimens.small3),
                placeholder = {
                    Text(
                        text = "Search Movies...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            )
            IconButton(onClick = { showFilters = !showFilters }) {
                Icon(
                    imageVector = if (showFilters) Icons.Default.FilterListOff else Icons.Default.FilterList,
                    contentDescription = "Toggle filters",
                    modifier = Modifier.size(dimens.iconMedium)
                )
            }
        }

        AnimatedVisibility(showFilters) {
            FilterPanel(
                includeAdult = uiState.includeAdult,
                onIncludeAdultChange = viewModel::setIncludeAdult,
                year = uiState.year ?: "",
                onYearChange = viewModel::setYear,
            )
        }

        Spacer(Modifier.height(dimens.medium1))

        when {
            uiState.error != null -> {
                ErrorMessage(
                    message = uiState.error.toString(),
                    onRetry = { /* refresh */ }
                )
            }

            isSearching || (uiState.isLoading && uiState.movies.isEmpty()) -> {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = dimens.large3)
                ) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            else -> {
                if (uiState.movies.isEmpty()) {
                    Text(
                        text = "No results",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                LazyColumn {
                    itemsIndexed(uiState.movies) { index, movie ->
                        MovieListItem(
                            movie = movie,
                            onClick = { onMovieClick(movie.id) }
                        )

                        if (index == uiState.movies.lastIndex) {
                            LaunchedEffect(Unit) {
                                viewModel.loadNextPage()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterPanel(
    includeAdult: Boolean,
    onIncludeAdultChange: (Boolean) -> Unit,
    year: String,
    onYearChange: (String) -> Unit,
) {
    Column(
        Modifier
            .padding(dimens.small3)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(dimens.small3)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = year,
                    onValueChange = onYearChange,
                    label = {
                        Text(
                            text = "Year",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    placeholder = {
                        Text(
                            text = "e.g. 2024",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(Modifier.width(dimens.medium2))
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Include Adult",
                    style = MaterialTheme.typography.bodyMedium
                )
                Switch(
                    checked = includeAdult,
                    onCheckedChange = onIncludeAdultChange
                )
            }
        }
    }
}

@Composable
fun MovieListItem(
    movie: Movie,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(dimens.small3),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(
                width = dimens.movieRowWidth,
                height = dimens.movieRowHeight
            )
        ) {
            MoviePoster(
                title = movie.title,
                posterPath = movie.posterPath,
                onClick = onClick,
            )
        }

        Spacer(modifier = Modifier.width(dimens.medium2))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(dimens.small2))
            Text(
                text = "Release: ${movie.releaseDate}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(dimens.small2))
            Text(
                text = "Genres: ${
                    movie.genreIds?.map { GenreMappings.getGenreName(it) }
                        ?.joinToString(", ")
                }",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(dimens.small2))
            Text(
                // Built by ChatGPT. displays a star with color. lol
                text = buildAnnotatedString {
                    append("Rating: ${formatStarRating(movie.voteAverage)} ")
                    appendInlineContent("star", "★")
                    append(" (${formatReviewCount(movie.voteCount)})")
                },
                style = MaterialTheme.typography.bodyMedium,
                inlineContent = mapOf(
                    "star" to InlineTextContent(
                        Placeholder(
                            width = dimens.placeholderWidth,
                            height = dimens.placeholderHeight,
                            placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "star rating",
                            modifier = Modifier.size(dimens.large1),
                            tint = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    }
                )
            )
        }
    }
}