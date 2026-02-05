package com.gromber05.peco.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gromber05.peco.data.repository.AuthRepository
import com.gromber05.peco.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel encargado de gestionar la lógica de inicio de sesión.
 *
 * Sigue una arquitectura MVVM y expone un estado inmutable (`uiState`)
 * basado en `StateFlow`, que es observado por la UI construida con
 * Jetpack Compose.
 *
 * Se encarga de:
 * - Gestionar los datos introducidos por el usuario.
 * - Validar el formulario de login.
 * - Coordinar el proceso de autenticación.
 * - Recuperar el perfil del usuario.
 * - Actualizar el estado de carga, error y sesión iniciada.
 *
 * La inyección de dependencias se realiza mediante Hilt.
 *
 * @property authRepository Repositorio responsable de la autenticación
 * (inicio y cierre de sesión).
 * @property usersRepository Repositorio encargado de obtener la información
 * del perfil del usuario autenticado.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val usersRepository: UserRepository
) : ViewModel() {

    /**
     * Estado mutable interno de la pantalla de login.
     * Solo debe modificarse desde el propio ViewModel.
     */
    private val _uiState = MutableStateFlow(LoginUiState())

    /**
     * Estado expuesto de forma inmutable para la capa de UI.
     * La interfaz debe observar este flujo para reaccionar
     * a los cambios de estado.
     */
    val uiState = _uiState.asStateFlow()

    /**
     * Actualiza el correo electrónico introducido por el usuario.
     *
     * Al cambiar el valor se limpia cualquier mensaje de error previo.
     *
     * @param email Nuevo valor del campo email.
     */
    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }

    /**
     * Actualiza la contraseña introducida por el usuario.
     *
     * Al cambiar el valor se limpia cualquier mensaje de error previo.
     *
     * @param pass Nueva contraseña introducida.
     */
    fun onPassChange(pass: String) {
        _uiState.update { it.copy(pass = pass, error = null) }
    }

    /**
     * Alterna la visibilidad de la contraseña en el campo de entrada.
     *
     * Se utiliza normalmente para mostrar u ocultar la contraseña
     * mediante un icono en la UI.
     */
    fun togglePasswordVisibility() {
        _uiState.update {
            it.copy(isPasswordVisible = !it.isPasswordVisible)
        }
    }

    /**
     * Inicia el proceso de autenticación del usuario.
     *
     * Flujo general:
     * - Valida que los campos no estén vacíos.
     * - Activa el estado de carga.
     * - Intenta autenticar al usuario.
     * - Obtiene el UID del usuario autenticado.
     * - Recupera el perfil asociado al usuario.
     * - Actualiza el estado final según el resultado.
     *
     * En caso de error, se muestra un mensaje descriptivo
     * adaptado al tipo de fallo producido.
     */
    fun login() {
        val email = _uiState.value.email.trim()
        val pass = _uiState.value.pass

        if (email.isBlank() || pass.isBlank()) {
            _uiState.update {
                it.copy(error = "Rellena todos los campos")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, error = null)
            }

            try {
                authRepository.signIn(email, pass)

                val uid = authRepository.currentUidFlow().first()
                if (uid.isNullOrBlank()) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Sesión no válida"
                        )
                    }
                    return@launch
                }

                val profile = usersRepository.getProfileOnce(uid)
                if (profile == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Perfil no encontrado. Vuelve a registrarte."
                        )
                    }
                    authRepository.signOut()
                    return@launch
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = null,
                        isLoggedIn = true
                    )
                }

            } catch (e: Exception) {
                val msg = when {
                    e.message?.contains("no user record", ignoreCase = true) == true ->
                        "El usuario no existe"
                    e.message?.contains("password is invalid", ignoreCase = true) == true ->
                        "Contraseña incorrecta"
                    e.message?.contains("badly formatted", ignoreCase = true) == true ->
                        "El correo no es válido"
                    else ->
                        "Error al iniciar sesión: ${e.message ?: "desconocido"}"
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = msg
                    )
                }
            }
        }
    }
}