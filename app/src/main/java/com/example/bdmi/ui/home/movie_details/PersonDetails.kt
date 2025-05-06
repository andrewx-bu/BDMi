package com.example.bdmi.ui.home.movie_details

import android.content.Intent
import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.bdmi.FilterState
import com.example.bdmi.data.utils.ImageURLHelper
import com.example.bdmi.ui.composables.ErrorMessage
import com.example.bdmi.ui.composables.LoadingIndicator
import com.example.bdmi.ui.composables.home.MoviePoster
import com.example.bdmi.ui.composables.movie_detail.middle.ShimmeringDivider
import com.example.bdmi.ui.theme.dimens
import com.example.bdmi.ui.theme.uiConstants

@Composable
fun PersonDetails(
    navController: NavController,
    onMovieClick: (Int) -> Unit,
    showFilters: Boolean,
    onShowFiltersChanged: (Boolean) -> Unit
) {
    val viewModel: PersonDetailsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val gridState = rememberLazyGridState()
    val filters = viewModel.uiState.collectAsState().value.let {
        FilterState(
            it.sortBy,
            it.voteCountGte to it.voteCountLte,
            it.voteAverageGte to it.voteAverageLte
        )
    }

    LaunchedEffect(uiState.person) {
        uiState.person?.name?.let { name ->
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
            val configuration = LocalConfiguration.current
            val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            LazyVerticalGrid(
                columns = GridCells.Fixed(uiConstants.movieColumns),
                verticalArrangement = Arrangement.spacedBy(dimens.small3),
                horizontalArrangement = Arrangement.spacedBy(dimens.small3),
                state = gridState,
                modifier = Modifier.padding(horizontal = dimens.medium2)
            ) {
                uiState.person?.let { person ->
                    // person info header
                    item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                        if (isLandscape) {
                            Column(
                                modifier = Modifier.padding(horizontal = dimens.small2)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val personUrl = ImageURLHelper.getURL(person.profilePath)
                                    Box(
                                        Modifier
                                            .aspectRatio(uiConstants.posterAspectRatio)
                                            .clip(RoundedCornerShape(dimens.small2))
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                            .weight(0.7f)
                                    ) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(personUrl)
                                                .crossfade(true)
                                                .build(),
                                            placeholder = rememberVectorPainter(Icons.Default.Person),
                                            error = rememberVectorPainter(Icons.Default.Person),
                                            contentDescription = null,
                                            contentScale = ContentScale.Fit,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(dimens.medium3))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = person.name,
                                            style = MaterialTheme.typography.headlineMedium
                                        )
                                        Spacer(Modifier.height(dimens.small3))
                                        Text(
                                            text = person.knownForDepartment,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(Modifier.height(dimens.small3))
                                        Text(
                                            text = "pob: ${person.placeOfBirth}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Spacer(Modifier.height(dimens.small3))
                                        person.birthday?.let { birth ->
                                            val death = person.deathday ?: "Present"
                                            Text(
                                                text = "$birth - $death",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                        Spacer(Modifier.height(dimens.small3))
                                        Row {
                                            // link to imdb if exists
                                            person.imdbId?.let { imdbId ->
                                                val context = LocalContext.current
                                                Button(
                                                    onClick = {
                                                        val intent = Intent(
                                                            Intent.ACTION_VIEW,
                                                            "https://www.imdb.com/name/$imdbId".toUri()
                                                        )
                                                        context.startActivity(intent)
                                                    },
                                                    shape = RoundedCornerShape(dimens.small3),
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                                    ),
                                                    modifier = Modifier
                                                        .size(
                                                            width = dimens.buttonWidthSmall,
                                                            height = dimens.buttonHeightSmall
                                                        )
                                                ) {
                                                    Text(
                                                        text = "IMDb",
                                                        style = MaterialTheme.typography.labelLarge
                                                    )
                                                }
                                            }
                                            // link to person homepage if exists
                                            person.homepage
                                                ?.takeIf { it.isNotBlank() }
                                                ?.let { url ->
                                                    Spacer(Modifier.width(dimens.small3))
                                                    val context = LocalContext.current

                                                    Button(
                                                        modifier = Modifier
                                                            .size(
                                                                width = dimens.buttonWidthSmall,
                                                                height = dimens.buttonHeightSmall
                                                            ),
                                                        onClick = {
                                                            val intent =
                                                                Intent(
                                                                    Intent.ACTION_VIEW,
                                                                    url.toUri()
                                                                )
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
                                        }
                                        Spacer(Modifier.height(dimens.small3))
                                        var isExpanded by remember { mutableStateOf(false) }
                                        val textLayoutResultState =
                                            remember { mutableStateOf<TextLayoutResult?>(null) }
                                        val maxLines =
                                            if (isExpanded) Int.MAX_VALUE else uiConstants.personDescMaxLines

                                        // Movie description
                                        Box(
                                            modifier = Modifier
                                                .height(570.dp)
                                                .clickable {
                                                    isExpanded = !isExpanded
                                                }) {
                                            Text(
                                                text = person.biography,
                                                style = MaterialTheme.typography.labelLarge,
                                                color = MaterialTheme.colorScheme.onBackground.copy(
                                                    alpha = 0.5f
                                                ),
                                                maxLines = maxLines,
                                                overflow = TextOverflow.Ellipsis,
                                                onTextLayout = { textLayoutResultState.value = it },
                                                modifier = Modifier.animateContentSize(),
                                            )

                                            // Fade out when collapsed
                                            if (!isExpanded && textLayoutResultState.value?.hasVisualOverflow == true) {
                                                Box(
                                                    modifier = Modifier
                                                        .matchParentSize()
                                                        .background(
                                                            brush = Brush.verticalGradient(
                                                                colors = listOf(
                                                                    Color.Transparent,
                                                                    MaterialTheme.colorScheme.background
                                                                ),
                                                                startY = 90f
                                                            )
                                                        )
                                                )
                                            }
                                        }
                                    }
                                }
                                Spacer(Modifier.height(dimens.small3))
                                ShimmeringDivider()
                                Spacer(Modifier.height(dimens.small3))
                            }
                        } else {
                            Column(
                                modifier = Modifier.padding(horizontal = dimens.small2)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val personUrl = ImageURLHelper.getURL(person.profilePath)
                                    Box(
                                        Modifier
                                            .aspectRatio(uiConstants.posterAspectRatio)
                                            .clip(RoundedCornerShape(dimens.small2))
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                            .weight(0.7f)
                                    ) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(personUrl)
                                                .crossfade(true)
                                                .build(),
                                            placeholder = rememberVectorPainter(Icons.Default.Person),
                                            error = rememberVectorPainter(Icons.Default.Person),
                                            contentDescription = null,
                                            contentScale = ContentScale.Fit,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(dimens.medium3))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = person.name,
                                            style = MaterialTheme.typography.headlineMedium
                                        )
                                        Spacer(Modifier.height(dimens.small3))
                                        Text(
                                            text = person.knownForDepartment,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(Modifier.height(dimens.small3))
                                        Text(
                                            text = "pob: ${person.placeOfBirth}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Spacer(Modifier.height(dimens.small3))
                                        person.birthday?.let { birth ->
                                            val death = person.deathday ?: "Present"
                                            Text(
                                                text = "$birth - $death",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                        Spacer(Modifier.height(dimens.small3))
                                        Row {
                                            // link to imdb if exists
                                            person.imdbId?.let { imdbId ->
                                                val context = LocalContext.current
                                                Button(
                                                    onClick = {
                                                        val intent = Intent(
                                                            Intent.ACTION_VIEW,
                                                            "https://www.imdb.com/name/$imdbId".toUri()
                                                        )
                                                        context.startActivity(intent)
                                                    },
                                                    shape = RoundedCornerShape(dimens.small3),
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                                    ),
                                                    modifier = Modifier
                                                        .size(
                                                            width = dimens.buttonWidthSmall,
                                                            height = dimens.buttonHeightSmall
                                                        )
                                                ) {
                                                    Text(
                                                        text = "IMDb",
                                                        style = MaterialTheme.typography.labelLarge
                                                    )
                                                }
                                            }
                                            // link to person homepage if exists
                                            person.homepage
                                                ?.takeIf { it.isNotBlank() }
                                                ?.let { url ->
                                                    Spacer(Modifier.width(dimens.small3))
                                                    val context = LocalContext.current

                                                    Button(
                                                        modifier = Modifier
                                                            .size(
                                                                width = dimens.buttonWidthSmall,
                                                                height = dimens.buttonHeightSmall
                                                            ),
                                                        onClick = {
                                                            val intent =
                                                                Intent(
                                                                    Intent.ACTION_VIEW,
                                                                    url.toUri()
                                                                )
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
                                        }
                                    }
                                }

                                Spacer(Modifier.height(dimens.small3))

                                var isExpanded by remember { mutableStateOf(false) }
                                val textLayoutResultState =
                                    remember { mutableStateOf<TextLayoutResult?>(null) }
                                val maxLines =
                                    if (isExpanded) Int.MAX_VALUE else uiConstants.personDescMaxLines

                                // Movie description
                                Box(modifier = Modifier.clickable { isExpanded = !isExpanded }) {
                                    Text(
                                        text = person.biography,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                        maxLines = maxLines,
                                        overflow = TextOverflow.Ellipsis,
                                        onTextLayout = { textLayoutResultState.value = it },
                                        modifier = Modifier.animateContentSize(),
                                    )

                                    // Fade out when collapsed
                                    if (!isExpanded && textLayoutResultState.value?.hasVisualOverflow == true) {
                                        Box(
                                            modifier = Modifier
                                                .matchParentSize()
                                                .background(
                                                    brush = Brush.verticalGradient(
                                                        colors = listOf(
                                                            Color.Transparent,
                                                            MaterialTheme.colorScheme.background
                                                        ),
                                                        startY = 90f
                                                    )
                                                )
                                        )
                                    }
                                }
                                Spacer(Modifier.height(dimens.small3))
                                ShimmeringDivider()
                                Spacer(Modifier.height(dimens.small3))
                            }
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