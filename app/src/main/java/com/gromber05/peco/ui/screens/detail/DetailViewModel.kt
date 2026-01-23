package com.gromber05.peco.ui.screens.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gromber05.peco.data.local.animal.toDomain
import com.gromber05.peco.data.repository.AnimalRepository
import com.gromber05.peco.model.data.Animal
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val animalRepository: AnimalRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState = _uiState.asStateFlow()

    private val animalId: Int? = savedStateHandle["animalId"]

    init {
        animalId?.let { id ->
            loadAnimal(id)
        } ?: run {
            _uiState.value = _uiState.value.copy(notFound = true)
        }
    }

    fun loadAnimal(id: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, notFound = false)

            try {
                val animalEntity = animalRepository.getAnimalById(id)

                if (animalEntity != null) {
                    val animalDomain = animalEntity.toDomain()
                    _uiState.value = DetailUiState(
                        isLoading = false,
                        animal = animalDomain,
                        notFound = false
                    )
                } else {
                    _uiState.value = DetailUiState(
                        isLoading = false,
                        notFound = true
                    )
                }
            } catch (e: Exception) {
                Log.e("DetailViewModel", "Error cargando animal", e)
                _uiState.value = DetailUiState(
                    isLoading = false,
                    notFound = true
                )
            }
        }
    }
}