package com.example.bdmi.data.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import com.example.bdmi.data.api.models.MovieDetails
import com.example.bdmi.data.repositories.MovieReview
import com.example.bdmi.data.repositories.Review
import com.example.bdmi.data.repositories.UserInfo
import com.example.bdmi.data.repositories.UserReview
import kotlin.math.abs

// Formats budget and revenue
fun formatAmount(value: Long): String {
    val abs = abs(value)
    return when {
        abs >= 1_000_000_000 -> "$%.1fB".format(value / 1_000_000_000.0)
        abs >= 1_000_000 -> "$%.1fM".format(value / 1_000_000.0)
        abs >= 1_000 -> "$%.1fK".format(value / 1_000.0)
        abs == 0L -> "Unknown"
        else -> "$$value"
    }
}

// Formats review numbers
fun formatReviewCount(count: Int): String {
    return when {
        count >= 1_000_000 -> "%.1fM".format(count / 1_000_000f)
        count >= 1_000 -> "%.1fK".format(count / 1_000f)
        else -> count.toString()
    }.replace(".0", "")
}

// Converts 2 letter iso31661 country code to flag emoji
fun String.toFlagEmoji(): String {
    if (this.length != 2) return this
    return this
        .uppercase()
        .map { char ->
            0x1F1E6 + (char.code - 'A'.code)
        }.joinToString("") { codePoint -> String(Character.toChars(codePoint)) }
}

// Fading edge gradient
fun Modifier.fadingEdge(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }

fun createReviewObjects(userInfo: UserInfo, movieDetails: MovieDetails, review: Review) : Pair<UserReview, MovieReview> {
    val userReview = UserReview(
        movieId = movieDetails.id,
        movieTitle = movieDetails.title,
        posterPath = movieDetails.posterPath.toString(),
        reviewTitle = review.reviewTitle,
        reviewText = review.reviewText,
        rating = review.rating,
        spoiler = review.spoiler,
        timestamp = review.timestamp
    )
    val movieReview = MovieReview(
        userId = userInfo.userId,
        displayName = userInfo.displayName.toString(),
        userProfilePicture = userInfo.profilePicture.toString(),
        reviewTitle = review.reviewTitle,
        reviewText = review.reviewText,
        rating = review.rating,
        spoiler = review.spoiler,
        timestamp = review.timestamp
    )
    return Pair(userReview, movieReview)
}