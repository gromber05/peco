package com.gromber05.peco.ui.screens.detail

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gromber05.peco.data.repository.AnimalRepository
import com.gromber05.peco.data.repository.UserRepository
import com.gromber05.peco.model.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val animalRepository: AnimalRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState = _uiState.asStateFlow()
    private val animalId: String? = savedStateHandle.get<String>("animalId")

    init {
        val id = animalId
        if (id == null) {
            _uiState.value = _uiState.value.copy(notFound = true, isLoading = false)
        } else {
            observeAnimal(id)
        }
    }

    private fun getVolunteerById(volunteerId: String) {
        viewModelScope.launch {
            val volunteer = userRepository.observeProfile(volunteerId)

            volunteer.collect { volunteerData ->
                if (volunteerData == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            notFound = true
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            notFound = false,
                            volunteer = volunteerData
                        )
                    }
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
                    getVolunteerById(animal.volunteerId)
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

    fun openDialer(context: Context, phone: String) {
        val normalized = phone.trim()
        if (normalized.isBlank()) return

        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$normalized")
        }

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }
}
