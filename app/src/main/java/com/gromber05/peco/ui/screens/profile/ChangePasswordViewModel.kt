package com.gromber05.peco.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gromber05.peco.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel encargado de gestionar la lógica de cambio de contraseña del usuario.
 *
 * Sigue el patrón MVVM y expone un estado inmutable mediante `StateFlow`,
 * que es observado por la UI desarrollada con Jetpack Compose.
 *
 * Responsabilidades principales:
 * - Gestionar los valores del formulario de cambio de contraseña.
 * - Validar los datos introducidos por el usuario.
 * - Ejecutar la operación de cambio de contraseña a través de [AuthRepository].
 * - Actualizar el estado de carga, error y éxito de la operación.
 *
 * La inyección de dependencias se realiza mediante Hilt.
 *
 * @property authRepository Repositorio responsable de las operaciones de autenticación,
 * incluyendo el cambio de contraseña del usuario actual.
 */
@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    /**
     * Estado mutable interno de la pantalla de cambio de contraseña.
     * Solo debe ser modificado desde este ViewModel.
     */
    private val _uiState = MutableStateFlow(ChangePasswordUiState())

    /**
     * Estado expuesto de forma inmutable para la capa de UI.
     * La interfaz debe observar este flujo para reaccionar a los cambios.
     */
    val uiState = _uiState.asStateFlow()

    /**
     * Actualiza la contraseña actual introducida por el usuario.
     *
     * Al modificar el valor se limpian los mensajes de error
     * y el estado de guardado previo.
     *
     * @param v Nueva contraseña actual.
     */
    fun onCurrentChange(v: String) =
        _uiState.update { it.copy(current = v, error = null, saved = false) }

    /**
     * Actualiza la nueva contraseña introducida por el usuario.
     *
     * Al modificar el valor se limpian los mensajes de error
     * y el estado de guardado previo.
     *
     * @param v Nueva contraseña.
     */
    fun onNewChange(v: String) =
        _uiState.update { it.copy(newPass = v, error = null, saved = false) }

    /**
     * Actualiza la confirmación de la nueva contraseña.
     *
     * Al modificar el valor se limpian los mensajes de error
     * y el estado de guardado previo.
     *
     * @param v Confirmación de la nueva contraseña.
     */
    fun onConfirmChange(v: String) =
        _uiState.update { it.copy(confirm = v, error = null, saved = false) }

    /**
     * Ejecuta el proceso de cambio de contraseña.
     *
     * Flujo de la operación:
     * - Valida que todos los campos estén rellenos.
     * - Comprueba la longitud mínima de la nueva contraseña.
     * - Verifica que la nueva contraseña y su confirmación coincidan.
     * - Activa el estado de carga.
     * - Solicita el cambio de contraseña al repositorio de autenticación.
     * - Actualiza el estado final en función del resultado.
     *
     * En caso de error, se muestra un mensaje descriptivo adaptado
     * al tipo de fallo producido.
     */
    fun save() {
        val s = _uiState.value

        if (s.current.isBlank() || s.newPass.isBlank() || s.confirm.isBlank()) {
            _uiState.update {
                it.copy(error = "Rellena todos los campos")
            }
            return
        }

        if (s.newPass.length < 6) {
            _uiState.update {
                it.copy(error = "La nueva contraseña debe tener al menos 6 caracteres")
            }
            return
        }

        if (s.newPass != s.confirm) {
            _uiState.update {
                it.copy(error = "Las contraseñas no coinciden")
            }
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(isLoading = true, error = null, saved = false)
                }

                authRepository.changePassword(
                    currentPassword = s.current,
                    newPassword = s.newPass
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        saved = true,
                        current = "",
                        newPass = "",
                        confirm = ""
                    )
                }
            } catch (e: Exception) {
                val msg = when {
                    e.message?.contains("wrong password", ignoreCase = true) == true ->
                        "Contraseña actual incorrecta"
                    e.message?.contains("requires recent authentication", ignoreCase = true) == true ->
                        "Por seguridad, vuelve a iniciar sesión e inténtalo de nuevo"
                    else ->
                        "No se pudo cambiar la contraseña"
                }

                _uiState.update {
                    it.copy(isLoading = false, error = msg)
                }
            }
        }
    }
}

