package com.gromber05.peco.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gromber05.peco.data.local.animal.toDomain
import com.gromber05.peco.data.local.swipe.SwipeAction
import com.gromber05.peco.data.repository.AnimalRepository
import com.gromber05.peco.data.repository.SwipeRepository
import com.gromber05.peco.data.repository.UserRepository
import com.gromber05.peco.model.data.Animal
import com.gromber05.peco.model.events.UiEvent
import com.gromber05.peco.utils.LocationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val animalRepository: AnimalRepository,
    private val swipeRepository: SwipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<UiEvent>()
    val events = _events.asSharedFlow()

    init {
        observeUser()
        observeAnimalsDeck()
        observeLikedIds()
    }

    fun logout() {
        viewModelScope.launch {
            try {
                userRepository.logout()
                _uiState.update { it.copy(isLogged = false) }
                _events.emit(UiEvent.LoggedOut)
            } catch (_: Exception) {
                _events.emit(UiEvent.Error("No se pudo cerrar sesión"))
            }
        }
    }

    private fun observeUser() {
        viewModelScope.launch {
            userRepository.currentUser.collect { user ->
                if (user != null) {
                    _uiState.update {
                        it.copy(
                            username = user.username,
                            email = user.email,
                            isAdmin = user.isAdmin,
                            isLogged = true
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLogged = false) }
                }
            }
        }
    }

    /** 🔥 Aquí se construye el "deck Tinder" excluyendo los animales ya swipeados */
    private fun observeAnimalsDeck() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            combine(
                animalRepository.getAnimals()
                    .map { list -> list.map { it.toDomain() } }
                    .flowOn(Dispatchers.IO),
                swipeRepository.observeSwipedIds()
            ) { animals: List<Animal>, swipedIds: List<Int> ->
                val swipedSet = swipedIds.toSet()
                val deck = animals.filterNot { it.id in swipedSet }
                animals to deck
            }
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error") }
                    _events.emit(UiEvent.Error("Error cargando animales"))
                }
                .collect { (animals, deck) ->
                    _uiState.update {
                        it.copy(
                            animalList = animals,
                            deck = deck,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    private fun observeLikedIds() {
        viewModelScope.launch {
            swipeRepository.observeLikedIds().collect { liked ->
                _uiState.update { it.copy(likedIds = liked.toSet()) }
            }
        }
    }

    /* -------- Tinder actions -------- */

    fun likeCurrent() {
        val animal = _uiState.value.deck.firstOrNull() ?: return
        onLike(animal)
    }

    fun dislikeCurrent() {
        val animal = _uiState.value.deck.firstOrNull() ?: return
        onDislike(animal)
    }

    fun onLike(animal: Animal) {
        viewModelScope.launch {
            swipeRepository.swipe(animal.id, SwipeAction.LIKE)
            // No hace falta quitarlo manualmente del deck:
            // el combine() reaccionará al nuevo swipe y lo eliminará solo ✅
        }
    }

    fun onDislike(animal: Animal) {
        viewModelScope.launch {
            swipeRepository.swipe(animal.id, SwipeAction.DISLIKE)
        }
    }

    fun resetSwipes() {
        viewModelScope.launch {
            swipeRepository.clearAll()
        }
    }

    /* -------- Orden por proximidad -------- */

    fun sortByProximity(userLat: Double, userLon: Double) {
        val animals = _uiState.value.animalList

        val ordered = animals.sortedBy { animal ->
            LocationUtils.calculateDistance(userLat, userLon, animal.latitude, animal.longitude)
        }

        // Reordenamos animalList. El deck se recalcula solo pero usando el order nuevo:
        val swiped = _uiState.value.animalList.map { it.id }.toSet() // no fiable
        // Mejor: solo actualiza animalList y deja deck al combine -> PERO combine usa flow de animales del repo.
        // Si quieres orden persistente, guárdalo en estado y aplica el orden al deck directamente:
        val likedIds = _uiState.value.likedIds

        _uiState.update {
            val swipedSet = emptySet<Int>() // el deck real lo gestiona combine
            it.copy(
                animalList = ordered,
                deck = ordered.filterNot { a -> a.id in swipedSet }, // opcional
                likedIds = likedIds
            )
        }
    }
}
