package com.gromber05.peco.ui.screens.detail

import com.gromber05.peco.model.data.Animal

data class DetailUiState(
    val isLoading: Boolean = true,
    val animal: Animal? = null,
    val notFound: Boolean = false
)