package com.gromber05.peco.data.local.animal

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface AnimalDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAnimal(animal: AnimalEntity)

    @Query("SELECT * FROM animals WHERE id = :id LIMIT 1")
    suspend fun getAnimalById(id: Int): AnimalEntity?

    @Query("DELETE FROM animals WHERE id = :id")
    suspend fun removeAnimal(id: Int): Int

    @Query("SELECT * FROM animals")
    fun getAllAnimals(): Flow<List<AnimalEntity>>
}