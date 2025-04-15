package com.example.bdmi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.bdmi.data.utils.ImageURLHelper
import com.example.bdmi.ui.theme.Spacing
import com.example.bdmi.ui.theme.UIConstants
import com.example.bdmi.ui.viewmodels.HomeViewModel

@Composable
fun MovieDetailScreen(
    movieId: Int,
    onNavigateBack: () -> Unit
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val movieDetails by viewModel.movieDetails.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadMovieDetails(movieId)
    }

    val backdropURL = ImageURLHelper.getPosterURL(movieDetails?.backdropPath)

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(UIConstants.BACKDROPASPECTRATIO)
        ) {
            AsyncImage(
                model = backdropURL,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().offset(y=-Spacing.large)
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(Spacing.medium),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .background(Color.Gray.copy(alpha = 0.5f), CircleShape)
                        .size(UIConstants.iconButtonSize)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.size(UIConstants.iconSize),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Menu Button
                IconButton(
                    onClick = { /* TODO: Add functionality */ },
                    modifier = Modifier
                        .background(Color.Gray.copy(alpha = 0.5f), CircleShape)
                        .size(UIConstants.iconButtonSize)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreHoriz,
                        contentDescription = "Menu",
                        modifier = Modifier.size(UIConstants.iconSize),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}