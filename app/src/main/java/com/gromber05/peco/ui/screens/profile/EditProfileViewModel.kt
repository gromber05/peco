package com.gromber05.peco.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gromber05.peco.data.repository.AuthRepository
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
    private val authRepository: AuthRepository,
    private val usersRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        load()
    }

    fun onUsernameChange(v: String) =
        _uiState.update { it.copy(username = v, error = null, saved = false) }

    fun onPhotoChange(v: String) =
        _uiState.update { it.copy(photo = v, error = null, saved = false) }

    fun onPhotoSelected(uriString: String) {
        _uiState.update { it.copy(photo = uriString, error = null, saved = false) }
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, saved = false) }

            val uid = authRepository.currentUidFlow().first()
            if (uid.isNullOrBlank()) {
                _uiState.update { it.copy(isLoading = false, error = "Sesión no válida") }
                return@launch
            }

            try {
                val profile = usersRepository.getProfileOnce(uid)
                if (profile == null) {
                    _uiState.update { it.copy(isLoading = false, error = "No se pudo cargar el usuario") }
                    return@launch
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        username = profile.username,
                        photo = profile.photo.orEmpty()
                    )
                }
            } catch (_: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "No se pudo cargar el usuario") }
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
            _uiState.update { it.copy(isLoading = true, error = null, saved = false) }

            val uid = authRepository.currentUidFlow().first()
            if (uid.isNullOrBlank()) {
                _uiState.update { it.copy(isLoading = false, error = "Sesión no válida") }
                return@launch
            }

            try {
                usersRepository.updateProfile(
                    uid = uid,
                    username = s.username.trim(),
                    photo = s.photo.trim().ifBlank { null }
                )

                _uiState.update { it.copy(isLoading = false, saved = true) }
            } catch (_: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "No se pudo guardar") }
            }
        }
    }
}
