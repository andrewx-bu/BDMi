package com.example.bdmi.ui.composables.movie_detail

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.example.bdmi.data.api.models.MovieDetails
import com.example.bdmi.data.utils.fadingEdge
import com.example.bdmi.ui.theme.dimens
import com.valentinilk.shimmer.shimmer

@Composable
fun DetailColumn(
    details: MovieDetails,
    directors: String,
    trailerKey: String?,
    certification: String
) {
    // Column fades upwards into the backdrop
    val topFadeBrush = Brush.verticalGradient(
        colorStops = arrayOf(
            0f to Color.Transparent.copy(alpha = 0.2f),
            0.2f to Color.Black,
        )
    )

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
                GenreChip(name = genre.name, onClick = { /* TODO: Implement */ })
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
