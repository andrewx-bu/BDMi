package com.example.bdmi.ui.screens

import android.content.Intent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.bdmi.R
import com.example.bdmi.UserViewModel
import com.example.bdmi.data.api.CastMember
import com.example.bdmi.data.api.CrewMember
import com.example.bdmi.data.api.ImageURLHelper
import com.example.bdmi.data.api.MovieDetails
import com.example.bdmi.data.api.MoviesResponse
import com.example.bdmi.data.api.WatchProvidersResponse
import com.example.bdmi.ui.theme.dimens
import com.example.bdmi.ui.theme.uiConstants
import com.example.bdmi.data.repositories.MediaItem
import com.example.bdmi.data.utils.formatAmount
import com.example.bdmi.data.utils.toFlagEmoji
import com.example.bdmi.ui.DotsIndicator
import com.example.bdmi.ui.ErrorMessage
import com.example.bdmi.ui.GenreChip
import com.example.bdmi.ui.ReviewCard
import com.example.bdmi.ui.ShimmeringDivider
import com.example.bdmi.ui.fadingEdge
import com.spr.jetpack_loading.components.indicators.BallPulseSyncIndicator
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun MovieDetailScreen(
    navController: NavHostController,
    userViewModel: UserViewModel? = null,
    movieId: Int
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val uiState by viewModel.detailUIState.collectAsState()
    var userPrivileges by remember { mutableStateOf(false) }

    LaunchedEffect(movieId) {
        launch { viewModel.refreshDetails(movieId) }
        if (userViewModel != null) {
            if (userViewModel.userInfo.value != null) {
                userPrivileges = true
            }
        }
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
                    .padding(horizontal = MaterialTheme.dimens.medium2),
                contentAlignment = Alignment.Center
            ) {
                ErrorMessage(
                    message = error.toString(),
                    onRetry = { viewModel.refreshDetails(movieId) }
                )
            }
        }

        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                BallPulseSyncIndicator(color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }

        details != null -> {
            LaunchedEffect(details) {
                details.title.let { title ->
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "movie_title",
                        title
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        TopSection(
                            details = details,
                            hasBackdrop = hasBackdrop,
                            directors = directors,
                            trailerKey = trailerKey,
                            certification = certification
                        )
                    }
                }

                item {
                    Spacer(Modifier.height(MaterialTheme.dimens.small3))
                    MovieDescription(details = details)
                    Spacer(Modifier.height(MaterialTheme.dimens.small3))
                }

                item {
                    // TODO: Add reviews
                    val reviews = listOf(
                        "Chicken Jockey Chicken Jockey Chicken Jockey Chicken Jockey " +
                                "Chicken Jockey Chicken Jockey Chicken Jockey Chicken Jockey " +
                                "Chicken Jockey Chicken Jockey Chicken Jockey Chicken Jockey " +
                                "Chicken Jockey CHICKEN JOCKEY CHICKEN JOCKEY CHICKEN JOCKEY",
                        "Flint and Steel",
                        "Ender Pearl",
                        "Water Bucket Release",
                        "Diamond Armor, Full Set"
                    )
                    ReviewCarousel(reviews = reviews)
                }

                item {
                    Spacer(Modifier.height(MaterialTheme.dimens.small3))
                    BottomSection(details = details, providers = providers)
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
            .then(if (hasBackdrop) Modifier.height(MaterialTheme.dimens.topBoxHeight) else Modifier)
    ) {
        // Backdrop box
        if (hasBackdrop) {
            AsyncImage(
                model = backdropURL,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(MaterialTheme.uiConstants.backdropAspectRatio)
                    .fadingEdge(bottomFadeBrush),
                contentScale = ContentScale.Crop
            )
        }

        // Poster row
        Row(
            modifier = Modifier
                .height(MaterialTheme.dimens.posterSize)
                .align(Alignment.BottomStart)
                .padding(start = MaterialTheme.dimens.medium2)
        ) {
            MoviePoster(
                title = details.title,
                posterPath = details.posterPath,
                onClick = {}
            )

            Spacer(modifier = Modifier.width(MaterialTheme.dimens.small3))

            // Column fades upwards into the backdrop
            val topFadeBrush = Brush.verticalGradient(
                colorStops = arrayOf(
                    0f to Color.Transparent.copy(alpha = 0.2f),
                    0.2f to Color.Black,
                )
            )

            // Details column
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(top = MaterialTheme.dimens.large2, end = MaterialTheme.dimens.medium2)
                    .fadingEdge(topFadeBrush)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.small2)
            ) {
                Spacer(Modifier.height(MaterialTheme.dimens.medium3))

                // Movie Title
                Text(
                    text = details.title,
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Genre Chips
                LazyRow(
                    modifier = Modifier.padding(bottom = MaterialTheme.dimens.small2),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.small2)
                ) {
                    items(details.genres) { genre ->
                        GenreChip(name = genre.name, onClick = {})
                    }
                }

                Spacer(Modifier.height(MaterialTheme.dimens.small1))

                // Release date, director
                Text(
                    text = "${details.releaseDate} | DIRECTED BY",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                )

                Text(
                    text = directors,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Trailer button, runtime, MPAA rating
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val context = LocalContext.current

                    // Only shimmer if trailer available
                    val iconModifier = Modifier
                        .size(MaterialTheme.dimens.iconTiny)
                        .let { base -> if (trailerKey != null) base.shimmer() else base }

                    Button(
                        onClick = {
                            val url = "https://www.youtube.com/watch?v=$trailerKey"
                            val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent, null)
                        },
                        enabled = trailerKey != null,
                        modifier = Modifier
                            .size(
                                width = MaterialTheme.dimens.buttonWidthSmall,
                                height = MaterialTheme.dimens.buttonHeightSmall
                            ),
                        contentPadding = PaddingValues(
                            start = MaterialTheme.dimens.small2,
                            end = MaterialTheme.dimens.small3
                        ),
                        shape = RoundedCornerShape(MaterialTheme.dimens.small3),
                        colors = ButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            disabledContainerColor = MaterialTheme.colorScheme.errorContainer,
                            disabledContentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            modifier = iconModifier
                        )
                        Text("TRAILER", style = MaterialTheme.typography.bodyLarge)
                    }

                    Spacer(modifier = Modifier.width(MaterialTheme.dimens.small3))

                    Text(
                        text = "${details.runtime} min | $certification",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    )
                }
            }
        }
    }
}

