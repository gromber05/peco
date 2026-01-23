package com.gromber05.peco.data.local.animal

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gromber05.peco.model.AdoptionState

@Entity(tableName = "animals")
data class AnimalEntity (
    @PrimaryKey val id: Int = 0,
    val name: String,
    val species: String,
    val photo: String? = null,
    val dob: String,
    val latitude: Double,
    val longitude: Double,
    val adoptionState: AdoptionState,
    val volunteerId: Int
)