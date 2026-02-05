package com.gromber05.peco.data.repository

import com.gromber05.peco.data.session.AppPreferences
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio encargado de gestionar la configuración y preferencias persistentes de la aplicación.
 * Centraliza el acceso a las opciones personalizables por el usuario, como el tema visual.
 *
 * @property appPrefs Fuente de datos local ([AppPreferences]) donde se almacenan físicamente las preferencias.
 */
@Singleton
class SettingsRepository @Inject constructor(
    private val appPrefs: AppPreferences
) {
    /**
     * Flujo ([Flow]) que emite el estado actual del modo oscuro.
     * Permite que la aplicación reaccione en tiempo real cuando el usuario cambia el tema.
     */
    val darkMode = appPrefs.darkMode

    /**
     * Actualiza la preferencia del modo oscuro de forma persistente.
     * Al ser una función de suspensión, garantiza que la escritura en disco no bloquee el hilo principal.
     *
     * @param enabled [true] para activar el modo oscuro, [false] para el modo claro.
     */
    suspend fun setDarkMode(enabled: Boolean) = appPrefs.setDarkMode(enabled)
}