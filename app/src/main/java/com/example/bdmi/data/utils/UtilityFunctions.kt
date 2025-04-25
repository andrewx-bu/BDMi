package com.example.bdmi.data.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import java.security.MessageDigest

// Returns the password hash in SHA-256
fun hashPassword(password: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val hash = digest.digest(password.toByteArray())
    return hash.joinToString("") { "%02x".format(it) }
}

// Formats budget and revenue
fun formatAmount(value: Long): String {
    val abs = kotlin.math.abs(value)
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