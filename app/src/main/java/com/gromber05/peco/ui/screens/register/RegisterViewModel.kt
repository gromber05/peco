package com.gromber05.peco.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gromber05.peco.data.repository.AuthRepository
import com.gromber05.peco.data.repository.UserRepository
import com.gromber05.peco.model.user.UserRole
import com.gromber05.peco.utils.normalizePhone
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * ViewModel encargado de gestionar la lógica de registro de usuario.
 *
 * Sigue el patrón MVVM y expone un estado inmutable mediante [StateFlow] que es observado
 * por la UI construida con Jetpack Compose.
 *
 * Responsabilidades principales:
 * - Gestionar los valores del formulario (nombre, email, teléfono, contraseña y confirmación).
 * - Validar los datos antes de registrar.
 * - Normalizar y validar el número de teléfono.
 * - Crear la cuenta en el sistema de autenticación usando [AuthRepository].
 * - Crear el perfil del usuario en la base de datos usando [UserRepository].
 * - Actualizar el estado de carga, error y resultado del registro.
 *
 * La inyección de dependencias se realiza mediante Hilt.
 *
 * @property authRepository Repositorio responsable de operaciones de autenticación (registro).
 * @property usersRepository Repositorio encargado de crear el perfil del usuario tras el registro.
 */
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val usersRepository: UserRepository
) : ViewModel() {

    /**
     * Estado mutable interno de la pantalla de registro.
     * Solo debe modificarse desde este ViewModel.
     */
    private val _uiState = MutableStateFlow(RegisterUiState())

    /**
     * Estado expuesto de forma inmutable para la UI.
     * La interfaz debe observar este flujo para reaccionar a los cambios.
     */
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    /**
     * Actualiza el nombre introducido por el usuario y limpia el error previo.
     *
     * @param newValue Nuevo valor del nombre.
     */
    fun onNameChange(newValue: String) { _uiState.update { it.copy(name = newValue, error = null) } }

    /**
     * Actualiza el email introducido por el usuario y limpia el error previo.
     *
     * @param newValue Nuevo valor del email.
     */
    fun onEmailChange(newValue: String) { _uiState.update { it.copy(email = newValue, error = null) } }

    /**
     * Actualiza la contraseña introducida por el usuario y limpia el error previo.
     *
     * @param newValue Nuevo valor de la contraseña.
     */
    fun onPassChange(newValue: String) { _uiState.update { it.copy(pass = newValue, error = null) } }

    /**
     * Actualiza la confirmación de la contraseña introducida por el usuario y limpia el error previo.
     *
     * @param newValue Nuevo valor de la confirmación de contraseña.
     */
    fun onConfirmPassChange(newValue: String) { _uiState.update { it.copy(confirmPass = newValue, error = null) } }

    /**
     * Alterna la visibilidad de la contraseña en los campos de contraseña.
     */
    fun togglePasswordVisibility() { _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) } }

    /**
     * Actualiza el teléfono introducido por el usuario y limpia el error previo.
     *
     * @param newValue Nuevo valor del teléfono.
     */
    fun onPhoneChange(newValue: String) { _uiState.update { it.copy(phone = newValue, error = null) }}

    /**
     * Comprueba si un número de teléfono es válido.
     *
     * Acepta:
     * - Un prefijo `+` opcional.
     * - Entre 9 y 15 dígitos.
     *
     * @param phone Teléfono (preferiblemente ya normalizado).
     * @return `true` si el teléfono cumple el patrón, `false` en caso contrario.
     */
    private fun isValidPhone(phone: String): Boolean =
        phone.matches(Regex("^\\+?[0-9]{9,15}\$"))

    /**
     * Ejecuta el proceso de registro.
     *
     * Validaciones realizadas:
     * - Campos obligatorios (nombre, email y contraseña) no vacíos.
     * - Coincidencia de contraseña y confirmación.
     * - Longitud mínima de contraseña (>= 6).
     * - Normalización y validación del teléfono (9-15 dígitos, `+` opcional).
     *
     * Flujo principal:
     * - Activa el estado de carga.
     * - Crea la cuenta mediante [AuthRepository.signUp].
     * - Crea el perfil del usuario con rol [UserRole.USER] mediante [UserRepository.createProfile].
     * - Marca `isRegistered = true` al completar correctamente.
     *
     * En caso de error, adapta el mensaje según el tipo de fallo detectado.
     */
    fun register() {
        val state = _uiState.value

        if (state.name.isBlank() || state.email.isBlank() || state.pass.isBlank()) {
            _uiState.update { it.copy(error = "Por favor, rellena todos los campos") }
            return
        }
        if (state.pass != state.confirmPass) {
            _uiState.update { it.copy(error = "Las contraseñas no coinciden") }
            return
        }
        if (state.pass.length < 6) {
            _uiState.update { it.copy(error = "La contraseña debe tener al menos 6 caracteres") }
            return
        }

        val phoneNormalized = normalizePhone(state.phone)
        if (!isValidPhone(phoneNormalized)) {
            _uiState.update { it.copy(error = "El teléfono no es válido (usa 9-15 dígitos, opcional +)") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val uid = authRepository.signUp(state.email.trim(), state.pass)

                usersRepository.createProfile(
                    uid = uid,
                    username = state.name.trim(),
                    email = state.email.trim(),
                    role = UserRole.USER,
                    phone = phoneNormalized
                )

                _uiState.update { it.copy(isLoading = false, isRegistered = true) }

            } catch (e: Exception) {
                val msg = when {
                    e.message?.contains("email address is already in use", ignoreCase = true) == true ->
                        "El correo ya está registrado"
                    e.message?.contains("badly formatted", ignoreCase = true) == true ->
                        "El correo no es válido"
                    else ->
                        "Error al registrar: ${e.message ?: "desconocido"}"
                }

                _uiState.update { it.copy(isLoading = false, error = msg) }
            }
        }
    }
}
