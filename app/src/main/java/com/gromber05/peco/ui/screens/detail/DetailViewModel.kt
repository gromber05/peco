package com.gromber05.peco.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gromber05.peco.data.repository.AnimalRepository
import com.gromber05.peco.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val animalRepository: AnimalRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState = _uiState.asStateFlow()
    private val animalId: String? =
        savedStateHandle.get<String>("animalId")
            ?: savedStateHandle.get<Int>("animalId")?.toString()

    init {
        val id = animalId
        if (id == null) {
            _uiState.value = _uiState.value.copy(notFound = true, isLoading = false)
        } else {
            observeAnimal(id)
        }
    }

    private fun observeAnimal(id: String) {
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

    fun openChat(
        animalId: String,
        myUid: String,
        otherUid: String,
        onReady: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val convId = chatRepository.getOrCreateConversationId(animalId, myUid, otherUid)
                onReady(convId)
            } catch (e: Exception) {
                onError(e.message ?: "Error creando conversación")
            }
        }
    }

}
