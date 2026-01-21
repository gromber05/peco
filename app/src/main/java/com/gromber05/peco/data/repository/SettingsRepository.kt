package com.gromber05.peco.data.repository

import com.gromber05.peco.data.session.AppPreferences
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val appPrefs: AppPreferences
) {
    val darkMode = appPrefs.darkMode
    suspend fun setDarkMode(enabled: Boolean) = appPrefs.setDarkMode(enabled)
}
