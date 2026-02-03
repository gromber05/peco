package com.gromber05.peco.ui.screens.gate

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun AuthGate(
    onGoHome: () -> Unit,
    onGoLogin: () -> Unit,
    viewModel: AuthGateViewModel = hiltViewModel()
) {
    val isLogged by viewModel.isLogged.collectAsState()

    LaunchedEffect(isLogged) {
        when (isLogged) {
            true -> onGoHome()
            false -> onGoLogin()
            null -> Unit
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