// Tagline and Overview
@Composable
fun MovieDescription(details: MovieDetails) {
    var isExpanded by remember { mutableStateOf(false) }
    val textLayoutResultState = remember { mutableStateOf<TextLayoutResult?>(null) }
    val maxLines = if (isExpanded) Int.MAX_VALUE else MaterialTheme.uiConstants.descriptionMaxLines
    Column(modifier = Modifier.padding(horizontal = MaterialTheme.dimens.medium2)) {
        if (!details.tagline.isNullOrEmpty()) {
            Text(
                text = details.tagline.uppercase(Locale.ROOT),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            )
            Spacer(Modifier.height(MaterialTheme.dimens.small3))
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
    }
}

// Review Carousel
// TODO: Prioritize Friend Reviews.
// TODO: Make Reviews clickable.
@Composable
fun ReviewCarousel(reviews: List<String>) {
    val pageCount = 1000 * reviews.size
    val startIndex = (pageCount / 2)
    val pagerState = rememberPagerState(
        initialPage = startIndex,
        pageCount = { pageCount }
    )
    val currentReviewIndex by remember { derivedStateOf { pagerState.currentPage % reviews.size } }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaterialTheme.dimens.medium2),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = MaterialTheme.dimens.small3)
                    .graphicsLayer { scaleX = -1f }
            ) {
                ShimmeringDivider()
            }
            Text(
                text = "REVIEWS",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = MaterialTheme.dimens.small3)
            ) {
                ShimmeringDivider()
            }
        }

        HorizontalPager(state = pagerState) { page ->
            val reviewIndex = page % reviews.size
            ReviewCard(
                text = reviews[reviewIndex],
                rating = 5f,
                liked = true,
                username = "Steve"
            )
        }

        Spacer(Modifier.height(MaterialTheme.dimens.small3))

        DotsIndicator(
            numDots = reviews.size,
            currentIndex = currentReviewIndex,
            onDotClick = { index ->
                coroutineScope.launch {
                    pagerState.animateScrollToPage(index)
                }
            }
        )
    }
}

