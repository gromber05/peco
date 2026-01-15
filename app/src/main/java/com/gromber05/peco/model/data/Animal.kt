package com.gromber05.peco.model.data

import com.gromber05.peco.model.AdoptionState

data class Animal(
    val id: Int,
    val name: String,
    val species: String,
    val photo: String?,
    val dob: String,
    val location: String,
    val adoptionState: AdoptionState,
)