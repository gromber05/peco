package com.gromber05.peco.ui.screens.admin

import com.gromber05.peco.model.AdoptionState

data class AdminAddAnimalUiState(
    val name: String = "",
    val species: String = "",
    val dob: String = "",
    val photo: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val photoBytes: ByteArray? = null,
    val photoUri: String? = null,
    val adoptionState: AdoptionState = AdoptionState.AVAILABLE,
    val isSaving: Boolean = false
)