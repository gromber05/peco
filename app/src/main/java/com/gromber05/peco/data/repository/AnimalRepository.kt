package com.gromber05.peco.data.repository

import com.gromber05.peco.data.remote.AnimalsFirestoreDataSource
import com.gromber05.peco.model.data.Animal
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnimalRepository @Inject constructor(private val db: AnimalsFirestoreDataSource) {
    fun observeAnimals(): Flow<List<Animal>> = db.observeAnimals()
    fun saveAnimal(animal: Animal) = db.saveAnimal(animal)
    suspend fun deleteAnimal(animalId: String) = db.deleteAnimal(animalId)
    suspend fun getAnimalById(animalId: String) = db.getAnimalById(animalId)
}