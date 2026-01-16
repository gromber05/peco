package com.gromber05.peco.ui.screens.profile

data class ChangePasswordUiState(
    val current: String = "",
    val newPass: String = "",
    val confirm: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false
)
