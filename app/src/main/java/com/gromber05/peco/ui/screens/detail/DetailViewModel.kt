package com.gromber05.peco.ui.screens.detail

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gromber05.peco.data.repository.AnimalRepository
import com.gromber05.peco.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val animalRepository: AnimalRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState = _uiState.asStateFlow()
    private val animalId: String? = savedStateHandle.get<String>("animalId")
    private var volunteerJob: kotlinx.coroutines.Job? = null

    init {
        val id = animalId
        if (id == null) {
            _uiState.value = _uiState.value.copy(notFound = true, isLoading = false)
        } else {
            observeAnimal(id)
        }
    }

    private fun observeVolunteer(volunteerId: String) {
        volunteerJob?.cancel()
        volunteerJob = viewModelScope.launch {
            userRepository.observeProfile(volunteerId)
                .collectLatest { volunteerData ->
                    _uiState.update {
                        it.copy(
                            volunteer = volunteerData,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun observeAnimal(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, notFound = false)

            try {
                val animal = animalRepository.getAnimalById(id)

                if (animal == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        notFound = true,
                        animal = null
                    )
                } else {
                    observeVolunteer(animal.volunteerId)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        notFound = false,
                        animal = animal
                    )
                }
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    notFound = true,
                    animal = null
                )
            }
        }
    }
}
