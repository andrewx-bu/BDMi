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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FilterListOff
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bdmi.ui.composables.ErrorMessage
import com.example.bdmi.ui.theme.dimens

@Composable
fun SearchScreen(
    onMovieClick: (Int) -> Unit = {}
) {
    val viewModel: SearchViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val searchText by viewModel.searchText.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    var showFilters by remember { mutableStateOf(false) }

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
                placeholder = { Text("Search movies...") }
            )
            IconButton(onClick = { showFilters = !showFilters }) {
                Icon(
                    imageVector = if (showFilters) Icons.Default.FilterListOff else Icons.Default.FilterList,
                    contentDescription = "Toggle filters"
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
                LazyColumn {
                    itemsIndexed(uiState.movies) { index, movie ->
                        Text(
                            text = movie.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onMovieClick(movie.id) }
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