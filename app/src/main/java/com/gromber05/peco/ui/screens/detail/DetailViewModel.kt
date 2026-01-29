package com.gromber05.peco.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gromber05.peco.data.repository.AnimalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val animalRepository: AnimalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState = _uiState.asStateFlow()

    private val animalId: Int? = savedStateHandle["animalId"]

    init {
        val id = animalId
        if (id == null) {
            _uiState.value = _uiState.value.copy(notFound = true)
        } else {
            observeAnimal(id)
        }
    }

    private fun observeAnimal(id: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, notFound = false)

            animalRepository.observeAnimal(id)
                .catch {
                    _uiState.value = DetailUiState(isLoading = false, notFound = true)
                }
                .collect { animal ->
                    if (animal == null) {
                        _uiState.value = DetailUiState(isLoading = false, notFound = true)
                    } else {
                        _uiState.value = DetailUiState(isLoading = false, animal = animal, notFound = false)
                    }
                }
        }
    }
}
