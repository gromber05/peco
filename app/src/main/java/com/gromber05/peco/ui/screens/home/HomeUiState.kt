package com.gromber05.peco.ui.screens.home

import com.gromber05.peco.model.data.Animal

data class HomeUiState(
    val username: String = "",
    val email: String = "",
    val isLoading: Boolean = false,
    var error: String? = null,
    val isAdmin: Boolean = false,
    val isLogged: Boolean = false,
    val animalList: List<Animal> = emptyList(),
    val deck: List<Animal> = emptyList(),
    val likedIds: Set<Int> = emptySet()
)
