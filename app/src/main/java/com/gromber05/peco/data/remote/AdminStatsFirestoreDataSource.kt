package com.gromber05.peco.data.remote

import androidx.annotation.Size
import com.google.firebase.firestore.FirebaseFirestore
import com.gromber05.peco.model.data.LabelCount
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.component1
import kotlin.collections.component2

@Singleton
class AdminStatsFirestoreDataSource @Inject constructor(
    private val db: FirebaseFirestore
) {
    private fun animals() = db.collection("animals")

    fun totalAnimals(): Flow<Int> =
        observeAnimals().map { it.size }

    fun availableAnimals(): Flow<Int> =
        countAnimalsByState("AVAILABLE")

    fun adoptedAnimals(): Flow<Int> =
        countAnimalsByState("ADOPTED")

    fun pendingAnimals(): Flow<Int> =
        countAnimalsByState("PENDING")

    private fun countAnimalsByState(state: String): Flow<Int> =
        observeAnimals().map { list ->
            list.count { it.adoptionState == state }
        }

    fun animalsBySpecies(): Flow<List<LabelCount>> =
        observeAnimals().map { animals ->
            animals
                .groupBy { it.species }
                .map { (species, list) ->
                    LabelCount(species, list.size)
                }
                .sortedByDescending { it.count }
        }

    private fun swipes() = db.collectionGroup("swipes")

    fun likes(): Flow<Int> =
        countSwipesByAction("LIKE")

    fun dislikes(): Flow<Int> =
        countSwipesByAction("DISLIKE")

    private fun countSwipesByAction(action: String): Flow<Int> =
        observeSwipes().map { list ->
            list.count { it.action == action }
        }

    fun topLikedSpecies(): Flow<List<LabelCount>> =
        observeSwipes().map { swipes ->
            swipes
                .filter { it.action == "LIKE" }
                .groupBy { it.animalSpecies }
                .map { (species, list) ->
                    LabelCount(species, list.size)
                }
                .sortedByDescending { it.count }
        }

    private data class AnimalMini(
        val species: String,
        val adoptionState: String
    )

    private fun observeAnimals(): Flow<List<AnimalMini>> = callbackFlow {
        val reg = animals()
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    close(err)
                    return@addSnapshotListener
                }

                val list = snap?.documents.orEmpty().mapNotNull { d ->
                    val species = d.getString("species")
                    val state = d.getString("adoptionState")
                    if (species != null && state != null) {
                        AnimalMini(species, state)
                    } else null
                }

                trySend(list)
            }

        awaitClose { reg.remove() }
    }

    private data class SwipeMini(
        val action: String,
        val animalSpecies: String
    )

    private fun observeSwipes(): Flow<List<SwipeMini>> = callbackFlow {
        val reg = swipes()
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    close(err)
                    return@addSnapshotListener
                }

                val list = snap?.documents.orEmpty().mapNotNull { d ->
                    val action = d.getString("action")
                    val species = d.getString("animalSpecies")
                    if (action != null && species != null) {
                        SwipeMini(action, species)
                    } else null
                }

                trySend(list)
            }

        awaitClose { reg.remove() }
    }
}