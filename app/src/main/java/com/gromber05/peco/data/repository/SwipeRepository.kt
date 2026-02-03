package com.gromber05.peco.data.repository

import com.gromber05.peco.data.remote.SwipesFirestoreDataSource
import com.gromber05.peco.model.SwipeAction
import com.gromber05.peco.model.data.Animal
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SwipeRepository @Inject constructor(
    private val db: SwipesFirestoreDataSource
) {
    suspend fun setSwipe(uid: String, animal: Animal, action: SwipeAction) = db.setSwipe(uid, animal, action)
    fun observeLikedAnimalIds(uid: String): Flow<Set<String>> = db.observeLikedAnimalIds(uid)
    fun observeSwipedIds(uid: String): Flow<Set<String>> = db.observeSwipedIds(uid)
    fun observeLikedIds(uid: String): Flow<Set<String>> = db.observeLikedIds(uid)
    suspend fun clearAll(uid: String) = db.clearAll(uid)
}
