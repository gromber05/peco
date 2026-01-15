package com.gromber05.peco.data.local.animal

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gromber05.peco.model.AdoptionState

@Entity(tableName = "animals")
data class AnimalEntity (
    @PrimaryKey val id: Int = 0,
    val name: String,
    val species: String,
    val photo: String?,
    val dob: String,
    val location: String,
    val adoptionState: AdoptionState,

)