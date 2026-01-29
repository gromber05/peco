package com.gromber05.peco.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.gromber05.peco.model.SwipeAction
import jakarta.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class SwipeRepository @Inject constructor(
    private val db: FirebaseFirestore
) {
    private fun swipes(uid: String) =
        db.collection("users").document(uid).collection("swipes")
    suspend fun setSwipe(
        uid: String,
        animalId: String,
        action: SwipeAction
    ) {
        db.collection("users")
            .document(uid)
            .collection("swipes")
            .document(animalId)
            .set(
                mapOf(
                    "action" to action.name,
                    "createdAt" to FieldValue.serverTimestamp()
                ),
                SetOptions.merge()
            ).await()
    }

    fun observeLikedAnimalIds(uid: String): Flow<Set<String>> = callbackFlow {
        val reg = db.collection("users").document(uid)
            .collection("swipes")
            .whereEqualTo("action", "LIKE")
            .addSnapshotListener { snap, err ->
                if (err != null) { close(err); return@addSnapshotListener }
                val ids = snap?.documents.orEmpty().map { it.id }.toSet()
                trySend(ids)
            }
        awaitClose { reg.remove() }
    }

    fun observeSwipedIds(uid: String): Flow<Set<Int>> = callbackFlow {
        val reg = swipes(uid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    close(err)
                    return@addSnapshotListener
                }

                val ids = snap?.documents
                    .orEmpty()
                    .mapNotNull { it.id.toIntOrNull() }
                    .toSet()

                trySend(ids)
            }

        awaitClose { reg.remove() }
    }

    fun observeLikedIds(uid: String): Flow<Set<Int>> = callbackFlow {
        val reg = swipes(uid)
            .whereEqualTo("action", "LIKE")
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    close(err)
                    return@addSnapshotListener
                }

                val ids = snap?.documents
                    .orEmpty()
                    .mapNotNull { it.id.toIntOrNull() }
                    .toSet()

                trySend(ids)
            }

        awaitClose { reg.remove() }
    }

    suspend fun clearAll(uid: String) {
        val swipesRef = swipes(uid)

        val snapshot = swipesRef.get().await()

        if (snapshot.isEmpty) return

        db.runBatch { batch ->
            snapshot.documents.forEach { doc ->
                batch.delete(doc.reference)
            }
        }.await()
    }
}
