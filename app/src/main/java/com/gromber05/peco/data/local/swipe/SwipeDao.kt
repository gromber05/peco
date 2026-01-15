package com.gromber05.peco.data.local.swipe

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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
}
