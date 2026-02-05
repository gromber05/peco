package com.gromber05.peco.ui.screens.gate

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

/**
 * Pantalla pasarela de autenticación (Auth Gate).
 *
 * Su función es decidir automáticamente a qué pantalla navegar
 * según el estado de autenticación del usuario.
 *
 * Comportamiento:
 * - Si el usuario está autenticado → navega a Home.
 * - Si el usuario no está autenticado → navega a Login.
 * - Si el estado aún es desconocido (null) → muestra un loader.
 *
 * Esta pantalla no muestra contenido interactivo, únicamente
 * un indicador de carga mientras se resuelve el estado de sesión.
 *
 * Arquitectura:
 * - Sigue patrón MVVM.
 * - Consume el estado expuesto por [AuthGateViewModel].
 * - Delegación de navegación mediante callbacks.
 *
 * @param onGoHome Callback para navegar a la pantalla principal.
 * @param onGoLogin Callback para navegar a la pantalla de login.
 * @param viewModel ViewModel inyectado por Hilt que expone el estado de autenticación.
 */
@Composable
fun AuthGate(
    onGoHome: () -> Unit,
    onGoLogin: () -> Unit,
    viewModel: AuthGateViewModel = hiltViewModel()
) {
    /**
     * Estado de autenticación observado desde el ViewModel.
     *
     * Valores posibles:
     * - true  → usuario autenticado
     * - false → usuario no autenticado
     * - null  → estado aún no resuelto
     */
    val isLogged by viewModel.isLogged.collectAsState()

    /**
     * Efecto que reacciona a cambios en el estado de autenticación.
     *
     * Se ejecuta cada vez que cambia isLogged y decide la navegación:
     * - true  → Home
     * - false → Login
     * - null  → no hace nada (se mantiene el loader)
     */
    LaunchedEffect(isLogged) {
        when (isLogged) {
            true -> onGoHome()
            false -> onGoLogin()
            null -> Unit
        }
    }

    /**
     * UI mínima: pantalla completa con indicador de carga centrado.
     *
     * Se muestra mientras se determina el estado de autenticación.
     */
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
