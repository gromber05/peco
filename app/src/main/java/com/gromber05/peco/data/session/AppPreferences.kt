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

/**
 * Gestor de preferencias de la aplicación basado en Jetpack DataStore.
 * Provee un almacenamiento reactivo y seguro para configuraciones locales y estado de sesión.
 *
 * @property dataStore Instancia de [DataStore] para persistir pares clave-valor.
 */
@Singleton
class AppPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    /**
     * Contenedor de claves para el acceso organizado a las preferencias.
     */
    private object Keys {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_ROLE = stringPreferencesKey("user_role")
        val EMAIL = stringPreferencesKey("email")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
    }

    /**
     * Flujo que emite el correo electrónico almacenado del usuario.
     * Emite null si no se ha guardado ningún correo.
     */
    val email: Flow<String?> = dataStore.data.map { it[Keys.EMAIL] }

    /**
     * Flujo que emite la preferencia actual del modo oscuro.
     * Por defecto devuelve [false] (modo claro) si no hay un valor persistido.
     */
    val darkMode: Flow<Boolean> = dataStore.data.map { it[Keys.DARK_MODE] ?: false }

    /**
     * Actualiza y persiste la preferencia del modo oscuro.
     * Al ser una función de suspensión, la operación se realiza de forma segura fuera del hilo principal.
     * * @param enabled [true] para activar el modo oscuro, [false] para desactivarlo.
     */
    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { it[Keys.DARK_MODE] = enabled }
    }
}