package com.gromber05.peco.ui.screens.login

data class LoginUiState(
    val user: String = "",
    val email: String = "",
    val pass: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isPasswordVisible: Boolean = false,
    val isLoggedIn: Boolean = false,
    val isAdmin: Boolean = false
)