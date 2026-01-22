package com.gromber05.peco.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gromber05.peco.data.local.animal.toDomain
import com.gromber05.peco.model.SwipeAction
import com.gromber05.peco.data.local.user.toUser
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
        restoreUserFromSessionIfNeeded()
        observeUser()
        observeAnimalsDeck()
        observeLikedIds()
    }

    private fun observeUser() {
        viewModelScope.launch {
            userRepository.currentUser.collect { user ->
                if (user != null) {
                    _uiState.update {
                        it.copy(
                            userId = user.id,
                            username = user.username,
                            email = user.email,
                            userRole = user.role,
                            photo = user.photo,
                            isLogged = true
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLogged = false) }
                }
            }
        }
    }

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

    fun sortByProximity(userLat: Double, userLon: Double) {
        val animals = _uiState.value.animalList

        val ordered = animals.sortedBy { animal ->
            LocationUtils.calculateDistance(userLat, userLon, animal.latitude, animal.longitude)
        }

        val swiped = _uiState.value.animalList.map { it.id }.toSet()
        val likedIds = _uiState.value.likedIds

        _uiState.update {
            val swipedSet = emptySet<Int>()
            it.copy(
                animalList = ordered,
                deck = ordered.filterNot { a -> a.id in swipedSet },
                likedIds = likedIds
            )
        }
    }

    private fun restoreUserFromSessionIfNeeded() {
        viewModelScope.launch {
            userRepository.sessionEmail.collect { email ->
                if (!email.isNullOrBlank()) {
                    val current = userRepository.currentUser.value
                    if (current == null) {
                        val entity = userRepository.getUserByEmail(email)
                        if (entity != null) {
                            userRepository.setCurrentUser(entity.toUser())
                        }
                    }
                }
            }
        }
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
}
