package com.gromber05.peco.ui.screens.animals

import com.gromber05.peco.model.data.Animal

data class AnimalsUiState(
    val isLoading: Boolean = true,
    val notFound: Boolean = false,
    val error: String? = null,
    val animals: List<Animal> = emptyList(),
    val filter: Boolean = false
)