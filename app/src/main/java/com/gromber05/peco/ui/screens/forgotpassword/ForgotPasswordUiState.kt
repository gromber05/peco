package com.gromber05.peco.ui.screens.forgotpassword

data class ForgotPasswordUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)