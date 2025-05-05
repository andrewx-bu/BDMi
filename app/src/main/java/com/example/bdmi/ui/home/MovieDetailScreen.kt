package com.example.bdmi.ui.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.bdmi.SessionViewModel
import com.example.bdmi.data.utils.ImageURLHelper
import com.example.bdmi.data.api.models.MovieDetails
import com.example.bdmi.data.api.models.WatchProvidersResponse
import com.example.bdmi.data.repositories.MovieMetrics
import com.example.bdmi.data.repositories.MovieReview
import com.example.bdmi.data.utils.fadingEdge
import com.example.bdmi.ui.composables.ErrorMessage
import com.example.bdmi.ui.composables.LoadingIndicator
import com.example.bdmi.ui.composables.movie_detail.middle.ReviewSection
import com.example.bdmi.ui.composables.home.MoviePoster
import com.example.bdmi.ui.composables.movie_detail.bottom.CastSection
import com.example.bdmi.ui.composables.movie_detail.bottom.CrewSection
import com.example.bdmi.ui.composables.movie_detail.top.DetailColumn
import com.example.bdmi.ui.composables.movie_detail.bottom.DetailsSection
import com.example.bdmi.ui.composables.movie_detail.bottom.ExploreSection
import com.example.bdmi.ui.theme.dimens
import com.example.bdmi.ui.theme.uiConstants
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun MovieDetailScreen(
    navController: NavHostController,
    sessionViewModel: SessionViewModel,
    movieId: Int,
    onMovieClick: (Int) -> Unit,
    onProfileClick: (String) -> Unit,
    onPersonClick: (Int) -> Unit,
    onGenreClick: (Int) -> Unit,
    onStudioClick: (Int) -> Unit,
    onAllReviewsClick: (Int) -> Unit
) {
    val viewModel: MovieDetailViewModel = hiltViewModel()
    val uiState by viewModel.detailUIState.collectAsState()
    val movieData by viewModel.movieData.collectAsState()
    val carouselReviews by viewModel.carouselReviews.collectAsState()
    val userReview by viewModel.userReview.collectAsState()

    LaunchedEffect(movieId) {
        sessionViewModel.clearSelectedMovie()
        sessionViewModel.clearSelectedMovieReview()
        launch { viewModel.refreshDetails(movieId) }
        launch {
            if (sessionViewModel.userInfo.value != null) {

                viewModel.loadUserReview(
                    sessionViewModel.userInfo.value!!.userId,
                    movieId
                )
            }
        }
        launch { viewModel.reviewCarousel(movieId) }
    }

    LaunchedEffect(userReview) {
        if (userReview != null)
            sessionViewModel.loadSelectedMovieReview(userReview!!)
    }

    val details = uiState.details
    val providers = uiState.providers
    val error = uiState.error
    val isLoading = uiState.isLoading

    when {
        error != null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = dimens.medium2),
                contentAlignment = Alignment.Center
            ) {
                ErrorMessage(
                    message = error.toString(),
                    onRetry = { viewModel.refreshDetails(movieId) }
                )
            }
        }

        isLoading -> {
            LoadingIndicator()
        }

        details != null -> {
            sessionViewModel.loadSelectedMovie(details)
            LaunchedEffect(details) {
                details.title.let { title ->
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "movie_title", title
                    )
                }
            }

            // String mangling done by ChatGPT
            val hasBackdrop = details.backdropPath?.isNotEmpty() == true
            // Handle multiple directors
            val directors = details.credits.crew
                .filter { it.job.equals("director", ignoreCase = true) }
                .joinToString(", ") { it.name }
                .ifEmpty { "Unknown" }
            // en-US by default. Extract YT key from most recent official trailer
            val trailerKey = details.videos.results.filter {
                it.site.equals("YouTube", ignoreCase = true) &&
                        it.type.equals("Trailer", ignoreCase = true) && it.official
            }
                .maxByOrNull { it.publishedAt }?.key
            // Extract MPAA certification from US release
            val us = details.releaseDates.results.firstOrNull { it.iso31661 == "US" }
            val certification =
                us?.releaseDates?.firstOrNull()?.certification.takeUnless { it?.isBlank() == true }
                    ?: "NR"

            LazyColumn {
                item {
                    TopSection(
                        details = details,
                        hasBackdrop = hasBackdrop,
                        directors = directors,
                        trailerKey = trailerKey,
                        certification = certification
                    )
                }

                item {
                    MiddleSection(
                        details,
                        carouselReviews,
                        movieData,
                        onProfileClick,
                        onAllReviewsClick
                    )
                }

                item {
                    BottomSection(
                        details = details,
                        providers = providers,
                        onMovieClick = onMovieClick,
                        onPersonClick = onPersonClick,
                        onStudioClick = onStudioClick
                    )
                }
            }
        }

        else -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ErrorMessage(
                    message = "An internal error occurred. No details available",
                    onRetry = { viewModel.refreshDetails(movieId) }
                )
            }
        }
    }
}

