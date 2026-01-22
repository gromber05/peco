package com.gromber05.peco.data.session

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.BuildConfig
import com.gromber05.peco.model.user.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "app_prefs")

class AppPreferences(private val context: Context) {
    private object Keys {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_ROLE = stringPreferencesKey("user_role")
        val EMAIL = stringPreferencesKey("email")

        val DARK_MODE = booleanPreferencesKey("dark_mode")
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { it[Keys.IS_LOGGED_IN] ?: false }
    val userRole: Flow<UserRole> =
        context.dataStore.data.map { prefs ->
            val raw = prefs[Keys.USER_ROLE] ?: UserRole.USER.name
            runCatching { UserRole.valueOf(raw) }
                .getOrDefault(UserRole.USER)
        }
    val isAdmin: Flow<Boolean> = userRole.map { it == UserRole.ADMIN }
    val isVolunteer: Flow<Boolean> = userRole.map { it == UserRole.VOLUNTEER }
    val email: Flow<String?> = context.dataStore.data.map { it[Keys.EMAIL] }

    suspend fun saveSession(email: String, role: UserRole) {
        context.dataStore.edit {
            it[Keys.IS_LOGGED_IN] = true
            it[Keys.USER_ROLE] = role.name
            it[Keys.EMAIL] = email
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit {
            it[Keys.IS_LOGGED_IN] = false
            it.remove(Keys.USER_ROLE)
            it.remove(Keys.EMAIL)
        }
    }

    val darkMode: Flow<Boolean> = context.dataStore.data.map { it[Keys.DARK_MODE] ?: false }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DARK_MODE] = enabled }
    }
}
