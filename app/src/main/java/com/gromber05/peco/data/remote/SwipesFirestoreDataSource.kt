package com.gromber05.peco.data.remote

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.gromber05.peco.model.SwipeAction
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SwipesFirestoreDataSource @Inject constructor(
    private val db: FirebaseFirestore
) {
    private fun swipes(uid: String) = db.collection("users").document(uid).collection("swipes")

    suspend fun setSwipe(uid: String, animalId: String, action: SwipeAction) {
        swipes(uid)
            .document(animalId)
            .set(
                mapOf(
                    "action" to action.name,
                    "createdAt" to FieldValue.serverTimestamp()
                ),
                SetOptions.merge()
            )
            .await()
    }

    fun observeLikedAnimalIds(uid: String): Flow<Set<String>> = callbackFlow {
        val reg = swipes(uid)
            .whereEqualTo("action", "LIKE")
            .addSnapshotListener { snap, err ->
                if (err != null || snap == null) {
                    trySend(emptySet())
                    return@addSnapshotListener
                }
                val ids = snap.documents.map { it.id }.toSet()
                trySend(ids)
            }
        awaitClose { reg.remove() }
    }

    fun observeSwipedIds(uid: String): Flow<Set<String>> = callbackFlow {
        val ref = db.collection("swipes").document(uid)

        val listener = ref.addSnapshotListener { snap, err ->
            if (err != null) {
                trySend(emptySet())
                return@addSnapshotListener
            }

            val raw = snap?.get("swipedIds")

            val swiped: Set<String> = when (raw) {
                is List<*> -> raw.mapNotNull { it?.toString() }.toSet()
                is Set<*> -> raw.mapNotNull { it?.toString() }.toSet()
                else -> emptySet()
            }

            trySend(swiped)
        }

        awaitClose { listener.remove() }
    }

    fun observeLikedIds(uid: String): Flow<Set<String>> = callbackFlow {
        val ref = db.collection("swipes").document(uid)

        val listener = ref.addSnapshotListener { snap, err ->
            if (err != null) {
                trySend(emptySet())
                return@addSnapshotListener
            }

            val raw = snap?.get("likedIds")

            val liked: Set<String> = when (raw) {
                is List<*> -> raw.mapNotNull { it?.toString() }.toSet()
                is Set<*> -> raw.mapNotNull { it?.toString() }.toSet()
                else -> emptySet()
            }

            trySend(liked)
        }

        awaitClose { listener.remove() }
    }

    suspend fun clearAll(uid: String) {
        val snapshot = swipes(uid).get().await()
        if (snapshot.isEmpty) return

        db.runBatch { batch ->
            snapshot.documents.forEach { doc -> batch.delete(doc.reference) }
        }.await()
    }
}
