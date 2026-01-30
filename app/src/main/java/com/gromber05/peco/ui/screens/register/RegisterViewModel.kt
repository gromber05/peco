package com.gromber05.peco.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gromber05.peco.data.repository.AuthRepository
import com.gromber05.peco.data.repository.UserRepository
import com.gromber05.peco.model.user.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val usersRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNameChange(newValue: String) { _uiState.update { it.copy(name = newValue, error = null) } }
    fun onEmailChange(newValue: String) { _uiState.update { it.copy(email = newValue, error = null) } }
    fun onPassChange(newValue: String) { _uiState.update { it.copy(pass = newValue, error = null) } }
    fun onConfirmPassChange(newValue: String) { _uiState.update { it.copy(confirmPass = newValue, error = null) } }
    fun togglePasswordVisibility() { _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) } }

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

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val uid = authRepository.signUp(state.email.trim(), state.pass)

                usersRepository.createProfile(
                    uid = uid,
                    username = state.name.trim(),
                    email = state.email.trim(),
                    role = UserRole.USER
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
