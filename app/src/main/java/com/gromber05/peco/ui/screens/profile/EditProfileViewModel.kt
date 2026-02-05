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

/**
 * ViewModel encargado de gestionar la lógica de edición del perfil de usuario.
 *
 * Sigue el patrón MVVM y expone un estado inmutable mediante `StateFlow`, que es observado
 * por la UI construida con Jetpack Compose.
 *
 * Responsabilidades principales:
 * - Cargar el perfil del usuario autenticado al inicializarse.
 * - Gestionar los cambios del formulario (nombre de usuario y foto).
 * - Validar los datos antes de guardar.
 * - Persistir los cambios del perfil usando [UserRepository].
 * - Reflejar en el estado el progreso, errores y éxito del guardado.
 *
 * La inyección de dependencias se realiza mediante Hilt.
 *
 * @property authRepository Repositorio responsable de la sesión y del usuario autenticado (UID).
 * @property usersRepository Repositorio encargado de obtener y actualizar el perfil del usuario.
 */
@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val usersRepository: UserRepository
) : ViewModel() {

    /**
     * Estado mutable interno de la pantalla de edición de perfil.
     * Solo debe modificarse desde este ViewModel.
     */
    private val _uiState = MutableStateFlow(EditProfileUiState())

    /**
     * Estado expuesto de forma inmutable para la capa de UI.
     * La interfaz debe observar este flujo para reaccionar a los cambios.
     */
    val uiState = _uiState.asStateFlow()

    /**
     * Inicializa el ViewModel cargando el perfil del usuario.
     * Se llama automáticamente al crearse el ViewModel.
     */
    init {
        load()
    }

    /**
     * Actualiza el nombre de usuario introducido por el usuario.
     *
     * Al modificar el valor se limpian los mensajes de error y se reinicia
     * el estado de guardado previo.
     *
     * @param v Nuevo nombre de usuario.
     */
    fun onUsernameChange(v: String) =
        _uiState.update { it.copy(username = v, error = null, saved = false) }

    /**
     * Actualiza la URI de la foto de perfil (en formato `String`).
     *
     * Al modificar el valor se limpian los mensajes de error y se reinicia
     * el estado de guardado previo.
     *
     * @param v Nueva URI (o cadena vacía si se elimina).
     */
    fun onPhotoChange(v: String) =
        _uiState.update { it.copy(photo = v, error = null, saved = false) }

    /**
     * Establece la foto seleccionada desde el selector de imágenes del sistema.
     *
     * Se utiliza normalmente cuando el usuario elige una imagen mediante Photo Picker.
     * Internamente actualiza el estado y limpia error/guardado previo.
     *
     * @param uriString URI de la imagen seleccionada en formato `String`.
     */
    fun onPhotoSelected(uriString: String) {
        _uiState.update { it.copy(photo = uriString, error = null, saved = false) }
    }

    /**
     * Carga el perfil del usuario autenticado y rellena el estado inicial del formulario.
     *
     * Flujo:
     * - Activa el estado de carga.
     * - Obtiene el UID desde [AuthRepository].
     * - Recupera el perfil mediante [UserRepository].
     * - Actualiza `username` y `photo` en el estado.
     *
     * En caso de no existir sesión válida o de error al recuperar el perfil,
     * se refleja un mensaje en `error`.
     */
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

    /**
     * Guarda los cambios del perfil del usuario.
     *
     * Realiza validación previa:
     * - El nombre de usuario no puede estar vacío.
     *
     * Flujo:
     * - Activa el estado de carga.
     * - Obtiene el UID desde [AuthRepository].
     * - Llama a [UserRepository.updateProfile] para persistir los cambios.
     * - Marca `saved = true` si el guardado finaliza correctamente.
     *
     * En caso de error o sesión inválida, actualiza el estado con un mensaje en `error`.
     */
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