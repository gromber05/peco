package com.gromber05.peco.data.repository

import com.gromber05.peco.data.local.swipe.SwipeAction
import com.gromber05.peco.data.local.swipe.SwipeDao
import com.gromber05.peco.data.local.swipe.SwipeEntity
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class SwipeRepository @Inject constructor(
    private val swipeDao: SwipeDao
) {
    fun observeSwipedIds(): Flow<List<Int>> = swipeDao.observeSwipedAnimalIds()

    fun observeLikedIds(): Flow<List<Int>> = swipeDao.observeLikedAnimalIds()

    suspend fun swipe(animalId: Int, action: SwipeAction) {
        swipeDao.upsertSwipe(SwipeEntity(animalId = animalId, action = action))
    }

    suspend fun undo(animalId: Int) {
        swipeDao.deleteByAnimalId(animalId)
    }

    suspend fun clearAll() {
        swipeDao.clearAll()
    }
}
