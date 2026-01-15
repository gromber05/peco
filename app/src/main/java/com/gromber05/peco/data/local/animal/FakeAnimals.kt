package com.gromber05.peco.data.local.animal

import com.gromber05.peco.model.AdoptionState

fun fakeAnimals(): List<AnimalEntity> = listOf(
    AnimalEntity(
        id = 1,
        name = "Luna",
        species = "Perro",
        photo = null,
        dob = "2022-03-01",
        latitude = 36.5271,
        longitude = -6.2886,
        adoptionState = AdoptionState.AVAILABLE
    ),
    AnimalEntity(
        id = 2,
        name = "Milo",
        species = "Gato",
        photo = null,
        dob = "2021-07-12",
        latitude = 36.6864,
        longitude = -6.1372,
        adoptionState = AdoptionState.AVAILABLE
    ),
    AnimalEntity(
        id = 3,
        name = "Nala",
        species = "Perro",
        photo = null,
        dob = "2020-11-05",
        latitude = 36.4657,
        longitude = -6.1967,
        adoptionState = AdoptionState.AVAILABLE
    ),
    AnimalEntity(
        id = 4,
        name = "Simba",
        species = "Gato",
        photo = null,
        dob = "2019-09-20",
        latitude = 36.5297,
        longitude = -6.2923,
        adoptionState = AdoptionState.AVAILABLE
    )
)
