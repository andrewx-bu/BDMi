package com.example.bdmi.data.utils

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("BDMi_Prefs", Context.MODE_PRIVATE)

    fun saveUserId(userId: String) {
        prefs.edit { putString("userId", userId) }
    }

    fun getUserId(): String? {
        return prefs.getString("userId", null)
    }

    fun clearUserId() {
        prefs.edit { remove("userId") }
    }

    fun getDarkMode(): Boolean {
        return prefs.getBoolean("darkMode", false)
    }

    fun setDarkMode(darkMode: Boolean) {
        prefs.edit { putBoolean("darkMode", darkMode) }
    }

}