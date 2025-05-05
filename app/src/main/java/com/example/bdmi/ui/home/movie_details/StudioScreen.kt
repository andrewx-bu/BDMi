package com.example.bdmi.ui.home.movie_details

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.bdmi.R
import com.example.bdmi.data.utils.ImageURLHelper
import com.example.bdmi.ui.composables.ErrorMessage
import com.example.bdmi.ui.composables.LoadingIndicator
import com.example.bdmi.ui.composables.home.MoviePoster
import com.example.bdmi.ui.theme.dimens
import com.example.bdmi.ui.theme.uiConstants
import androidx.core.net.toUri
import com.example.bdmi.FilterState
import com.example.bdmi.data.utils.SortOptions
import com.example.bdmi.ui.composables.movie_detail.middle.ShimmeringDivider
import kotlin.math.roundToInt

@Composable
fun StudioDetails(
    navController: NavController,
    onMovieClick: (Int) -> Unit,
    showFilters: Boolean,
    onShowFiltersChanged: (Boolean) -> Unit
) {
    val viewModel: StudioViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val gridState = rememberLazyGridState()
    val filters = viewModel.uiState.collectAsState().value.let {
        FilterState(
            it.sortBy,
            it.voteCountGte to it.voteCountLte,
            it.voteAverageGte to it.voteAverageLte
        )
    }

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
                uiState.company?.let { company ->
                    // company info header
                    item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                        Column(
                            modifier = Modifier.padding(horizontal = dimens.small2)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val logoUrl = ImageURLHelper.getURL(company.logoPath, width = 200)
                                AsyncImage(
                                    model = logoUrl,
                                    contentDescription = "${company.name} logo",
                                    modifier = Modifier
                                        .size(dimens.studioLogoSize)
                                        .clip(RoundedCornerShape(dimens.medium2))
                                        .background(
                                            MaterialTheme.colorScheme.onBackground.copy(
                                                alpha = 0.2f
                                            )
                                        )
                                        .padding(dimens.small3),
                                    contentScale = ContentScale.Fit,
                                    placeholder = painterResource(R.drawable.broken_icon),
                                    error = painterResource(R.drawable.broken_icon)
                                )
                                Spacer(modifier = Modifier.width(dimens.medium3))
                                Column {
                                    Text(
                                        text = company.name,
                                        style = MaterialTheme.typography.headlineMedium
                                    )
                                    Spacer(Modifier.height(dimens.small2))
                                    Text(
                                        text = "HQ: ${company.headquarters}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Spacer(Modifier.height(dimens.small3))
                                    Text(
                                        text = "Country: ${company.originCountry}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    // link to company homepage if exists
                                    company.homepage
                                        ?.takeIf { it.isNotBlank() }
                                        ?.let { url ->
                                            Spacer(Modifier.height(dimens.small3))
                                            val context = LocalContext.current

                                            Button(
                                                modifier = Modifier
                                                    .size(
                                                        width = dimens.buttonWidthSmall,
                                                        height = dimens.buttonHeightSmall
                                                    ),
                                                onClick = {
                                                    val intent =
                                                        Intent(Intent.ACTION_VIEW, url.toUri())
                                                    context.startActivity(intent)
                                                },
                                                shape = RoundedCornerShape(dimens.small3),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                                )
                                            ) {
                                                Text(
                                                    text = "Website",
                                                    style = MaterialTheme.typography.labelLarge
                                                )
                                            }
                                        }
                                    Spacer(Modifier.height(dimens.small3))
                                    // parent company logo and name if exists
                                    company.parentCompany?.let { parent ->
                                        Text(
                                            text = "Parent Company",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Spacer(Modifier.height(dimens.small2))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            val logo =
                                                ImageURLHelper.getURL(parent.logoPath, width = 100)
                                            AsyncImage(
                                                model = logo,
                                                contentDescription = "parent logo",
                                                modifier = Modifier
                                                    .size(dimens.parentLogoSize)
                                                    .clip(RoundedCornerShape(dimens.medium1))
                                                    .background(
                                                        MaterialTheme.colorScheme.onBackground.copy(
                                                            alpha = 0.2f
                                                        )
                                                    )
                                                    .padding(dimens.small1),
                                                contentScale = ContentScale.Fit,
                                                placeholder = painterResource(R.drawable.broken_icon),
                                                error = painterResource(R.drawable.broken_icon)
                                            )
                                            Spacer(modifier = Modifier.width(dimens.small2))
                                            Text(
                                                text = parent.name,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(dimens.small3))
                            ShimmeringDivider()
                            Spacer(Modifier.height(dimens.small3))
                        }
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

@Composable
fun FilterDialog(
    current: FilterState,
    onDismiss: () -> Unit,
    onApply: (FilterState) -> Unit
) {
    var sortBy by remember { mutableStateOf(current.sortBy) }
    var voteMin by remember { mutableFloatStateOf(current.voteCountRange.first ?: 0f) }
    var voteMax by remember { mutableFloatStateOf(current.voteCountRange.second ?: 50000f) }
    var avgMin by remember { mutableFloatStateOf(current.voteAvgRange.first ?: 0f) }
    var avgMax by remember { mutableFloatStateOf(current.voteAvgRange.second ?: 10f) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter Movies", style = MaterialTheme.typography.titleMedium) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Sort by:", style = MaterialTheme.typography.bodyMedium)
                var expanded by remember { mutableStateOf(false) }
                Box {
                    Text(
                        text = SortOptions.values[sortBy] ?: sortBy,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true }
                            .padding(vertical = dimens.small3)
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        SortOptions.values.forEach { (key, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    sortBy = key
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(dimens.small3))

                Text("Vote Count", style = MaterialTheme.typography.bodyMedium)
                RangeSlider(
                    value = voteMin..voteMax,
                    onValueChange = { range ->
                        voteMin = range.start.coerceAtLeast(0f).roundToInt().toFloat()
                        voteMax = range.endInclusive.coerceIn(0f, 50000f).roundToInt().toFloat()
                    },
                    valueRange = 0f..50000f,
                    steps = 20,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "≥ ${voteMin.toInt()} ... ≤ ${voteMax.toInt()}",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(dimens.small3))

                Text("Vote Average", style = MaterialTheme.typography.bodyMedium)
                RangeSlider(
                    value = avgMin..avgMax,
                    onValueChange = { range ->
                        avgMin = (range.start * 10).roundToInt() / 10f
                        avgMax = (range.endInclusive * 10).roundToInt() / 10f
                    },
                    valueRange = 0f..10f,
                    steps = 20,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "≥ ${"%.1f".format(avgMin)} ... ≤ ${"%.1f".format(avgMax)}",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onApply(FilterState(sortBy, voteMin to voteMax, avgMin to avgMax))
            }) {
                Text(text = "Apply", style = MaterialTheme.typography.bodyMedium)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel", style = MaterialTheme.typography.bodyMedium)
            }
        }
    )
}