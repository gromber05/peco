package com.gromber05.peco.ui.screens.home

data class HomeUiState(
    val user: String = "",
    val email: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAdmin: Boolean = false
)
