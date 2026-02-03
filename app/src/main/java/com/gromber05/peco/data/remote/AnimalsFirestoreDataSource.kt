package com.gromber05.peco.data.remote

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.gromber05.peco.model.AdoptionState
import com.gromber05.peco.model.data.Animal
import com.gromber05.peco.utils.uriToBytes
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnimalsFirestoreDataSource @Inject constructor(
    private val db: FirebaseFirestore,
    private val storage: StorageDataSource
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

                val list = snapshot?.documents.orEmpty().mapNotNull { d ->
                    val uid = d.id

                    val name = d.getString("name") ?: return@mapNotNull null
                    val species = d.getString("species") ?: return@mapNotNull null

                    val photo = d.getString("photo")
                    val dob = d.getString("dob") ?: ""
                    val lat = d.getDouble("latitude") ?: 0.0
                    val lon = d.getDouble("longitude") ?: 0.0
                    val volunteerId = d.getString("volunteerId") ?: ""

                    val stateStr = d.getString("adoptionState") ?: "AVAILABLE"
                    val adoptionState = runCatching { AdoptionState.valueOf(stateStr) }
                        .getOrDefault(AdoptionState.AVAILABLE)

                    Animal(
                        uid = uid,
                        name = name,
                        species = species,
                        photo = photo,
                        dob = dob,
                        latitude = lat,
                        longitude = lon,
                        adoptionState = adoptionState,
                        volunteerId = volunteerId
                    )
                }

                trySend(list).isSuccess
            }

        awaitClose { listener.remove() }
    }

    suspend fun getAnimalById(animalId: String): Animal? {
        val doc = animals().document(animalId).get().await()
        if (!doc.exists()) return null

        val uid = doc.id
        val name = doc.getString("name") ?: return null
        val species = doc.getString("species") ?: return null
        val photo = doc.getString("photo")
        val dob = doc.getString("dob") ?: ""
        val lat = doc.getDouble("latitude") ?: 0.0
        val lon = doc.getDouble("longitude") ?: 0.0
        val volunteerId = doc.getString("volunteerId") ?: ""
        val stateStr = doc.getString("adoptionState") ?: "AVAILABLE"
        val adoptionState = runCatching { AdoptionState.valueOf(stateStr) }
            .getOrDefault(AdoptionState.AVAILABLE)

        return Animal(
            uid = uid,
            name = name,
            species = species,
            photo = photo,
            dob = dob,
            latitude = lat,
            longitude = lon,
            adoptionState = adoptionState,
            volunteerId = volunteerId
        )
    }

    suspend fun updateAnimal(animal: Animal) {
        require(animal.uid.isNotBlank()) { "Animal id no puede estar vacío" }

        animals().document(animal.uid)
            .set(
                mapOf(
                    "name" to animal.name,
                    "species" to animal.species,
                    "photo" to animal.photo,
                    "dob" to animal.dob,
                    "latitude" to animal.latitude,
                    "longitude" to animal.longitude,
                    "adoptionState" to animal.adoptionState.name,
                    "volunteerId" to animal.volunteerId,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
            .await()
    }

    suspend fun deleteAnimal(animalId: String) {
        animals().document(animalId).delete().await()
    }

    suspend fun createAnimal(
        animal: Animal,
        photoBytes: ByteArray?
    ): String {
        val ref = animals().document()
        val animalId = ref.id

        val photoUrl: String? = if (photoBytes != null) {
            storage.uploadAnimalPhoto(animalId, photoBytes)
        } else {
            null
        }

        ref.set(
            mapOf(
                "name" to animal.name,
                "species" to animal.species,
                "photo" to photoUrl,
                "dob" to animal.dob,
                "latitude" to animal.latitude,
                "longitude" to animal.longitude,
                "adoptionState" to animal.adoptionState.name,
                "volunteerId" to animal.volunteerId,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()

        return animalId
    }
}
