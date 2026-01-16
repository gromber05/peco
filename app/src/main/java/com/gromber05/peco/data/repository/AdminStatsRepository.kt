package com.gromber05.peco.data.repository

import com.gromber05.peco.data.local.animal.AnimalDao
import com.gromber05.peco.data.local.swipe.SwipeDao
import com.gromber05.peco.model.data.LabelCount
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class AdminStatsRepository @Inject constructor(
    private val animalDao: AnimalDao,
    private val swipeDao: SwipeDao
) {
    fun totalAnimals(): Flow<Int> = animalDao.countAnimals()

    fun availableAnimals(): Flow<Int> = animalDao.countAnimalsByState("AVAILABLE")
    fun adoptedAnimals(): Flow<Int> = animalDao.countAnimalsByState("ADOPTED")
    fun pendingAnimals(): Flow<Int> = animalDao.countAnimalsByState("PENDING")

    fun likes(): Flow<Int> = swipeDao.countLikes()
    fun dislikes(): Flow<Int> = swipeDao.countDislikes()

    fun animalsBySpecies(): Flow<List<LabelCount>> = animalDao.countBySpecies()
    fun topLikedSpecies(): Flow<List<LabelCount>> = swipeDao.topLikedSpecies()
}
