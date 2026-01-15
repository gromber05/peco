package com.gromber05.peco.ui.screens.home

import androidx.lifecycle.ViewModel
import com.gromber05.peco.data.repository.AnimalRepository
import com.gromber05.peco.data.repository.UserRepository
import com.gromber05.peco.ui.screens.login.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val animalRepository: AnimalRepository
) : ViewModel()
{
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
}
