package com.gromber05.peco.ui.screens.animals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gromber05.peco.data.repository.AnimalRepository
import com.gromber05.peco.data.repository.AuthRepository
import com.gromber05.peco.data.repository.SwipeRepository
import com.gromber05.peco.data.repository.UserRepository
import com.gromber05.peco.model.data.Animal
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Optional.empty

/**
 * ViewModel de la pantalla de listado de animales.
 *
 * Responsabilidades:
 * - Mantener el estado de UI ([AnimalsUiState]) consumido por Compose.
 * - Obtener el UID del usuario autenticado y reaccionar a cambios de sesión.
 * - Combinar en tiempo real:
 *   - Lista de animales desde [AnimalRepository].
 *   - IDs likeados desde [SwipeRepository].
 *   - Un filtro interno de UI (campo `filter` dentro del estado).
 * - Construir el listado final en función del filtro:
 *   - Si `filter == true`: muestra "mis animales" (volunteerId == uid).
 *   - Si `filter == false`: muestra "favoritos" (animal.uid in likedIds).
 * - Permitir eliminar un animal desde UI.
 * - Permitir obtener la lista de animales una vez (para informes PDF).
 *
 * Implementación:
 * - Se usa `flatMapLatest` para cancelar automáticamente colecciones anteriores
 *   cuando cambia el UID (usuario cambia o se vuelve a loguear).
 * - Se usa `combine` para unir múltiples fuentes de datos (animales + likes + filtro).
 * - Se usa [distinctUntilChanged] para evitar recomposiciones y emisiones redundantes.
 *
 * Nota:
 * - La lógica actual interpreta `filter` como un booleano que alterna entre
 *   "mis animales" y "favoritos".
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AnimalsViewModel @Inject constructor(
    /** Repositorio de animales (lectura/observación y acciones CRUD). */
    private val animalRepository: AnimalRepository,
    /** Repositorio de swipes para obtener favoritos (likes) en tiempo real. */
    private val swipeRepository: SwipeRepository,
    /** Repositorio de autenticación para obtener el UID actual. */
    private val authRepository: AuthRepository,
) : ViewModel() {

    /**
     * Estado interno mutable de la pantalla.
     * Se expone como inmutable mediante [uiState] para la UI.
     */
    private val _uiState = MutableStateFlow(AnimalsUiState())
    val uiState = _uiState.asStateFlow()

    /**
     * Inicializa la carga/observación al crear el ViewModel.
     */
    init {
        loadScreen()
    }

    /**
     * Arranca la observación reactiva del contenido de la pantalla.
     *
     * Crea un flujo [filterflow] a partir del campo `filter` del estado:
     * - Emitirá solo cuando cambie el valor (distinctUntilChanged).
     *
     * Luego:
     * - Se obtiene el UID actual desde [AuthRepository].
     * - Por cada UID, se combinan:
     *   - Animales (stream)
     *   - Likes (stream)
     *   - Filtro (stream)
     *
     * Y se construye el listado final:
     * - Filtro activo: animales del voluntario actual.
     * - Filtro inactivo: animales favoritos (likeados).
     *
     * Manejo de errores:
     * - En caso de excepción, se actualiza el estado con `error` y `isLoading=false`.
     */
    private fun loadScreen() {
        val filterflow = uiState
            .map { it.filter }
            .distinctUntilChanged()


        viewModelScope.launch {
            authRepository.currentUidFlow()
                .filterNotNull()
                .distinctUntilChanged()
                .flatMapLatest { uid ->
                    combine(
                        animalRepository.observeAnimals().onStart { emit(emptyList()) },
                        swipeRepository.observeLikedIds(uid).onStart { emit(emptySet()) },
                        filterflow
                    ) { animals, likedIds, filter ->
                        if (filter) {
                            // Modo "mis animales": filtrado por volunteerId
                            animals.filter { it.volunteerId == uid }
                        } else {
                            // Modo "favoritos": filtrado por IDs likeados
                            animals.filter { it.uid in likedIds }
                        }
                    }
                }
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error") }
                }
                .collect { favorites ->
                    // Publica el resultado final en el estado (carga terminada)
                    _uiState.update { it.copy(isLoading = false, animals = favorites, error = null) }
                }
        }
    }

    /**
     * Elimina un animal por su UID.
     *
     * Flujo:
     * 1) Marca `isLoading=true` para reflejar operación en curso.
     * 2) Llama a [AnimalRepository.deleteAnimal].
     * 3) Si todo va bien, desmarca loading.
     * 4) Si falla, guarda un mensaje en `error`.
     *
     * @param animalUid ID del animal a eliminar.
     */
    fun deleteAnimal(animalUid: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                animalRepository.deleteAnimal(animalUid)

                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error al eliminar el animal"
                    )
                }
            }
        }
    }

    /**
     * Obtiene todos los animales una sola vez.
     *
     * Uso típico:
     * - Generación de informes (PDF) donde se necesita una lista puntual.
     *
     * Nota:
     * - Actualmente usa `observeAnimals().first()`, es decir, toma el primer valor emitido
     *   por el flujo de observación.
     *
     * @return Lista de [Animal] obtenida en la primera emisión del Flow.
     */
    suspend fun getAllAnimalsOnce(): List<Animal> {
        return animalRepository.observeAnimals().first()
    }

    /**
     * Establece el modo de filtro del listado.
     *
     * Interpretación actual:
     * - `true`  => mostrar "mis animales" (volunteerId == uid)
     * - `false` => mostrar "favoritos" (animal.uid in likedIds)
     *
     * @param filter Nuevo valor del filtro.
     */
    fun setFilter(filter: Boolean) {
        _uiState.update {
            it.copy(
                filter = filter
            )
        }
    }
}
