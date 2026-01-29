package com.gromber05.peco.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gromber05.peco.data.repository.AnimalRepository
import com.gromber05.peco.data.repository.AuthRepository
import com.gromber05.peco.data.repository.SwipeRepository
import com.gromber05.peco.data.repository.UserRepository
import com.gromber05.peco.data.repository.UsersRepository
import com.gromber05.peco.model.SwipeAction
import com.gromber05.peco.model.data.Animal
import com.gromber05.peco.model.events.UiEvent
import com.gromber05.peco.utils.LocationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
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

    init {
        observeUserAndData()
    }

    private fun observeUserAndData() {
        viewModelScope.launch {
            authRepository.currentUidFlow()
                .collect { uid ->
                    if (uid == null) {
                        _uiState.update { it.copy(isLogged = false) }
                    } else {
                        _uiState.update { it.copy(isLogged = true) }
                    }
                }
        }

        viewModelScope.launch {
            // Perfil usuario (Firestore)
            authRepository.currentUidFlow()
                .filterNotNull()
                .collect { uid ->
                    usersRepository.observeProfile(uid)
                        .catch { e ->
                            _uiState.update { it.copy(error = e.message) }
                        }
                        .collect { profile ->
                            _uiState.update {
                                it.copy(
                                    userUid = profile.uid,
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
                .filterNotNull()
                .collect { uid ->
                    combine(
                        animalRepository.observeAnimals(),
                        swipeRepository.observeSwipedIds(uid),
                        swipeRepository.observeLikedIds(uid)
                    ) { animals, swipedIds, likedIds ->
                        val deck = animals.filterNot { it.id in swipedIds }
                        Triple(animals, deck, likedIds)
                    }
                        .catch { e ->
                            _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error") }
                            _events.emit(UiEvent.Error("Error cargando datos"))
                        }
                        .collect { (animals, deck, likedIds) ->
                            _uiState.update {
                                it.copy(
                                    animalList = animals,
                                    deck = deck,
                                    likedIds = likedIds,
                                    isLoading = false,
                                    error = null
                                )
                            }
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

    fun onLike(animal: Animal) {
        val uid = _uiState.value.userUid ?: return
        viewModelScope.launch {
            swipeRepository.setSwipe(uid, animal.uid, SwipeAction.LIKE)
        }
    }

    fun onDislike(animal: Animal) {
        val uid = _uiState.value.userUid ?: return
        viewModelScope.launch {
            swipeRepository.setSwipe(uid, animal.uid, SwipeAction.DISLIKE)
        }
    }

    fun resetSwipes() {
        val uid = _uiState.value.userUid ?: return
        viewModelScope.launch {
            swipeRepository.clearAll(uid)
        }
    }

    fun sortByProximity(userLat: Double, userLon: Double) {
        val animals = _uiState.value.animalList

        val ordered = animals.sortedBy { animal ->
            LocationUtils.calculateDistance(userLat, userLon, animal.latitude, animal.longitude)
        }

        // Deck = ordered - swiped
        val swipedSet = _uiState.value.animalList
            .map { it.id }
            .toSet() // (si quieres usar swipes reales, mejor guarda swipedIds en state)
        val likedIds = _uiState.value.likedIds

        _uiState.update {
            it.copy(
                animalList = ordered,
                deck = ordered.filterNot { a -> a.id in swipedSet },
                likedIds = likedIds
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                authRepository.signOut()
                _uiState.update { it.copy(isLogged = false, userUid = null) }
                _events.emit(UiEvent.LoggedOut)
            } catch (_: Exception) {
                _events.emit(UiEvent.Error("No se pudo cerrar sesión"))
            }
        }
    }
}
