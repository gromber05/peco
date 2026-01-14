package com.gromber05.peco.ui.screens.register

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val pass: String = "",
    val confirmPass: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val isRegistered: Boolean = false,
    val error: String? = null
)
