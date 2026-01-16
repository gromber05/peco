package com.gromber05.peco.data.local.animal

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gromber05.peco.model.data.LabelCount
import kotlinx.coroutines.flow.Flow


@Dao
interface AnimalDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAnimal(animal: AnimalEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAnimals(animals: List<AnimalEntity>)

    @Query("SELECT * FROM animals WHERE id = :id LIMIT 1")
    suspend fun getAnimalById(id: Int): AnimalEntity?

    @Query("DELETE FROM animals WHERE id = :id")
    suspend fun removeAnimal(id: Int): Int

    @Query("SELECT * FROM animals")
    fun getAllAnimals(): Flow<List<AnimalEntity>>

    @Query("SELECT COUNT(*) FROM animals")
    fun countAnimals(): kotlinx.coroutines.flow.Flow<Int>

    @Query("SELECT COUNT(*) FROM animals WHERE adoptionState = :state")
    fun countAnimalsByState(state: String): kotlinx.coroutines.flow.Flow<Int>

    @Query("""
    SELECT species AS label, COUNT(*) AS count
    FROM animals
    GROUP BY species
    ORDER BY count DESC
""")
    fun countBySpecies(): kotlinx.coroutines.flow.Flow<List<LabelCount>>

}