package com.example.bdmi.ui.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.bdmi.R
import com.example.bdmi.UserViewModel
import com.example.bdmi.data.api.models.CastMember
import com.example.bdmi.data.api.models.CrewMember
import com.example.bdmi.data.utils.ImageURLHelper
import com.example.bdmi.data.api.models.MovieDetails
import com.example.bdmi.data.api.models.MoviesResponse
import com.example.bdmi.data.api.models.WatchProvidersResponse
import com.example.bdmi.ui.theme.dimens
import com.example.bdmi.ui.theme.uiConstants
import com.example.bdmi.data.repositories.MediaItem
import com.example.bdmi.data.utils.fadingEdge
import com.example.bdmi.data.utils.formatAmount
import com.example.bdmi.data.utils.toFlagEmoji
import com.example.bdmi.ui.composables.ErrorMessage
import com.example.bdmi.ui.composables.LoadingIndicator
import com.example.bdmi.ui.composables.movie_detail.ReviewCarousel
import com.example.bdmi.ui.composables.MoviePoster
import com.example.bdmi.ui.composables.movie_detail.DetailColumn
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
            LoadingIndicator()
        }

        details != null -> {
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
                    // TODO: Add actual reviews
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
                    MiddleSection(details, reviews)
                }

                item {
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

            DetailColumn(details, directors, trailerKey, certification)
        }
    }
}

// Description and Reviews
@Composable
fun MiddleSection(details: MovieDetails, reviews: List<String>) {
    var isExpanded by remember { mutableStateOf(false) }
    val textLayoutResultState = remember { mutableStateOf<TextLayoutResult?>(null) }
    val maxLines = if (isExpanded) Int.MAX_VALUE else MaterialTheme.uiConstants.descriptionMaxLines
    Column(
        modifier = Modifier.padding(
            horizontal = MaterialTheme.dimens.medium2,
            vertical = MaterialTheme.dimens.small3
        )
    ) {
        if (!details.tagline.isNullOrEmpty()) {
            Text(
                text = details.tagline.uppercase(Locale.ROOT),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.inverseSurface,
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
        Spacer(Modifier.height(MaterialTheme.dimens.small2))
        ReviewCarousel(reviews = reviews)
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = MaterialTheme.dimens.medium1),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    Modifier
                        .weight(0.25f)
                        .padding(end = MaterialTheme.dimens.small3),
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "COUNTRIES",
                    style = MaterialTheme.typography.titleMedium,
                )
                HorizontalDivider(
                    Modifier
                        .weight(1f)
                        .padding(start = MaterialTheme.dimens.small3),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        items(details.productionCountries) { country ->
            HorizontalDivider()
            Spacer(modifier = Modifier.height(MaterialTheme.dimens.small2))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MaterialTheme.dimens.countryColumnHeight)
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = MaterialTheme.dimens.medium1),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    Modifier
                        .weight(0.25f)
                        .padding(end = MaterialTheme.dimens.small3),
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "STUDIOS",
                    style = MaterialTheme.typography.titleMedium,
                )
                HorizontalDivider(
                    Modifier
                        .weight(1f)
                        .padding(start = MaterialTheme.dimens.small3),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
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

        if (providers == null) return@LazyColumn
        item {
            HorizontalDivider(
                modifier = Modifier.padding(
                    top = MaterialTheme.dimens.small2,
                    bottom = MaterialTheme.dimens.large3
                )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = MaterialTheme.dimens.medium1),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    Modifier
                        .weight(0.25f)
                        .padding(end = MaterialTheme.dimens.small3),
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "WATCH PROVIDERS",
                    style = MaterialTheme.typography.titleMedium,
                )
                HorizontalDivider(
                    Modifier
                        .weight(1f)
                        .padding(start = MaterialTheme.dimens.small3),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        val usProviders = providers.results.us
        usProviders?.let { p ->
            item {
                Text(
                    text = "RENT",
                    style = MaterialTheme.typography.titleMedium,
                )
                HorizontalDivider(Modifier.padding(vertical = MaterialTheme.dimens.small2))
            }
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.small2),
                ) {
                    items(p.rent) { prov ->
                        val logoURL = ImageURLHelper.getURL(prov.logoPath, width = 200)
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(logoURL)
                                .crossfade(true)
                                .build(),
                            placeholder = painterResource(R.drawable.broken_icon),
                            error = painterResource(R.drawable.broken_icon),
                            contentDescription = null,
                            modifier = Modifier
                                .size(75.dp)
                                .padding(MaterialTheme.dimens.medium1)
                                .clip(
                                    CircleShape
                                ),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }

            item {
                Text(
                    text = "BUY",
                    style = MaterialTheme.typography.titleMedium,
                )
                HorizontalDivider(Modifier.padding(vertical = MaterialTheme.dimens.small2))
            }
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.small2),
                ) {
                    items(p.rent) { prov ->
                        val logoURL = ImageURLHelper.getURL(prov.logoPath, width = 200)
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(logoURL)
                                .crossfade(true)
                                .build(),
                            placeholder = painterResource(R.drawable.broken_icon),
                            error = painterResource(R.drawable.broken_icon),
                            contentDescription = null,
                            modifier = Modifier
                                .size(75.dp)
                                .padding(MaterialTheme.dimens.medium1)
                                .clip(
                                    CircleShape
                                ),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
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