// Backdrop and Poster Row
@Composable
fun TopSection(
    details: MovieDetails,
    hasBackdrop: Boolean,
    directors: String,
    trailerKey: String?,
    certification: String
) {
    val backdropURL = ImageURLHelper.getBackdropURL(details.backdropPath)

    // Image fades at the bottom
    val bottomFadeBrush = Brush.verticalGradient(
        0.75f to Color.Black,
        1f to Color.Transparent.copy(alpha = 0.2f)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (hasBackdrop) Modifier.height(dimens.topBoxHeight) else Modifier)
    ) {
        // Backdrop box
        if (hasBackdrop) {
            AsyncImage(
                model = backdropURL,
                contentDescription = null,
                modifier = Modifier
                    .aspectRatio(uiConstants.backdropAspectRatio)
                    .fadingEdge(bottomFadeBrush),
                contentScale = ContentScale.Crop
            )
        }

        // Poster row
        Row(
            modifier = Modifier
                .height(dimens.posterSize)
                .align(Alignment.BottomStart)
                .padding(start = dimens.medium2)
        ) {
            MoviePoster(
                title = details.title,
                posterPath = details.posterPath,
                onClick = {}
            )

            Spacer(modifier = Modifier.width(dimens.small3))

            DetailColumn(details, directors, trailerKey, certification)
        }
    }
}

// Description and Reviews
@Composable
fun MiddleSection(
    details: MovieDetails,
    reviews: List<MovieReview>,
    movieData: MovieMetrics?,
    onProfileClick: (String) -> Unit,
    onAllReviewsClick: (Int) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val textLayoutResultState = remember { mutableStateOf<TextLayoutResult?>(null) }
    val maxLines = if (isExpanded) Int.MAX_VALUE else uiConstants.descriptionMaxLines
    Column(
        modifier = Modifier.padding(
            horizontal = dimens.medium2,
            vertical = dimens.small3
        )
    ) {
        if (!details.tagline.isNullOrEmpty()) {
            Text(
                text = details.tagline.uppercase(Locale.ROOT),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.inverseSurface,
            )
            Spacer(Modifier.height(dimens.small3))
        }
        // Movie description
        Box(modifier = Modifier.clickable { isExpanded = !isExpanded }) {
            Text(
                text = details.overview,
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
        Spacer(Modifier.height(dimens.small2))
        ReviewSection(
            reviews = reviews,
            movieData = movieData,
            onProfileClick = onProfileClick,
            onAllReviewsClick = { onAllReviewsClick(details.id) }
        )
    }
}

@Composable
fun BottomSection(
    details: MovieDetails,
    providers: WatchProvidersResponse?,
    onMovieClick: (Int) -> Unit,
    onPersonClick: (Int) -> Unit,
    onStudioClick: (Int) -> Unit
) {
    val tabs = listOf("CAST", "CREW", "DETAILS", "EXPLORE")
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    // Segmented Tabs
    TabRow(
        selectedTabIndex = selectedTab,
        contentColor = MaterialTheme.colorScheme.primary,
        indicator = { tabPositions ->
            SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                height = dimens.small1,
                color = MaterialTheme.colorScheme.tertiaryContainer
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimens.medium2)
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTab == index,
                onClick = { selectedTab = index },
                text = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        color = if (selectedTab == index) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        }
                    )
                }
            )
        }
    }
    when (selectedTab) {
        0 -> CastSection(details.credits.cast, onPersonClick)
        1 -> CrewSection(details.credits.crew, onPersonClick)
        2 -> DetailsSection(details, providers, onStudioClick)
        3 -> ExploreSection(details.similar, details.recommendations, onMovieClick)
    }
}