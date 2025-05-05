package com.example.bdmi.ui.home.movie_details

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.example.bdmi.ui.composables.movie_detail.middle.ShimmeringDivider

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
            LazyVerticalGrid(
                columns = GridCells.Fixed(uiConstants.movieColumns),
                verticalArrangement = Arrangement.spacedBy(dimens.small3),
                horizontalArrangement = Arrangement.spacedBy(dimens.small3),
                state = gridState,
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
                                        text = "Headquarters: ${company.headquarters}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Spacer(Modifier.height(dimens.small3))
                                    Text(
                                        text = "Country: ${company.originCountry}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    // link to company homepage if exists
                                    company.homepage?.takeIf { it.isNotBlank() }?.let { url ->
                                        Spacer(Modifier.height(dimens.small3))
                                        val context = LocalContext.current
                                        Text(
                                            text = url,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.clickable {
                                                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                                context.startActivity(intent)
                                            },
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                textDecoration = TextDecoration.Underline
                                            )
                                        )
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