package com.gromber05.peco.data.local.animal

import com.gromber05.peco.model.data.Animal

fun AnimalEntity.toDomain(): Animal =
    Animal(
        id = id,
        name = name,
        species = species,
        photo = photo,
        dob = dob,
        location = location,
        adoptionState = adoptionState
    )

fun Animal.toEntity(): AnimalEntity =
    AnimalEntity(
        id = id,
        name = name,
        species = species,
        photo = photo,
        dob = dob,
        location = location,
        adoptionState = adoptionState
    )
