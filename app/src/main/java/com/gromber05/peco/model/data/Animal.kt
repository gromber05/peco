package com.gromber05.peco.model.data

import com.gromber05.peco.model.AdoptionState

data class Animal(
    val uid: String,
    val name: String,
    val species: String,
    val photo: String?,
    val dob: String,
    val latitude: Double,
    val longitude: Double,
    val adoptionState: AdoptionState,
    val volunteerId: String
)