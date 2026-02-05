package com.gromber05.peco.ui.screens.gate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gromber05.peco.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel de la pantalla AuthGate.
 *
 * Su responsabilidad es exponer de forma reactiva el estado de autenticación
 * del usuario para que la UI pueda decidir a qué pantalla navegar.
 *
 * No contiene lógica de UI ni navegación directa, únicamente transforma
 * el estado de autenticación en un [StateFlow] observable.
 *
 * Arquitectura:
 * - Sigue el patrón MVVM.
 * - Consume el flujo de autenticación de [AuthRepository].
 * - Expone un estado simple y reactivo a la UI.
 */
@HiltViewModel
class AuthGateViewModel @Inject constructor(
    /** Repositorio de autenticación que expone el estado de sesión del usuario. */
    authRepository: AuthRepository
) : ViewModel() {

    /**
     * Estado observable que indica si el usuario está autenticado.
     *
     * Valores posibles:
     * - `true`  → el usuario está logueado.
     * - `false` → el usuario NO está logueado.
     * - `null`  → el estado aún no se ha resuelto (estado inicial).
     *
     * Implementación:
     * - Se basa en `currentUidFlow()` del repositorio.
     * - Si el UID es distinto de null → usuario autenticado.
     * - Se convierte en [StateFlow] con `stateIn` para que Compose pueda observarlo.
     *
     * Parámetros de `stateIn`:
     * - scope: [viewModelScope], ligado al ciclo de vida del ViewModel.
     * - started: [SharingStarted.Eagerly], empieza a recolectar inmediatamente.
     * - initialValue: `null`, usado para mostrar un loader mientras se decide.
     */
    val isLogged: StateFlow<Boolean?> =
        authRepository.currentUidFlow()
            .map { uid -> uid != null }
            .stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                null
            )
}
