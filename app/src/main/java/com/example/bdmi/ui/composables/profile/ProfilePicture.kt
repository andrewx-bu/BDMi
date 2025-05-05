package com.example.bdmi.ui.composables.profile

import android.net.Uri
import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ProfilePicture(profileImageUrl: String, tempImageUri: Uri?, editPrivileges: Boolean, onEditClick: () -> Unit) {
    val value by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 3000,
                easing = LinearEasing
            )
        )
    )

    // Animation from
    // https://rodrigomartind.medium.com/the-art-of-small-animations-in-android-with-jetpack-compose-566caa94deba
    // TODO: Modify Colors
    val colors = listOf(
        Color(0xFF405DE6),
        Color(0xFFC13584),
        Color(0xFFFD1D1D),
        Color(0xFFFFDC80)
    )
    val gradientBrush by remember {
        mutableStateOf(
            Brush.horizontalGradient(
                colors = colors,
                startX = -10.0f,
                endX = 400.0f,
                tileMode = TileMode.Repeated
            )
        )
    }
    Box(
        modifier = Modifier.size(300.dp),
        contentAlignment = Alignment.Center
    ) {
        Log.d("ProfilePicture", "Loading profileImageUrl: $profileImageUrl")

        // Profile Picture
        Box(
            modifier = Modifier
                .drawBehind {
                    rotate(value) {
                        drawCircle(
                            gradientBrush, style = Stroke(width = 12.dp.value)
                        )
                    }
                }
                .size(250.dp)
        )
        AsyncImage(
            model = tempImageUri ?: profileImageUrl,
            contentDescription = "Profile Picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(250.dp)
                .clip(CircleShape)
        )

        // Edit Icon in Bottom-Right
        if (editPrivileges) {
            IconButton(
                onClick = { onEditClick() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Profile Picture",
                    tint = Color.White // Icon color
                )
            }
        }
    }
}