@Composable
fun BottomSection(details: MovieDetails, providers: WatchProvidersResponse?) {
    val tabs = listOf("CAST", "CREW", "DETAILS", "EXPLORE")
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    // Segmented Tabs
    TabRow(
        selectedTabIndex = selectedTab,
        contentColor = MaterialTheme.colorScheme.primary,
        indicator = { tabPositions ->
            SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                height = MaterialTheme.dimens.small1,
                color = MaterialTheme.colorScheme.tertiaryContainer
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.dimens.medium2)
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
    Spacer(Modifier.height(MaterialTheme.dimens.medium2))
    when (selectedTab) {
        0 -> CastSection(details.credits.cast)
        1 -> CrewSection(details.credits.crew)
        2 -> DetailsSection(details, providers)
        3 -> ExploreSection(details.similar, details.recommendations)
    }
}

@Composable
private fun CastSection(cast: List<CastMember>) {
    if (cast.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(MaterialTheme.dimens.personColumnHeight)
                .padding(horizontal = MaterialTheme.dimens.medium2)
        ) {
            Text(
                text = "No cast data available",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.errorContainer),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
        return
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.dimens.medium2)
            .height(MaterialTheme.dimens.personColumnHeight),
    ) {
        items(cast) { person ->
            val portraitURL = ImageURLHelper.getURL(person.profilePath)
            HorizontalDivider()
            Spacer(modifier = Modifier.height(MaterialTheme.dimens.small2))
            Row(
                modifier = Modifier
                    .height(MaterialTheme.dimens.personRowHeight)
                    .clickable { /* TODO: Move to Actor Screen */ },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .aspectRatio(MaterialTheme.uiConstants.posterAspectRatio)
                        .clip(RoundedCornerShape(MaterialTheme.dimens.small2))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(portraitURL)
                            .crossfade(true)
                            .build(),
                        placeholder = rememberVectorPainter(Icons.Default.Person),
                        error = rememberVectorPainter(Icons.Default.Person),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.width(MaterialTheme.dimens.small3))
                Column {
                    Text(text = person.name, style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.width(MaterialTheme.dimens.small3))
                    Text(person.character, style = MaterialTheme.typography.titleMedium)
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(MaterialTheme.dimens.iconMedium)
                )
            }
            Spacer(modifier = Modifier.height(MaterialTheme.dimens.small2))
        }
    }
}

@Composable
private fun CrewSection(crew: List<CrewMember>) {
    if (crew.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(MaterialTheme.dimens.personColumnHeight)
                .padding(horizontal = MaterialTheme.dimens.medium2)
        ) {
            Text(
                text = "No crew data available",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.errorContainer),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
        return
    }

    // Join duplicate crew members to prevent them from showing twice
    // Code generated by ChatGPT.
    val crewById = remember(crew) {
        crew.groupBy { it.id }
    }

    val uniqueCrew = crewById.map { (_, entries) ->
        val representative = entries.first()
        val jobs = entries.map { it.job }.distinct()
        representative to jobs
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.dimens.medium2)
            .height(MaterialTheme.dimens.personColumnHeight),
    ) {
        items(uniqueCrew) { (person, jobs) ->
            val portraitURL = ImageURLHelper.getURL(person.profilePath)
            HorizontalDivider()
            Spacer(modifier = Modifier.height(MaterialTheme.dimens.small2))
            Row(
                modifier = Modifier
                    .height(MaterialTheme.dimens.personRowHeight)
                    .clickable { /* TODO: Move to Person Screen */ },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .aspectRatio(MaterialTheme.uiConstants.posterAspectRatio)
                        .clip(RoundedCornerShape(MaterialTheme.dimens.small2))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(portraitURL)
                            .crossfade(true)
                            .build(),
                        placeholder = rememberVectorPainter(Icons.Default.Person),
                        error = rememberVectorPainter(Icons.Default.Person),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.width(MaterialTheme.dimens.small3))
                Column {
                    Text(
                        text = person.name,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.dimens.small1))
                    Text(
                        text = jobs.joinToString(", "),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(MaterialTheme.dimens.iconMedium)
                )
            }
            Spacer(modifier = Modifier.height(MaterialTheme.dimens.small2))
        }
    }
}

