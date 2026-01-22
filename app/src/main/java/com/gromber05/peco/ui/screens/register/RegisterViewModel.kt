package com.gromber05.peco.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gromber05.peco.data.local.user.UserEntity
import com.gromber05.peco.data.repository.UserRepository
import com.gromber05.peco.model.user.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository
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
                val existingUser = userRepository.getUserByEmail(state.email)
                if (existingUser != null) {
                    _uiState.update { it.copy(isLoading = false, error = "El correo ya está registrado") }
                    return@launch
                }

                val newUser = UserEntity(
                    username = state.name,
                    email = state.email,
                    password = state.pass,
                    role = UserRole.USER
                )

                userRepository.insertUser(newUser)

                _uiState.update { it.copy(isLoading = false, isRegistered = true) }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al registrar: ${e.message}") }
            }
        }
    }
}