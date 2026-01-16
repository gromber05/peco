package com.gromber05.peco.data.local.swipe

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gromber05.peco.model.data.LabelCount
import kotlinx.coroutines.flow.Flow

@Dao
interface SwipeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSwipe(swipe: SwipeEntity)

    @Query("SELECT animalId FROM swipes")
    fun observeSwipedAnimalIds(): Flow<List<Int>>

    @Query("SELECT animalId FROM swipes WHERE action = 'LIKE'")
    fun observeLikedAnimalIds(): Flow<List<Int>>

    @Query("DELETE FROM swipes WHERE animalId = :animalId")
    suspend fun deleteByAnimalId(animalId: Int)

    @Query("DELETE FROM swipes")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM swipes WHERE action = 'LIKE'")
    fun countLikes(): kotlinx.coroutines.flow.Flow<Int>

    @Query("SELECT COUNT(*) FROM swipes WHERE action = 'DISLIKE'")
    fun countDislikes(): kotlinx.coroutines.flow.Flow<Int>

    @Query("""
    SELECT a.species AS label, COUNT(*) AS count
    FROM animals a
    INNER JOIN swipes s ON s.animalId = a.id
    WHERE s.action = 'LIKE'
    GROUP BY a.species
    ORDER BY count DESC
    LIMIT 5
""")
    fun topLikedSpecies(): kotlinx.coroutines.flow.Flow<List<LabelCount>>

}
