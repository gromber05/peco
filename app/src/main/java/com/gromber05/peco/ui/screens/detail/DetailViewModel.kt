package com.gromber05.peco.ui.screens.detail

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gromber05.peco.data.repository.AnimalRepository
import com.gromber05.peco.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel de la pantalla de detalle de un animal.
 *
 * Responsabilidades:
 * - Obtener el `animalId` desde [SavedStateHandle] (parámetro de navegación).
 * - Cargar el animal por ID a través de [AnimalRepository].
 * - Observar el perfil del voluntario asociado a través de [UserRepository].
 * - Publicar un estado de UI reactivo mediante [DetailUiState].
 *
 * Flujo principal:
 * 1) En `init`, intenta recuperar `animalId`.
 * 2) Si no existe, marca `notFound=true`.
 * 3) Si existe, llama a [observeAnimal] para cargar el animal.
 * 4) Cuando hay animal, arranca [observeVolunteer] con `animal.volunteerId`.
 *
 * Concurrencia:
 * - Se mantiene una referencia a [volunteerJob] para cancelar la observación del voluntario
 *   cuando cambia el voluntario o se vuelve a cargar el animal.
 *
 * @param savedStateHandle Permite recuperar argumentos de navegación (ej. "animalId").
 * @param animalRepository Repositorio de acceso a animales.
 * @param userRepository Repositorio de acceso a perfiles de usuario.
 */
@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val animalRepository: AnimalRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    /**
     * Estado interno mutable del detalle.
     * Se expone como inmutable mediante [uiState] para ser consumido por Compose.
     */
    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState = _uiState.asStateFlow()

    /**
     * ID del animal obtenido desde argumentos de navegación.
     * Clave esperada: "animalId".
     */
    private val animalId: String? = savedStateHandle.get<String>("animalId")

    /**
     * Job de la corrutina que observa el voluntario.
     * Se cancela antes de lanzar una nueva observación para evitar múltiples colecciones activas.
     */
    private var volunteerJob: kotlinx.coroutines.Job? = null

    /**
     * Inicialización del ViewModel:
     * - Si no se encuentra el animalId, se marca el estado como notFound.
     * - Si existe, se inicia la carga/observación del animal.
     */
    init {
        val id = animalId
        if (id == null) {
            _uiState.value = _uiState.value.copy(notFound = true, isLoading = false)
        } else {
            observeAnimal(id)
        }
    }

    /**
     * Observa en tiempo real el perfil del voluntario asociado al animal.
     *
     * Implementación:
     * - Cancela cualquier observación previa mediante [volunteerJob].
     * - Se suscribe a `userRepository.observeProfile(volunteerId)`.
     * - Actualiza el estado con los datos del voluntario cuando llegan cambios.
     *
     * @param volunteerId UID del voluntario a observar.
     */
    private fun observeVolunteer(volunteerId: String) {
        volunteerJob?.cancel()
        volunteerJob = viewModelScope.launch {
            userRepository.observeProfile(volunteerId)
                .collectLatest { volunteerData ->
                    _uiState.update {
                        it.copy(
                            volunteer = volunteerData,
                            isLoading = false
                        )
                    }
                }
        }
    }

    /**
     * Carga el animal por ID y actualiza el estado de UI.
     *
     * Flujo:
     * 1) Marca `isLoading=true` y `notFound=false`.
     * 2) Llama a `animalRepository.getAnimalById(id)`.
     * 3) Si devuelve null => marca notFound y limpia animal.
     * 4) Si devuelve un animal:
     *    - Inicia observación del voluntario con [observeVolunteer].
     *    - Guarda el animal en el estado.
     *
     * Manejo de errores:
     * - Ante cualquier excepción, se considera como no encontrado para simplificar
     *   el flujo de UI (notFound=true).
     *
     * @param id ID del animal a cargar.
     */
    fun observeAnimal(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, notFound = false)

            try {
                val animal = animalRepository.getAnimalById(id)

                if (animal == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        notFound = true,
                        animal = null
                    )
                } else {
                    // Inicia observación del voluntario asignado (siempre que exista volunteerId)
                    observeVolunteer(animal.volunteerId)

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        notFound = false,
                        animal = animal
                    )
                }
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    notFound = true,
                    animal = null
                )
            }
        }
    }
}
