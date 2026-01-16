package com.gromber05.peco.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gromber05.peco.data.local.animal.AnimalEntity
import com.gromber05.peco.data.repository.AnimalRepository
import com.gromber05.peco.model.AdoptionState
import com.gromber05.peco.model.events.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminAddAnimalUiState(
    val name: String = "",
    val species: String = "",
    val dob: String = "",
    val photo: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val adoptionState: AdoptionState = AdoptionState.AVAILABLE,
    val isSaving: Boolean = false
)

@HiltViewModel
class AdminAddAnimalViewModel @Inject constructor(
    private val animalRepository: AnimalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminAddAnimalUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<UiEvent>()
    val events = _events.asSharedFlow()

    fun onNameChange(v: String) = _uiState.update { it.copy(name = v) }
    fun onSpeciesChange(v: String) = _uiState.update { it.copy(species = v) }
    fun onDobChange(v: String) = _uiState.update { it.copy(dob = v) }
    fun onPhotoChange(v: String) = _uiState.update { it.copy(photo = v) }
    fun onLatChange(v: String) = _uiState.update { it.copy(latitude = v) }
    fun onLonChange(v: String) = _uiState.update { it.copy(longitude = v) }
    fun onStateChange(v: AdoptionState) = _uiState.update { it.copy(adoptionState = v) }

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

                val entity = AnimalEntity(
                    id = (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
                    name = s.name.trim(),
                    species = s.species.trim(),
                    photo = s.photo.trim().ifBlank { null },
                    dob = s.dob.trim(),
                    latitude = lat,
                    longitude = lon,
                    adoptionState = s.adoptionState
                )

                animalRepository.registerAnimal(entity)

                _events.emit(UiEvent.Success("Animal creado"))
                _uiState.update { AdminAddAnimalUiState() }
            } catch (e: Exception) {
                emitError("No se pudo guardar el animal")
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }

    private fun emitError(msg: String) {
        viewModelScope.launch { _events.emit(UiEvent.Error(msg)) }
    }
}
