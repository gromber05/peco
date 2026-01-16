package com.gromber05.peco.data.session

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "app_prefs")

class AppPreferences(private val context: Context) {

    private object Keys {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val IS_ADMIN = booleanPreferencesKey("is_admin")
        val EMAIL = stringPreferencesKey("email")

        val DARK_MODE = booleanPreferencesKey("dark_mode")
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { it[Keys.IS_LOGGED_IN] ?: false }
    val isAdmin: Flow<Boolean> = context.dataStore.data.map { it[Keys.IS_ADMIN] ?: false }
    val email: Flow<String?> = context.dataStore.data.map { it[Keys.EMAIL] }

    suspend fun saveSession(email: String, isAdmin: Boolean) {
        context.dataStore.edit {
            it[Keys.IS_LOGGED_IN] = true
            it[Keys.IS_ADMIN] = isAdmin
            it[Keys.EMAIL] = email
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit {
            it[Keys.IS_LOGGED_IN] = false
            it[Keys.IS_ADMIN] = false
            it.remove(Keys.EMAIL)
        }
    }

    val darkMode: Flow<Boolean> = context.dataStore.data.map { it[Keys.DARK_MODE] ?: false }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DARK_MODE] = enabled }
    }
}
