package com.gromber05.peco.ui.screens.home

import com.gromber05.peco.model.data.Animal
import com.gromber05.peco.model.user.UserRole

data class HomeUiState(
    val isLoading: Boolean = true,
    val isLogged: Boolean = false,
    val userUid: String? = null,
    val username: String = "",
    val email: String = "",
    val userRole: UserRole = UserRole.USER,
    val photo: String? = null,
    val photoBytes: ByteArray? = null,
    val photoUri: String? = null,

    val animalList: List<Animal> = emptyList(),
    val deck: List<Animal> = emptyList(),

    val swipedIds: Set<String> = emptySet(),
    val likedIds: Set<String> = emptySet(),

    val error: String? = null
) {
    val visibleAnimals: List<Animal>
        get() = animalList.filterNot { it.uid in swipedIds }
}

