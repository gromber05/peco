package com.gromber05.peco

import com.gromber05.peco.model.AdoptionState
import com.gromber05.peco.model.data.Animal

fun testAnimal(
    uid: String,
    volunteerId: String,
    adoptionState: AdoptionState = AdoptionState.AVAILABLE,
    species: String = "Perro",
    name: String = "Animal $uid"
) = Animal(
    uid = uid,
    name = name,
    species = species,
    photo = null,
    dob = "2021-01-01",
    latitude = 36.5,
    longitude = -6.3,
    adoptionState = adoptionState,
    volunteerId = volunteerId
)
