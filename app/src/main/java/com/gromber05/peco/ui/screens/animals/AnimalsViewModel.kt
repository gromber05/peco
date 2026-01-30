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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@HiltViewModel
class AnimalsViewModel @Inject constructor(
    private val animalRepository: AnimalRepository,
    private val swipeRepository: SwipeRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnimalsUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    private val userUid = authRepository.currentUidFlow()

    fun start() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            combine(
                animalRepository.observeAnimals(),
                swipeRepository.observeLikedAnimalIds(userUid.toString())
            ) { allAnimals, likedIds ->
                val likedSet = likedIds.toSet()

                allAnimals.filter { it.uid in likedSet }
            }
                .catch { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                }
                .collect { likedAnimals ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        animals = likedAnimals
                    )
                }
        }
    }
}
