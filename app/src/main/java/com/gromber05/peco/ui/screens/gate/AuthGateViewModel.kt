package com.gromber05.peco.ui.screens.gate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gromber05.peco.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AuthGateViewModel @Inject constructor(
    authRepository: AuthRepository
) : ViewModel() {
    val isLogged: StateFlow<Boolean?> =
        authRepository.currentUidFlow()
            .map { uid -> uid != null }
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)
}
