package com.example.bdmi.ui.composables.movie_detail.bottom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.bdmi.R
import com.example.bdmi.data.api.models.MovieDetails
import com.example.bdmi.data.api.models.WatchProvidersResponse
import com.example.bdmi.data.utils.ImageURLHelper
import com.example.bdmi.data.utils.formatAmount
import com.example.bdmi.data.utils.toFlagEmoji
import com.example.bdmi.ui.theme.dimens

@Composable
fun DetailsSection(
    details: MovieDetails,
    providers: WatchProvidersResponse?,
    onStudioClick: (Int) -> Unit,
    onCountryClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimens.medium2)
            .height(dimens.bottomColumnHeight),
    ) {
        // General Information
        item {
            SectionHeader(title = "GENERAL")
            HorizontalDivider(
                modifier = Modifier.padding(vertical = dimens.small2),
                color = MaterialTheme.colorScheme.inverseSurface
            )
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
                modifier = Modifier.padding(vertical = dimens.small2),
                color = MaterialTheme.colorScheme.inverseSurface
            )
            SectionHeader(title = "COUNTRIES")
        }

        // Production Countries
        items(details.productionCountries) { country ->
            HorizontalDivider(
                modifier = Modifier.padding(vertical = dimens.small2),
                color = MaterialTheme.colorScheme.inverseSurface
            )
            ListItemRow(
                onClick = { onCountryClick(country.iso31661) },
                leading = {
                    Text(
                        country.iso31661.toFlagEmoji(),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                title = country.name,
                height = dimens.countryRowHeight
            )
        }

        // Studios Header
        item {
            HorizontalDivider(
                modifier = Modifier.padding(vertical = dimens.small2),
                color = MaterialTheme.colorScheme.inverseSurface
            )
            SectionHeader(title = "STUDIOS")
        }

        // Production Companies
        items(details.productionCompanies) { studio ->
            HorizontalDivider(
                modifier = Modifier.padding(vertical = dimens.small2),
                color = MaterialTheme.colorScheme.inverseSurface
            )
            ListItemRow(
                onClick = { onStudioClick(studio.id) },
                leading = {
                    val logoURL = ImageURLHelper.getURL(studio.logoPath, width = 200)
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(dimens.medium2))
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
                            modifier = Modifier.padding(dimens.small1),
                            contentScale = ContentScale.Fit
                        )
                    }
                },
                title = studio.name,
                subtitle = studio.country?.toFlagEmoji() + " " + studio.country,
                height = dimens.personRowHeight,
            )
        }


        item {
            HorizontalDivider(
                modifier = Modifier.padding(vertical = dimens.small2),
                color = MaterialTheme.colorScheme.inverseSurface
            )
        }

        val usProviders = providers?.results?.us
        usProviders?.let { p ->
            item { ProvidersColumn(title = "RENT", providers = p.rent) }
            item { ProvidersColumn(title = "BUY", providers = p.buy) }
        }
    }
}