package com.gromber05.peco.ui.screens.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gromber05.peco.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel de la pantalla de recuperación de contraseña.
 *
 * Responsabilidades:
 * - Mantener el estado de UI ([ForgotPasswordUiState]).
 * - Validar el correo introducido por el usuario.
 * - Solicitar el envío del correo de recuperación mediante [AuthRepository].
 * - Gestionar estados de carga, éxito y error.
 *
 * Arquitectura:
 * - Sigue el patrón MVVM.
 * - Usa [StateFlow] para exponer el estado a la UI (Jetpack Compose).
 * - Inyección de dependencias con Hilt mediante [@HiltViewModel].
 *
 * Flujo principal:
 * 1) El usuario introduce su email.
 * 2) Se valida que no esté vacío.
 * 3) Se llama a Firebase Auth para enviar el correo de recuperación.
 * 4) Se actualiza la UI según éxito o error.
 */
@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    /** Repositorio de autenticación que encapsula FirebaseAuth. */
    private val authRepository: AuthRepository
) : ViewModel() {

    /**
     * Estado interno mutable de la pantalla.
     * Se expone como inmutable mediante [uiState].
     */
    private val _uiState = MutableStateFlow(ForgotPasswordUiState())

    /** Estado observable desde la UI. */
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState

    /**
     * Actualiza el email introducido por el usuario.
     *
     * Además:
     * - Limpia cualquier error previo.
     * - Reinicia el estado de éxito.
     *
     * @param v Nuevo valor del email.
     */
    fun onEmailChange(v: String) {
        _uiState.update { it.copy(email = v, error = null, success = false) }
    }

    /**
     * Envía el correo de recuperación de contraseña.
     *
     * Validaciones:
     * - El email no puede estar vacío.
     *
     * Flujo:
     * 1) Valida el email.
     * 2) Marca `isLoading=true`.
     * 3) Llama a [AuthRepository.sendPasswordResetEmail].
     * 4) Si tiene éxito, marca `success=true`.
     * 5) Si falla, muestra un mensaje de error.
     */
    fun sendReset() {
        val email = uiState.value.email.trim()
        if (email.isBlank()) {
            _uiState.update { it.copy(error = "Introduce un correo válido") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, success = false) }
            try {
                authRepository.sendPasswordResetEmail(email)
                _uiState.update { it.copy(isLoading = false, success = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "No se pudo enviar el correo"
                    )
                }
            }
        }
    }
}
