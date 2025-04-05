package com.example.bdmi.utils

// Returns the password hash in SHA-256
fun hashPassword(password: String): String {
    val digest = java.security.MessageDigest.getInstance("SHA-256")
    val hash = digest.digest(password.toByteArray())
    return hash.joinToString("") { "%02x".format(it) }
}