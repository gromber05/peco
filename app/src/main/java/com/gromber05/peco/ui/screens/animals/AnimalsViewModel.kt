package com.gromber05.peco.ui.screens.animals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gromber05.peco.data.local.animal.toDomain
import com.gromber05.peco.data.repository.AnimalRepository
import com.gromber05.peco.model.data.Animal
import com.gromber05.peco.ui.screens.detail.DetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


@HiltViewModel
class AnimalsViewModel @Inject constructor(
    private val animalRepository: AnimalRepository
): ViewModel() {
    private val _animal = MutableStateFlow<Animal?>(null)
    val animal: StateFlow<Animal?> = _animal

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState = _uiState.asStateFlow()

    fun loadAnimal(id: Int) {
        viewModelScope.launch {
            _uiState.value = DetailUiState(isLoading = true)

            val animal = animalRepository.getAnimalById(id)?.toDomain()

            _uiState.value = if (animal != null) {
                DetailUiState(isLoading = false, animal = animal)
            } else {
                DetailUiState(isLoading = false, notFound = true)
            }
        }
    }
}