package com.gromber05.peco.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gromber05.peco.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        load()
    }

    fun onUsernameChange(v: String) = _uiState.update { it.copy(username = v, error = null, saved = false) }
    fun onPhotoChange(v: String) = _uiState.update { it.copy(photo = v, error = null, saved = false) }

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val email = userRepository.sessionEmail.first().orEmpty()
            val entity = userRepository.getUserByEmail(email)

            if (entity == null) {
                _uiState.update { it.copy(isLoading = false, error = "No se pudo cargar el usuario") }
                return@launch
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    username = entity.username,
                    photo = entity.photo.orEmpty()
                )
            }
        }
    }

    fun save() {
        val s = _uiState.value
        if (s.username.isBlank()) {
            _uiState.update { it.copy(error = "El nombre no puede estar vacío") }
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null, saved = false) }

                val email = userRepository.sessionEmail.first().orEmpty()
                val entity = userRepository.getUserByEmail(email)

                if (entity == null) {
                    _uiState.update { it.copy(isLoading = false, error = "No se pudo cargar el usuario") }
                    return@launch
                }

                val updated = entity.copy(
                    username = s.username.trim(),
                    photo = s.photo.trim().ifBlank { null }
                )

                userRepository.updateUser(updated)

                userRepository.refreshCurrentUserFromEmail(email)

                _uiState.update { it.copy(isLoading = false, saved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "No se pudo guardar") }
            }
        }
    }
}