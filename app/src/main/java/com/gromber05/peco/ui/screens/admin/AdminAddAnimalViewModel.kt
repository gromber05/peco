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

@HiltViewModel
class AdminAddAnimalViewModel @Inject constructor(
    private val animalRepository: AnimalRepository,
    private val locationRepository: LocationRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminAddAnimalUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<UiEvent>()
    val events = _events.asSharedFlow()

    fun onNameChange(v: String) = _uiState.update { it.copy(name = v) }
    fun onSpeciesChange(v: String) = _uiState.update { it.copy(species = v) }
    fun onDobChange(v: String) = _uiState.update { it.copy(dob = v) }
    fun onStateChange(v: AdoptionState) = _uiState.update { it.copy(adoptionState = v) }
    fun onPhotoUriChange(uri: String) = _uiState.update { it.copy(photoUri = uri) }

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

    fun save() {
        val s = _uiState.value

        if (s.name.isBlank() || s.species.isBlank() || s.dob.isBlank()) {
            emitError("Rellena nombre, especie y nacimiento")
            return
        }

        val lat = s.latitude.toDoubleOrNull()
        val lon = s.longitude.toDoubleOrNull()
        if (lat == null || lon == null) {
            emitError("Latitud/Longitud inválidas")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isSaving = true) }

                val uid = authRepository.currentUidFlow().first()
                if (uid.isNullOrBlank()) {
                    emitError("Sesión no válida. Inicia sesión de nuevo.")
                    return@launch
                }

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

                animalRepository.createAnimal(animal, s.photoBytes)

                _events.emit(UiEvent.Success("Animal creado"))
                _uiState.value = AdminAddAnimalUiState()

            } catch (e: Exception) {
                emitError(e.message ?: "No se pudo guardar el animal")
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }

    fun onPhotoSelected(bytes: ByteArray, uriString: String) {
        _uiState.update { it.copy(photoBytes = bytes, photoUri = uriString) }
    }

    private fun emitError(msg: String) {
        viewModelScope.launch { _events.emit(UiEvent.Error(msg)) }
    }
}
