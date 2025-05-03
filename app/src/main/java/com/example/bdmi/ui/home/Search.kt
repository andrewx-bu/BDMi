package com.example.bdmi.ui.home

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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.bdmi.data.api.models.Movie
import com.example.bdmi.ui.composables.ErrorMessage
import com.example.bdmi.ui.theme.dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onMovieClick: (Int) -> Unit = {}
) {
    val viewModel: SearchViewModel = hiltViewModel()
    val uiState by viewModel.searchUIState.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.refresh() }

    Column(Modifier.fillMaxSize()) {
        SearchBar(
            modifier = Modifier.fillMaxWidth(),
            query = uiState.searchQuery,
            onQueryChange = viewModel::onSearchQueryChanged,
            onSearch = {
                viewModel.executeSearch()
                expanded = false
            },
            active = expanded,
            onActiveChange = { expanded = it },
            placeholder = { Text("Search movies") }
        ) {
            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxWidth()) {
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }
                }

                uiState.error != null -> {
                    Text("Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
                }

                uiState.movies.isNotEmpty() -> {
                    LazyColumn {
                        items(uiState.movies) { movie ->
                            Text(
                                text = "${movie.title} (${movie.releaseDate.take(4)})",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onMovieClick(movie.id) }
                                    .padding(16.dp)
                            )
                        }
                    }
                }

                uiState.searchQuery.isNotBlank() -> {
                    Text("No results found", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

