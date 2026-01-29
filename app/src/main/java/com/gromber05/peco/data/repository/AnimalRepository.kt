package com.gromber05.peco.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.gromber05.peco.model.AdoptionState
import com.gromber05.peco.model.data.Animal
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@Singleton
class AnimalRepository @Inject constructor(
    private val db: FirebaseFirestore
){
    fun observeAnimals(): Flow<List<Animal>> = callbackFlow {
        val reg = FirebaseFirestore.getInstance()
            .collection("animals")
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    close(err)
                    return@addSnapshotListener
                }

                val animals = snap?.documents.orEmpty().mapNotNull { doc ->
                    try {
                        Animal(
                            uid = doc.getString("id") ?: return@mapNotNull null,
                            name = doc.getString("name") ?: return@mapNotNull null,
                            species = doc.getString("species") ?: "",
                            photo = doc.getString("photo"),
                            dob = doc.getString("dob") ?: "",
                            latitude = doc.getDouble("latitude") ?: 0.0,
                            longitude = doc.getDouble("longitude") ?: 0.0,
                            adoptionState = AdoptionState.valueOf(
                                doc.getString("adoptionState") ?: AdoptionState.AVAILABLE.name
                            ),
                            volunteerId = doc.getString("volunteerId") ?: ""
                        )
                    } catch (e: Exception) {
                        null
                    }
                }

                trySend(animals)
            }

        awaitClose { reg.remove() }
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