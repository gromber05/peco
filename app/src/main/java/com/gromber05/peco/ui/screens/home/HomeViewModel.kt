package com.gromber05.peco.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gromber05.peco.data.local.animal.toDomain
import com.gromber05.peco.data.repository.AnimalRepository
import com.gromber05.peco.data.repository.UserRepository
import com.gromber05.peco.ui.screens.login.LoginUiState
import com.gromber05.peco.utils.LocationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val animalRepository: AnimalRepository
) : ViewModel()
{
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadAnimals()
        observeUser()
    }

    suspend fun logout() {
        userRepository.logout()
    }

    private fun observeUser() {
        viewModelScope.launch {
            userRepository.currentUser.collect { user ->
                if (user != null) {
                    _uiState.update {
                        it.copy(
                            username = user.username,
                            email = user.email,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    private fun loadAnimals() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            animalRepository.getAnimals()
                .map { lista -> lista.map { it.toDomain() } }
                .flowOn(Dispatchers.IO)
                .catch { _uiState.value.error = it.message }
                .collect { listaYaLista ->
                    _uiState.update { it.copy(animalList = listaYaLista, isLoading = false) }
                }
        }
    }

    fun sortByProximity(userLat: Double, userLon: Double) {
        val currentList = _uiState.value.animalList

        val nuevaListaOrdenada = currentList.sortedBy { animal ->
            LocationUtils.calculateDistance(userLat, userLon, animal.latitude, animal.longitude)
        }

        _uiState.update {
            it.copy(animalList = nuevaListaOrdenada)
        }
    }
}
