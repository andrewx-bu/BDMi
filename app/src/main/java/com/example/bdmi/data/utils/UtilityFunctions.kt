package com.example.bdmi.data.utils

import android.annotation.SuppressLint
import java.security.MessageDigest

// Returns the password hash in SHA-256
fun hashPassword(password: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val hash = digest.digest(password.toByteArray())
    return hash.joinToString("") { "%02x".format(it) }
}

// Formats budget and revenue
@SuppressLint("DefaultLocale")
fun formatAmount(value: Long): String {
    val abs = kotlin.math.abs(value)
    return when {
        abs >= 1_000_000_000 -> String.format("$%.1fB", value / 1_000_000_000.0)
        abs >= 1_000_000 -> String.format("$%.1fM", value / 1_000_000.0)
        abs >= 1_000 -> String.format("$%.1fK", value / 1_000.0)
        abs == 0L -> "Unknown"
        else -> "$$value"
    }
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