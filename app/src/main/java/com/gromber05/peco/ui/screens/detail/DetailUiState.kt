package com.gromber05.peco.ui.screens.detail

import com.gromber05.peco.model.data.Animal
import com.gromber05.peco.model.user.User

data class DetailUiState(
    val isLoading: Boolean = true,
    val animal: Animal? = null,
    val notFound: Boolean = false,
    val volunteer: User? = null
)