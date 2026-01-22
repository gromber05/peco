package com.gromber05.peco.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gromber05.peco.data.local.user.toUser
import com.gromber05.peco.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) { _uiState.update { it.copy(email = email, error = null) } }
    fun onPassChange(pass: String) { _uiState.update { it.copy(pass = pass, error = null) } }
    fun togglePasswordVisibility() { _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) } }

    fun login() {
        val email = _uiState.value.email
        val pass = _uiState.value.pass

        if (email.isBlank() || pass.isBlank()) {
            _uiState.update { it.copy(error = "Rellena todos los campos") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val user = userRepository.getUserByEmail(email)

            if (user != null) {
                if (user.password == pass) {
                    userRepository.setCurrentUser(user.toUser())
                    userRepository.saveSession(email = user.email, role = user.role)

                    _uiState.update { it.copy(isLoading = false, error = null) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Contraseña incorrecta") }
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "El usuario no existe") }
            }
        }
    }
}