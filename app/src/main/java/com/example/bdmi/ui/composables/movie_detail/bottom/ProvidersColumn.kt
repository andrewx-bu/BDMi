package com.example.bdmi.ui.composables.movie_detail.bottom

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.bdmi.R
import com.example.bdmi.data.api.models.Provider
import com.example.bdmi.data.utils.ImageURLHelper
import com.example.bdmi.ui.theme.dimens

@Composable
fun ProvidersColumn(
    title: String,
    providers: List<Provider>,
    onProviderClick: (Provider) -> Unit = {}
) {
    SectionHeader(title = title)
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimens.small3),
        contentPadding = PaddingValues(horizontal = dimens.small3)
    ) {
        items(providers) { prov ->
            val logoUrl = ImageURLHelper.getURL(prov.logoPath, width = 200)
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(logoUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.broken_icon),
                error = painterResource(R.drawable.broken_icon),
                contentDescription = prov.providerName,
                modifier = Modifier
                    .size(dimens.personRowHeight)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f))
                    .clickable { onProviderClick(prov) },
                contentScale = ContentScale.Crop
            )
        }
    }
}