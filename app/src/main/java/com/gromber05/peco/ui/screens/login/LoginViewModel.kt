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

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val usersRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChange(email: String) { _uiState.update { it.copy(email = email, error = null) } }
    fun onPassChange(pass: String) { _uiState.update { it.copy(pass = pass, error = null) } }
    fun togglePasswordVisibility() { _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) } }

    fun login() {
        val email = _uiState.value.email.trim()
        val pass = _uiState.value.pass

        if (email.isBlank() || pass.isBlank()) {
            _uiState.update { it.copy(error = "Rellena todos los campos") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                authRepository.signIn(email, pass)

                val uid = authRepository.currentUidFlow().first()
                if (uid.isNullOrBlank()) {
                    _uiState.update { it.copy(isLoading = false, error = "Sesión no válida") }
                    return@launch
                }

                val profile = usersRepository.getProfileOnce(uid)
                if (profile == null) {
                    _uiState.update { it.copy(isLoading = false, error = "Perfil no encontrado. Vuelve a registrarte.") }
                    authRepository.signOut()
                    return@launch
                }

                _uiState.update { it.copy(isLoading = false, error = null, isLoggedIn = true) }

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

                _uiState.update { it.copy(isLoading = false, error = msg) }
            }
        }
    }
}
