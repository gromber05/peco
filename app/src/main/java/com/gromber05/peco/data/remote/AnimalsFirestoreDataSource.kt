package com.gromber05.peco.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.gromber05.peco.model.AdoptionState
import com.gromber05.peco.model.data.Animal
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnimalsFirestoreDataSource @Inject constructor(
    private val db: FirebaseFirestore
) {

    private fun animals() = db.collection("animals")

    fun observeAnimals(): Flow<List<Animal>> = callbackFlow {
        val listener = animals()
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val animals = snapshot?.documents
                    ?.mapNotNull { doc ->
                        doc.toObject(Animal::class.java)
                            ?.copy(uid = doc.id)
                    }
                    ?: emptyList()

                trySend(animals)
            }

        awaitClose { listener.remove() }
    }

    suspend fun getAnimalById(animalId: String): Animal? {
        val doc = animals().document(animalId).get().await()
        return doc.toObject(Animal::class.java)?.copy(uid = doc.id)
    }

    suspend fun updateAnimal(animal: Animal) {
        require(animal.uid.isNotBlank()) { "Animal id no puede estar vacío" }
        animals().document(animal.uid).set(animal).await()
    }

    suspend fun deleteAnimal(animalId: String) {
        animals().document(animalId).delete().await()
    }

    fun saveAnimal(animal: Animal) {
        FirebaseFirestore.getInstance()
            .collection("animals")
            .document(animal.uid)
            .set(
                mapOf(
                    "id" to animal.uid,
                    "name" to animal.name,
                    "species" to animal.species,
                    "photo" to animal.photo,
                    "dob" to animal.dob,
                    "latitude" to animal.latitude,
                    "longitude" to animal.longitude,
                    "adoptionState" to animal.adoptionState.name,
                    "volunteerId" to animal.volunteerId
                )
            )
    }
}
