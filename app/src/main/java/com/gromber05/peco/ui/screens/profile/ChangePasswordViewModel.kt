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

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState = _uiState.asStateFlow()

    fun onCurrentChange(v: String) = _uiState.update { it.copy(current = v, error = null, saved = false) }
    fun onNewChange(v: String) = _uiState.update { it.copy(newPass = v, error = null, saved = false) }
    fun onConfirmChange(v: String) = _uiState.update { it.copy(confirm = v, error = null, saved = false) }

    fun save() {
        val s = _uiState.value

        if (s.current.isBlank() || s.newPass.isBlank() || s.confirm.isBlank()) {
            _uiState.update { it.copy(error = "Rellena todos los campos") }
            return
        }
        if (s.newPass.length < 6) {
            _uiState.update { it.copy(error = "La nueva contraseña debe tener al menos 6 caracteres") }
            return
        }
        if (s.newPass != s.confirm) {
            _uiState.update { it.copy(error = "Las contraseñas no coinciden") }
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null, saved = false) }

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

                _uiState.update { it.copy(isLoading = false, error = msg) }
            }
        }
    }
}
