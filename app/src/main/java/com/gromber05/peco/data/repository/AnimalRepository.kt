package com.gromber05.peco.data.repository

import com.gromber05.peco.data.local.animal.AnimalDao
import com.gromber05.peco.data.local.animal.AnimalEntity
import com.gromber05.peco.data.local.user.UserEntity
import jakarta.inject.Inject

class AnimalRepository @Inject constructor(
    private val animalDao: AnimalDao
){
    suspend fun registerAnimal(animalEntity: AnimalEntity) {
        animalDao.insertAnimal(animalEntity)
    }

    suspend fun getAnimalById(id: Int): AnimalEntity? {
        return animalDao.getAnimalById(id)
    }
}