package com.gromber05.peco.data.repository

import com.gromber05.peco.data.remote.AdminStatsFirestoreDataSource
import com.gromber05.peco.model.data.LabelCount
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminStatsRepository @Inject constructor(
    private val db: AdminStatsFirestoreDataSource
) {
    fun totalAnimals(): Flow<Int> = db.totalAnimals()
    fun availableAnimals(): Flow<Int> = db.availableAnimals()
    fun adoptedAnimals(): Flow<Int> = db.adoptedAnimals()
    fun pendingAnimals(): Flow<Int> = db.pendingAnimals()
    fun animalsBySpecies(): Flow<List<LabelCount>> = db.animalsBySpecies()
    fun likes(): Flow<Int> = db.likes()
    fun dislikes(): Flow<Int> = db.dislikes()
    fun topLikedSpecies(): Flow<List<LabelCount>> = db.topLikedSpecies()
}
