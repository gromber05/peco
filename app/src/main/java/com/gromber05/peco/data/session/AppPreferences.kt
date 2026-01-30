package com.gromber05.peco.data.session

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.gromber05.peco.model.user.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_ROLE = stringPreferencesKey("user_role")
        val EMAIL = stringPreferencesKey("email")

        val DARK_MODE = booleanPreferencesKey("dark_mode")
    }

    val isLoggedIn: Flow<Boolean> = dataStore.data.map { it[Keys.IS_LOGGED_IN] ?: false }
    val userRole: Flow<UserRole> =
        dataStore.data.map { prefs ->
            val raw = prefs[Keys.USER_ROLE] ?: UserRole.USER.name
            runCatching { UserRole.valueOf(raw) }
                .getOrDefault(UserRole.USER)
        }
    val isAdmin: Flow<Boolean> = userRole.map { it == UserRole.ADMIN }
    val isVolunteer: Flow<Boolean> = userRole.map { it == UserRole.VOLUNTEER }
    val email: Flow<String?> = dataStore.data.map { it[Keys.EMAIL] }

    suspend fun saveSession(email: String, role: UserRole) {
        dataStore.edit {
            it[Keys.IS_LOGGED_IN] = true
            it[Keys.USER_ROLE] = role.name
            it[Keys.EMAIL] = email
        }
    }

    suspend fun clearSession() {
        dataStore.edit {
            it[Keys.IS_LOGGED_IN] = false
            it.remove(Keys.USER_ROLE)
            it.remove(Keys.EMAIL)
        }
    }

    val darkMode: Flow<Boolean> = dataStore.data.map { it[Keys.DARK_MODE] ?: false }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { it[Keys.DARK_MODE] = enabled }
    }
}
