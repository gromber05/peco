package com.gromber05.peco.ui.screens.animals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gromber05.peco.data.repository.AnimalRepository
import com.gromber05.peco.data.repository.AuthRepository
import com.gromber05.peco.data.repository.SwipeRepository
import com.gromber05.peco.data.repository.UserRepository
import com.gromber05.peco.model.data.Animal
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Optional.empty

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AnimalsViewModel @Inject constructor(
    private val animalRepository: AnimalRepository,
    private val swipeRepository: SwipeRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnimalsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadScreen()
    }

    private fun loadScreen() {
        val filterflow = uiState
            .map { it.filter }
            .distinctUntilChanged()


        viewModelScope.launch {
            authRepository.currentUidFlow()
                .filterNotNull()
                .distinctUntilChanged()
                .flatMapLatest { uid ->
                    combine(
                        animalRepository.observeAnimals().onStart { emit(emptyList()) },
                        swipeRepository.observeLikedIds(uid).onStart { emit(emptySet()) },
                        filterflow
                    ) { animals, likedIds, filter ->
                        if (filter) {
                            animals.filter { it.volunteerId == uid }
                        } else {
                            animals.filter { it.uid in likedIds }
                        }
                    }
                }
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error") }
                }
                .collect { favorites ->
                    _uiState.update { it.copy(isLoading = false, animals = favorites, error = null) }
                }
        }
    }

    fun deleteAnimal(animalUid: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                animalRepository.deleteAnimal(animalUid)

                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error al eliminar el animal"
                    )
                }
            }
        }
    }


    fun setFilter(filter: Boolean) {
        _uiState.update {
            it.copy(
                filter = filter
            )
        }
    }
}
