package com.gromber05.peco.data.local.animal

import com.gromber05.peco.model.data.Animal
import kotlin.Double

fun AnimalEntity.toDomain(): Animal =
    Animal(
        id = id,
        name = name,
        species = species,
        photo = photo,
        dob = dob,
        latitude = latitude,
        longitude = longitude,
        adoptionState = adoptionState
    )

fun Animal.toEntity(): AnimalEntity =
    AnimalEntity(
        id = id,
        name = name,
        species = species,
        photo = photo,
        dob = dob,
        latitude = latitude,
        longitude = longitude,
        adoptionState = adoptionState
    )