@Composable
private fun DetailsSection(details: MovieDetails, providers: WatchProvidersResponse?) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.dimens.medium2)
            .height(MaterialTheme.dimens.personColumnHeight),
    ) {
        item {
            HorizontalDivider(Modifier.padding(bottom = MaterialTheme.dimens.small2))
            Text(
                text = "Budget: ${formatAmount(details.budget.toLong())}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Revenue: ${formatAmount(details.revenue)}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Spoken Languages: " + details.spokenLanguages.joinToString { it.englishName },
                style = MaterialTheme.typography.bodyLarge
            )
            HorizontalDivider(
                modifier = Modifier.padding(
                    top = MaterialTheme.dimens.small2,
                    bottom = MaterialTheme.dimens.large3
                )
            )
            Text(text = "COUNTRIES", style = MaterialTheme.typography.titleMedium)
        }

        items(details.productionCountries) { country ->
            HorizontalDivider()
            Spacer(modifier = Modifier.height(MaterialTheme.dimens.small2))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(25.dp)
                    .clickable { /* TODO: Move to Country Screen */ },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${country.iso31661.toFlagEmoji()} ${country.name}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(MaterialTheme.dimens.iconMedium)
                )
            }

            Spacer(Modifier.height(MaterialTheme.dimens.small2))
        }

        item {
            HorizontalDivider(Modifier.padding(bottom = MaterialTheme.dimens.large3))
            Text(text = "STUDIOS", style = MaterialTheme.typography.titleMedium)
        }

        items(details.productionCompanies) { studio ->
            HorizontalDivider(Modifier.padding(bottom = MaterialTheme.dimens.small2))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MaterialTheme.dimens.personRowHeight)
                    .clickable { /* TODO: Move to Studio Screen */ },
                verticalAlignment = Alignment.CenterVertically
            ) {
                val logoURL = ImageURLHelper.getURL(studio.logoPath, width = 200)
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(MaterialTheme.dimens.medium2))
                        .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(logoURL)
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(R.drawable.broken_icon),
                        error = painterResource(R.drawable.broken_icon),
                        contentDescription = null,
                        modifier = Modifier.padding(MaterialTheme.dimens.small1),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(Modifier.width(MaterialTheme.dimens.small3))

                Text(
                    text = studio.name,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(Modifier.weight(1f))

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(MaterialTheme.dimens.iconMedium)
                )
            }
            Spacer(modifier = Modifier.height(MaterialTheme.dimens.small2))
        }

        if (providers == null) return@LazyColumn;
        item {
            HorizontalDivider()
            val usProviders = providers.results.us
            usProviders?.let { p ->
                Text(
                    "Rent: " + (p.rent.takeIf { it.isNotEmpty() }
                        ?.joinToString(", ") { it.providerName }
                        ?: "None")
                )
                Text(
                    "Buy: " + (p.buy.takeIf { it.isNotEmpty() }
                        ?.joinToString(", ") { it.providerName }
                        ?: "None")
                )
            }
        }
    }
}

@Composable
private fun ExploreSection(similar: MoviesResponse, recommended: MoviesResponse) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.dimens.medium2)
            .height(MaterialTheme.dimens.personColumnHeight),
    ) {

    }
}

// TODO: Integrate with outer scaffold
// TODO: Issue with dropdown menu overlapping
@Composable
fun MenuButton(
    userPrivileges: Boolean,
    userViewModel: UserViewModel?,
    movieDetails: MovieDetails?,
) {
    val movieDetailViewModel: MovieDetailViewModel = hiltViewModel()
    var expanded by remember { mutableStateOf(false) }
    var showWatchlists by remember { mutableStateOf(false) }
    val watchlists = movieDetailViewModel.lists.collectAsState()
    val userId = userViewModel?.userInfo?.collectAsState()?.value?.userId
    LaunchedEffect(Unit) {
        if (userId != null)
            movieDetailViewModel.getLists(userId.toString())
    }
    IconButton(
        onClick = { expanded = true },
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.background.copy(alpha = 0.5f), CircleShape
            )
            .size(MaterialTheme.dimens.iconLarge)
    ) {
        Icon(
            imageVector = Icons.Default.MoreHoriz,
            contentDescription = "Menu",
            modifier = Modifier.size(MaterialTheme.dimens.iconSmall),
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = {
            expanded = false
            showWatchlists = false
        }
    ) {
        if (userPrivileges) {
            DropdownMenuItem(
                text = { Text("Add to Watchlist") },
                onClick = {
                    showWatchlists = true
                }
            )
        }
    }

    if (showWatchlists && movieDetails != null) {
        DropdownMenu(
            expanded = true,
            onDismissRequest = { showWatchlists = false },
        ) {
            watchlists.value.forEach { list ->
                DropdownMenuItem(
                    text = { Text(list.name) },
                    onClick = {
                        movieDetailViewModel.addToWatchlist(
                            userId.toString(),
                            list.listId,
                            MediaItem(
                                id = movieDetails.id,
                                title = movieDetails.title,
                                posterPath = movieDetails.posterPath.toString(),
                                releaseDate = movieDetails.releaseDate
                            )
                        )
                        showWatchlists = false
                        expanded = false
                    }
                )
            }
        }
    }
}