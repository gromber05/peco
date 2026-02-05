package com.gromber05.peco.ui.screens.admin

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gromber05.peco.data.repository.AnimalRepository
import com.gromber05.peco.data.repository.AuthRepository
import com.gromber05.peco.data.repository.LocationRepository
import com.gromber05.peco.model.AdoptionState
import com.gromber05.peco.model.data.Animal
import com.gromber05.peco.model.events.UiEvent
import com.gromber05.peco.utils.uriToBytes
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel de la pantalla de administración para crear un nuevo animal.
 *
 * Responsabilidades:
 * - Mantener el estado de UI ([AdminAddAnimalUiState]) con los campos del formulario.
 * - Validar datos antes de guardar (nombre, especie, fecha de nacimiento, coordenadas).
 * - Autocompletar ubicación usando [LocationRepository] si falta lat/lon.
 * - Crear el animal a través de [AnimalRepository].
 * - Emitir eventos de UI ([UiEvent]) para mostrar mensajes (éxito / error).
 *
 * Arquitectura:
 * - Sigue patrón MVVM.
 * - Usa StateFlow para estado reactivo y SharedFlow para eventos "one-shot".
 * - Inyección de dependencias con Hilt mediante [@HiltViewModel].
 */
@HiltViewModel
class AdminAddAnimalViewModel @Inject constructor(
    /** Repositorio para operaciones CRUD relacionadas con animales. */
    private val animalRepository: AnimalRepository,
    /** Repositorio encargado de obtener la ubicación actual del dispositivo. */
    private val locationRepository: LocationRepository,
    /** Repositorio de autenticación para obtener el UID actual del usuario. */
    private val authRepository: AuthRepository,
) : ViewModel() {

    /**
     * Estado interno mutable del formulario.
     * Se expone como [StateFlow] inmutable mediante [uiState].
     */
    private val _uiState = MutableStateFlow(AdminAddAnimalUiState())
    val uiState = _uiState.asStateFlow()

    /**
     * Flujo de eventos de UI (mensajes de error/éxito).
     * Usado típicamente para Snackbars, Toasts o navegación.
     */
    private val _events = MutableSharedFlow<UiEvent>()
    val events = _events.asSharedFlow()

    /** Actualiza el campo "nombre" del formulario. */
    fun onNameChange(v: String) = _uiState.update { it.copy(name = v) }

    /** Actualiza el campo "especie" del formulario. */
    fun onSpeciesChange(v: String) = _uiState.update { it.copy(species = v) }

    /** Actualiza el campo "fecha de nacimiento" del formulario. */
    fun onDobChange(v: String) = _uiState.update { it.copy(dob = v) }

    /** Actualiza el estado de adopción seleccionado (AVAILABLE / ADOPTED / PENDING...). */
    fun onStateChange(v: AdoptionState) = _uiState.update { it.copy(adoptionState = v) }

    /** Actualiza la URI de la foto seleccionada (normalmente para previsualizar en UI). */
    fun onPhotoUriChange(uri: String) = _uiState.update { it.copy(photoUri = uri) }

    /**
     * Autocompleta latitud/longitud si aún no se han introducido.
     *
     * Funcionamiento:
     * - Si ya hay lat/lon, no hace nada.
     * - Si están vacíos, intenta obtener la ubicación actual con [LocationRepository].
     * - Si se obtiene ubicación, la copia al estado.
     * - Si falla, emite un [UiEvent.Error] para notificar al usuario.
     */
    fun autoFillLocationIfNeeded() {
        val s = uiState.value
        if (s.latitude.isNotBlank() && s.longitude.isNotBlank()) return

        viewModelScope.launch {
            val loc = locationRepository.getCurrentLocation()
            if (loc != null) {
                val (lat, lon) = loc
                _uiState.update {
                    it.copy(
                        latitude = lat.toString(),
                        longitude = lon.toString()
                    )
                }
            } else {
                _events.emit(UiEvent.Error("No se pudo obtener la ubicación. Activa GPS o inténtalo de nuevo."))
            }
        }
    }

    /**
     * Valida el formulario y crea un nuevo animal en Firestore.
     *
     * Validaciones:
     * - Nombre, especie y fecha de nacimiento no pueden estar vacíos.
     * - Latitud y longitud deben ser numéricas válidas.
     * - Debe existir una sesión válida (UID no nulo).
     *
     * Flujo de guardado:
     * 1) Marca `isSaving = true`.
     * 2) Obtiene UID desde [AuthRepository].
     * 3) Construye un [Animal] con los datos del formulario.
     * 4) Llama a [AnimalRepository.createAnimal] pasando también la foto (si existe).
     * 5) Emite evento de éxito y resetea el formulario.
     * 6) En caso de error, emite evento de error.
     * 7) Finalmente, desmarca `isSaving`.
     */
    fun save() {
        val s = _uiState.value

        // Validación de campos obligatorios
        if (s.name.isBlank() || s.species.isBlank() || s.dob.isBlank()) {
            emitError("Rellena nombre, especie y nacimiento")
            return
        }

        // Validación de coordenadas
        val lat = s.latitude.toDoubleOrNull()
        val lon = s.longitude.toDoubleOrNull()
        if (lat == null || lon == null) {
            emitError("Latitud/Longitud inválidas")
            return
        }

        viewModelScope.launch {
            try {
                // Bloquea UI / muestra estado de guardado
                _uiState.update { it.copy(isSaving = true) }

                // Obtiene usuario actual
                val uid = authRepository.currentUidFlow().first()
                if (uid.isNullOrBlank()) {
                    emitError("Sesión no válida. Inicia sesión de nuevo.")
                    return@launch
                }

                // Construye el modelo Animal a guardar
                val animal = Animal(
                    uid = "",
                    name = s.name.trim(),
                    species = s.species.trim(),
                    photo = null,
                    dob = s.dob.trim(),
                    latitude = lat,
                    longitude = lon,
                    adoptionState = s.adoptionState,
                    volunteerId = uid
                )

                // Bytes de imagen seleccionada (si existen)
                val bytes = s.photoBytes
                android.util.Log.d("PHOTO", "photoBytes = ${bytes?.size ?: 0} bytes")

                // Crea el animal en el repositorio (incluye foto si existe)
                animalRepository.createAnimal(animal, s.photoBytes)

                // Notifica éxito y resetea el formulario
                _events.emit(UiEvent.Success("Animal creado"))
                _uiState.value = AdminAddAnimalUiState()

            } catch (e: Exception) {
                // Notifica error y deja trazabilidad
                emitError(e.message ?: "No se pudo guardar el animal")
            } finally {
                // Desbloquea UI / termina estado de guardado
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }

    /**
     * Actualiza el estado con la foto seleccionada.
     *
     * Se guardan:
     * - [bytes] para subida a storage o persistencia remota.
     * - [uriString] para previsualización local en UI.
     *
     * @param bytes Imagen en bytes (puede ser null si solo se guarda referencia).
     * @param uriString URI de la imagen seleccionada en String.
     */
    fun onPhotoSelected(bytes: ByteArray?, uriString: String) {
        _uiState.update { it.copy(photoBytes = bytes, photoUri = uriString) }
    }

    /**
     * Emite un evento de error hacia la UI.
     *
     * @param msg Mensaje de error a mostrar.
     */
    private fun emitError(msg: String) {
        viewModelScope.launch { _events.emit(UiEvent.Error(msg)) }
    }
}
