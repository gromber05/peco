package com.gromber05.peco.ui.screens.profile

data class EditProfileUiState(
    val username: String = "",
    val photo: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false
)
