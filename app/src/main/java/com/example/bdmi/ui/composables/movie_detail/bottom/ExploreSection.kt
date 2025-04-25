package com.example.bdmi.ui.composables.movie_detail.bottom

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.bdmi.data.api.models.MoviesResponse
import com.example.bdmi.ui.theme.dimens

@Composable
fun ExploreSection(similar: MoviesResponse, recommended: MoviesResponse) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.dimens.medium2)
            .height(MaterialTheme.dimens.personColumnHeight),
    ) {

    }
}