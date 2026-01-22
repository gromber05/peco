package com.gromber05.peco.ui.screens.home

import com.gromber05.peco.model.data.Animal
import com.gromber05.peco.model.user.UserRole

data class HomeUiState(
    val userId: Int? = null,
    val username: String = "",
    val email: String = "",
    val photo: String? = null,
    val isLoading: Boolean = false,
    var error: String? = null,
    val userRole: UserRole = UserRole.USER,
    val isAdmin: Boolean = userRole.equals(UserRole.ADMIN),
    val isVolunteer: Boolean = userRole.equals(UserRole.VOLUNTEER),
    val isLogged: Boolean = false,
    val animalList: List<Animal> = emptyList(),
    val deck: List<Animal> = emptyList(),
    val likedIds: Set<Int> = emptySet()
)
