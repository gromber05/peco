package com.gromber05.peco.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gromber05.peco.data.repository.AnimalRepository
import com.gromber05.peco.data.repository.AuthRepository
import com.gromber05.peco.data.repository.SwipeRepository
import com.gromber05.peco.data.repository.UserRepository
import com.gromber05.peco.model.SwipeAction
import com.gromber05.peco.model.data.Animal
import com.gromber05.peco.model.events.UiEvent
import com.gromber05.peco.model.user.UserRole
import com.gromber05.peco.utils.LocationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel principal de la pantalla Home.
 *
 * Responsabilidades:
 * - Mantener el estado global de Home ([HomeUiState]) para Compose.
 * - Gestionar el perfil del usuario (username, email, rol, foto) observando Firestore.
 * - Observar el listado de animales y el estado de swipes (swipedIds/likedIds).
 * - Construir el "deck" de tarjetas tipo Tinder filtrando los animales ya swipeados.
 * - Registrar acciones de LIKE / DISLIKE en Firestore mediante [SwipeRepository].
 * - Permitir reiniciar swipes (reset) y ordenar por proximidad (ubicación).
 * - Gestionar logout y emitir eventos de UI ([UiEvent]) para feedback/navegación.
 *
 * Arquitectura:
 * - MVVM con Kotlin Flow/StateFlow.
 * - Se usan corrutinas en [viewModelScope].
 * - Se combinan múltiples flujos con [combine] y se controla el ciclo de vida
 *   del usuario con [flatMapLatest] (cuando cambia uid, se cancelan flujos previos).
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    /** Repositorio de autenticación (estado de sesión, signOut...). */
    private val authRepository: AuthRepository,
    /** Repositorio de usuarios (perfil en tiempo real). */
    private val usersRepository: UserRepository,
    /** Repositorio de animales (observación de listado). */
    private val animalRepository: AnimalRepository,
    /** Repositorio de swipes (registrar y observar likes/dislikes). */
    private val swipeRepository: SwipeRepository
) : ViewModel() {

    /**
     * Estado interno mutable del Home.
     * Se expone como inmutable mediante [uiState] para ser observado por Compose.
     */
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    /**
     * Flujo de eventos one-shot para la UI.
     * Útil para:
     * - Toasts / Snackbars
     * - Navegación (por ejemplo LoggedOut)
     */
    private val _events = MutableSharedFlow<UiEvent>()
    val events = _events.asSharedFlow()

    /**
     * Flag para evitar ejecutar logout más de una vez (doble click o recomposición).
     */
    private var logoutDone = false

    /**
     * Inicialización del ViewModel: arranca todas las observaciones necesarias.
     */
    init {
        observeAll()
    }

    /**
     * Arranca la observación de:
     * 1) UID actual (sesión).
     * 2) Perfil del usuario (username/email/rol/foto).
     * 3) Animales + swipes (swipedIds/likedIds) para construir el deck.
     *
     * Se ejecutan en corrutinas separadas para que cada flujo sea independiente.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeAll() {
        /**
         * Observa el UID del usuario actual.
         * - Actualiza `userUid` e `isLogged` en el estado.
         */
        viewModelScope.launch {
            authRepository.currentUidFlow()
                .distinctUntilChanged()
                .collect { uid ->
                    _uiState.update { s ->
                        s.copy(
                            userUid = uid,
                            isLogged = uid != null
                        )
                    }
                }
        }

        /**
         * Observa el perfil del usuario autenticado.
         *
         * - Si uid es null => emite profile null.
         * - Si uid existe => observa el documento de usuario en tiempo real.
         *
         * Actualiza:
         * - username, email, userRole, photo, isLogged
         */
        viewModelScope.launch {
            authRepository.currentUidFlow()
                .distinctUntilChanged()
                .flatMapLatest { uid ->
                    if (uid == null) flowOf(null)
                    else usersRepository.observeProfile(uid)
                }
                .catch { e -> _uiState.update { it.copy(error = e.message) } }
                .collect { profile ->
                    if (profile == null) {
                        _uiState.update {
                            it.copy(
                                username = "",
                                email = "",
                                userRole = UserRole.USER,
                                photo = null,
                                isLogged = false
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                username = profile.username,
                                email = profile.email,
                                userRole = profile.role,
                                photo = profile.photo,
                                isLogged = true
                            )
                        }
                    }
                }
        }

        /**
         * Observa datos necesarios para el "deck":
         * - Lista de animales (stream).
         * - IDs swipeados por el usuario (stream).
         * - IDs likeados por el usuario (stream).
         *
         * Si uid es null:
         * - Emite valores vacíos para evitar nulls y mantener estado consistente.
         *
         * Con los datos:
         * - Construye `deck` filtrando los animales ya swipeados.
         * - Actualiza `animalList`, `deck`, `swipedIds`, `likedIds`, `isLoading`, `error`.
         */
        viewModelScope.launch {
            authRepository.currentUidFlow()
                .distinctUntilChanged()
                .flatMapLatest { uid ->

                    if (uid == null) {
                        flowOf(
                            Triple(
                                emptyList(),
                                emptySet(),
                                emptySet()
                            )
                        )
                    } else {
                        combine(
                            animalRepository.observeAnimals(),
                            swipeRepository.observeSwipedIds(uid),
                            swipeRepository.observeLikedIds(uid)
                        ) { animals: List<Animal>, swipedSet: Set<String>, likedSet: Set<String> ->
                            Triple(animals, swipedSet, likedSet)
                        }
                    }
                }
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error") }
                }
                .collect { (animals, swipedSet, likedSet) ->

                    // Deck = animales que aún no han sido swipeados por el usuario
                    val deck = animals.filterNot { it.uid in swipedSet }

                    _uiState.update {
                        it.copy(
                            animalList = animals,
                            deck = deck,
                            swipedIds = swipedSet,
                            likedIds = likedSet,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }

    }

    /**
     * Ejecuta un LIKE sobre el animal actual del deck (primer elemento).
     * Si el deck está vacío, no hace nada.
     */
    fun likeCurrent() {
        val animal = _uiState.value.deck.firstOrNull() ?: return
        onLike(animal)
    }

    /**
     * Ejecuta un DISLIKE sobre el animal actual del deck (primer elemento).
     * Si el deck está vacío, no hace nada.
     */
    fun dislikeCurrent() {
        val animal = _uiState.value.deck.firstOrNull() ?: return
        onDislike(animal)
    }

    /**
     * Avanza el deck de forma local inmediatamente tras un swipe.
     *
     * Objetivo:
     * - Mejorar UX: la UI responde instantáneamente sin esperar a Firestore.
     *
     * Implementación:
     * - Añade el ID del animal a `swipedIds`.
     * - Elimina la primera tarjeta del deck (`drop(1)`).
     *
     * @param swipedAnimalId ID del animal swipeado.
     */
    private fun advanceDeckLocally(swipedAnimalId: String) {
        _uiState.update { s ->
            val newSwiped = s.swipedIds + swipedAnimalId
            val newDeck = s.deck.drop(1)

            s.copy(
                swipedIds = newSwiped,
                deck = newDeck
            )
        }
    }

    /**
     * Guarda en el estado una foto seleccionada (bytes + uri).
     *
     * Uso típico:
     * - Previsualización y subida posterior del avatar del usuario.
     *
     * @param bytes Imagen seleccionada en bytes.
     * @param uriString URI local de la imagen seleccionada.
     */
    fun onPhotoSelected(bytes: ByteArray, uriString: String) {
        _uiState.update { it.copy(photoBytes = bytes, photoUri = uriString) }
    }

    /**
     * Registra un LIKE para el usuario actual sobre un [Animal].
     *
     * Flujo:
     * 1) Verifica que exista uid.
     * 2) Avanza el deck localmente para respuesta inmediata.
     * 3) Guarda el swipe en Firestore mediante [SwipeRepository].
     * 4) Si falla, emite evento de error para la UI.
     *
     * @param animal Animal al que se aplica LIKE.
     */
    fun onLike(animal: Animal) {
        val uid = _uiState.value.userUid ?: return

        advanceDeckLocally(animal.uid)

        viewModelScope.launch {
            try {
                swipeRepository.setSwipe(uid, animal, SwipeAction.LIKE)
            } catch (e: Exception) {
                _events.emit(UiEvent.Error(e.message ?: "Error guardando LIKE"))
            }
        }
    }

    /**
     * Registra un DISLIKE para el usuario actual sobre un [Animal].
     *
     * Flujo similar a [onLike], pero con acción DISLIKE.
     *
     * @param animal Animal al que se aplica DISLIKE.
     */
    fun onDislike(animal: Animal) {
        val uid = _uiState.value.userUid ?: return

        advanceDeckLocally(animal.uid)

        viewModelScope.launch {
            try {
                swipeRepository.setSwipe(uid, animal, SwipeAction.DISLIKE)
            } catch (e: Exception) {
                _events.emit(UiEvent.Error(e.message ?: "Error guardando DISLIKE"))
            }
        }
    }

    /**
     * Reinicia el historial de swipes del usuario actual.
     *
     * Implementación:
     * - Elimina todos los documentos `users/{uid}/swipes` mediante [SwipeRepository.clearAll].
     * - Si falla, emite evento de error.
     */
    fun resetSwipes() {
        val uid = _uiState.value.userUid ?: return
        viewModelScope.launch {
            try {
                swipeRepository.clearAll(uid)
            } catch (e: Exception) {
                _events.emit(UiEvent.Error(e.message ?: "No se pudo reiniciar"))
            }
        }
    }

    /**
     * Ordena los animales por proximidad a la ubicación del usuario.
     *
     * Uso:
     * - Se llama desde la UI cuando se obtiene la localización (HomeScreen).
     *
     * Implementación:
     * - Ordena `animalList` por distancia usando [LocationUtils.calculateDistance].
     * - Reconstruye `deck` excluyendo animales ya swipeados.
     *
     * @param userLat Latitud del usuario.
     * @param userLon Longitud del usuario.
     */
    fun sortByProximity(userLat: Double, userLon: Double) {
        val ordered = _uiState.value.animalList.sortedBy { animal ->
            LocationUtils.calculateDistance(userLat, userLon, animal.latitude, animal.longitude)
        }

        val swipedSet = _uiState.value.swipedIds

        _uiState.update {
            it.copy(
                animalList = ordered,
                deck = ordered.filterNot { a -> a.uid in swipedSet }
            )
        }
    }

    /**
     * Cierra sesión del usuario actual.
     *
     * Prevención de doble ejecución:
     * - Usa [logoutDone] para evitar múltiples llamadas concurrentes.
     *
     * Flujo:
     * 1) Llama a [AuthRepository.signOut].
     * 2) Emite [UiEvent.LoggedOut] para que la UI navegue al login.
     * 3) Si falla, restablece `logoutDone=false` y emite error.
     */
    fun logout() {
        if (logoutDone) return
        logoutDone = true

        viewModelScope.launch {
            try {
                authRepository.signOut()
                _events.emit(UiEvent.LoggedOut)
            } catch (_: Exception) {
                logoutDone = false
                _events.emit(UiEvent.Error("No se pudo cerrar sesión"))
            }
        }
    }
}
