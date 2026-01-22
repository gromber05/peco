package com.gromber05.peco.ui.screens.animals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gromber05.peco.data.local.animal.toDomain
import com.gromber05.peco.data.repository.AnimalRepository
import com.gromber05.peco.data.repository.SwipeRepository
import com.gromber05.peco.model.data.Animal
import com.gromber05.peco.ui.screens.detail.DetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


@HiltViewModel
class AnimalsViewModel @Inject constructor(
    private val animalRepository: AnimalRepository,
    private val swipeRepository: SwipeRepository
): ViewModel() {
    private val _animal = MutableStateFlow<Animal?>(null)
    val animal: StateFlow<Animal?> = _animal

    private val _uiState = MutableStateFlow(AnimalsUiState())
    val uiState = _uiState.asStateFlow()

    fun loadAnimals() {
        viewModelScope.launch {
            _uiState.value = AnimalsUiState(isLoading = true)

            combine(
                animalRepository.getAnimals(),
                swipeRepository.observeLikedIds()
            ) { allAnimals, likedIds ->
                val likedSet = likedIds.toSet()

                allAnimals
                    .filter { it.id in likedSet }
                    .map { it.toDomain() }
            }.collect { likedAnimals ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    animals = likedAnimals
                )
            }
        }
    }
}