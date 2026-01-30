package com.gromber05.peco.data.repository

import com.gromber05.peco.data.remote.SwipesFirestoreDataSource
import com.gromber05.peco.model.SwipeAction
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SwipeRepository @Inject constructor(
    private val db: SwipesFirestoreDataSource
) {
    suspend fun setSwipe(uid: String, animalId: String, action: SwipeAction) = db.setSwipe(uid, animalId, action)
    fun observeLikedAnimalIds(uid: String): Flow<Set<String>> = db.observeLikedAnimalIds(uid)
    fun observeSwipedIds(uid: String): Flow<Set<Int>> = db.observeSwipedIds(uid)
    fun observeLikedIds(uid: String): Flow<Set<Int>> = db.observeLikedIds(uid)
    suspend fun clearAll(uid: String) = db.clearAll(uid)
}
