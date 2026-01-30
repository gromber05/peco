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

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val usersRepository: UserRepository,
    private val animalRepository: AnimalRepository,
    private val swipeRepository: SwipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<UiEvent>()
    val events = _events.asSharedFlow()

    private var logoutDone = false

    init {
        observeAll()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeAll() {
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

        viewModelScope.launch {
            authRepository.currentUidFlow()
                .distinctUntilChanged()
                .flatMapLatest { uid ->
                    if (uid == null) {
                        flowOf(Triple(emptyList<Animal>(), emptySet<String>(), emptyList<String>()))
                    } else {
                        combine(
                            animalRepository.observeAnimals(),
                            swipeRepository.observeSwipedIds(uid),
                            swipeRepository.observeLikedIds(uid)
                        ) { animals, swipedIdsRaw, likedIdsRaw ->

                            val swipedSet: Set<String> =
                                swipedIdsRaw.map { it.toString() }.toSet()

                            val likedIds: List<String> =
                                likedIdsRaw.map { it.toString() }

                            val deck = animals.filterNot { it.uid in swipedSet }

                            Triple(animals, deck, Pair(swipedSet, likedIds))
                        }

                    }
                }
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error") }
                }
                .collect { (animals, swipedSet, likedIds) ->
                    val deck = animals.filterNot { it.uid in swipedSet }

                    _uiState.update {
                        it.copy(
                            animalList = animals,
                            deck = deck,
                            swipedIds = swipedSet as Set<String>,
                            likedIds = likedIds as List<String>,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    fun likeCurrent() {
        val animal = _uiState.value.deck.firstOrNull() ?: return
        onLike(animal)
    }

    fun dislikeCurrent() {
        val animal = _uiState.value.deck.firstOrNull() ?: return
        onDislike(animal)
    }

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

    fun onLike(animal: Animal) {
        val uid = _uiState.value.userUid ?: return

        advanceDeckLocally(animal.uid)

        viewModelScope.launch {
            try {
                swipeRepository.setSwipe(uid, animal.uid, SwipeAction.LIKE)
            } catch (e: Exception) {
                _events.emit(UiEvent.Error(e.message ?: "Error guardando LIKE"))
            }
        }
    }

    fun onDislike(animal: Animal) {
        val uid = _uiState.value.userUid ?: return

        advanceDeckLocally(animal.uid)

        viewModelScope.launch {
            try {
                swipeRepository.setSwipe(uid, animal.uid, SwipeAction.DISLIKE)
            } catch (e: Exception) {
                _events.emit(UiEvent.Error(e.message ?: "Error guardando DISLIKE"))
            }
        }
    }

    fun resetSwipes() {
        val uid = _uiState.value.userUid ?: return
        viewModelScope.launch {
            try {
                swipeRepository.clearAll(uid)
                // No hace falta tocar deck aquí: el combine recomputará al momento
            } catch (e: Exception) {
                _events.emit(UiEvent.Error(e.message ?: "No se pudo reiniciar"))
            }
        }
    }

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
