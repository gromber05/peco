package com.gromber05.peco.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gromber05.peco.data.repository.AuthRepository
import com.gromber05.peco.data.repository.SettingsRepository
import com.gromber05.peco.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel de nivel de aplicación (App-level) para exponer estados globales.
 *
 * Centraliza información y acciones que afectan a toda la app, como:
 * - Estado de autenticación (sesión iniciada o no).
 * - Preferencias globales (por ejemplo, modo oscuro).
 *
 * Utiliza `StateFlow` para que la UI (Jetpack Compose) pueda observar cambios de forma reactiva.
 * La inyección de dependencias se realiza mediante Hilt.
 *
 * @property userRepository Repositorio de usuarios. (Se inyecta para acceso a operaciones de usuario
 * a nivel global si la app lo requiere).
 * @property authRepository Repositorio de autenticación, encargado de exponer el estado de sesión.
 * @property settingsRepository Repositorio de preferencias/ajustes, como el modo oscuro.
 */
@HiltViewModel
class AppViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    /**
     * Estado observable que indica si hay sesión iniciada.
     *
     * - `null`: estado inicial/indeterminado mientras se resuelve la sesión.
     * - `true`: usuario autenticado.
     * - `false`: no hay usuario autenticado.
     *
     * Se convierte a [StateFlow] mediante `stateIn` para disponer de un valor actual,
     * y se mantiene activo mientras haya suscriptores (con un timeout de 5s).
     */
    val isLoggedInOrNull: StateFlow<Boolean?> =
        authRepository.isLoggedInFlow()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null
            )

    /**
     * Preferencia observable del modo oscuro.
     *
     * Normalmente es un `Flow<Boolean>` expuesto por [SettingsRepository] que la UI
     * consume para aplicar tema claro/oscuro.
     */
    val darkMode = settingsRepository.darkMode

    /**
     * Alterna el modo oscuro.
     *
     * Lee el valor actual de [darkMode] y persiste el valor contrario mediante
     * [SettingsRepository.setDarkMode].
     */
    fun toggleDarkMode() {
        viewModelScope.launch {
            val current = darkMode.first()
            settingsRepository.setDarkMode(!current)
        }
    }
}
