package com.gromber05.peco.data.repository

import com.gromber05.peco.data.local.animal.AnimalDao
import com.gromber05.peco.data.local.animal.AnimalEntity
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class AnimalRepository @Inject constructor(
    private val animalDao: AnimalDao
){
    suspend fun registerAnimal(animalEntity: AnimalEntity) {
        animalDao.insertAnimal(animalEntity)
    }

    suspend fun getAnimalById(id: Int): AnimalEntity? {
        return animalDao.getAnimalById(id)
    }

    fun getAnimals(): Flow<List<AnimalEntity>> = animalDao.getAllAnimals()
}