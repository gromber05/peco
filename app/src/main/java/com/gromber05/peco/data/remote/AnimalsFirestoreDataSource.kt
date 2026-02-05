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

/**
 * DataSource remoto encargado de gestionar la colección "animals" en Firestore.
 *
 * Responsabilidades:
 * - Observar en tiempo real la lista de animales (streaming con snapshot listener).
 * - Consultar un animal concreto por ID.
 * - Crear, actualizar y eliminar animales.
 * - Integrarse con [StorageDataSource] para subir fotos (cuando se proporcionan bytes).
 *
 * Arquitectura:
 * - Esta clase se sitúa en la capa "data" y se encarga únicamente del acceso a datos.
 * - Devuelve modelos de dominio/datos ([Animal]) a partir de documentos Firestore.
 *
 * Consideraciones:
 * - Se usa [callbackFlow] para convertir listeners de Firestore en [Flow].
 * - Se usan timestamps del servidor con [FieldValue.serverTimestamp] para `createdAt/updatedAt`.
 */
@Singleton
class AnimalsFirestoreDataSource @Inject constructor(
    /** Instancia de Firestore inyectada (Singleton) para operar con la base de datos. */
    private val db: FirebaseFirestore,
    /** DataSource de almacenamiento para subir archivos (fotos) y obtener su URL. */
    private val storage: StorageDataSource
) {

    /**
     * Referencia a la colección principal de animales.
     *
     * Estructura esperada:
     * - animals (colección)
     *   - {animalId} (documento)
     *     - name: String
     *     - species: String
     *     - photo: String? (URL)
     *     - dob: String
     *     - latitude: Double
     *     - longitude: Double
     *     - adoptionState: String (enum name)
     *     - volunteerId: String
     *     - createdAt: Timestamp
     *     - updatedAt: Timestamp
     */
    private fun animals() = db.collection("animals")

    /**
     * Observa en tiempo real la lista de animales, ordenada por fecha de creación descendente.
     *
     * Implementación:
     * - Usa `addSnapshotListener` y lo convierte a [Flow] mediante [callbackFlow].
     * - Mapea cada documento a un [Animal].
     * - Si faltan campos obligatorios (name/species), el documento se descarta (mapNotNull).
     *
     * Gestión de recursos:
     * - El listener se elimina automáticamente al cancelar el Flow ([awaitClose]).
     *
     * @return [Flow] que emite la lista actualizada de animales cada vez que Firestore cambia.
     */
    fun observeAnimals(): Flow<List<Animal>> = callbackFlow {
        val listener = animals()
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->

                // Si Firestore devuelve error, se cierra el Flow propagando la excepción.
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                // Se mapean documentos a Animal. Si faltan campos obligatorios, se ignoran.
                val list = snapshot?.documents.orEmpty().mapNotNull { d ->
                    val uid = d.id

                    val name = d.getString("name") ?: return@mapNotNull null
                    val species = d.getString("species") ?: return@mapNotNull null

                    val photo = d.getString("photo")
                    val dob = d.getString("dob") ?: ""
                    val lat = d.getDouble("latitude") ?: 0.0
                    val lon = d.getDouble("longitude") ?: 0.0
                    val volunteerId = d.getString("volunteerId") ?: ""

                    // Se convierte el string a enum. Si el valor es inválido, cae a AVAILABLE.
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

                // Se emite la lista actualizada al collector.
                trySend(list).isSuccess
            }

        // Se limpia el listener cuando se cancela el Flow.
        awaitClose { listener.remove() }
    }

    /**
     * Obtiene un animal por su identificador (ID de documento en "animals").
     *
     * @param animalId ID del animal a recuperar.
     * @return Un [Animal] si existe y contiene campos mínimos válidos, o `null` si no existe.
     */
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

    /**
     * Actualiza un animal existente en Firestore.
     *
     * Se usa `set(mapOf(...))` sobre el documento `animals/{animal.uid}`.
     *
     * Nota:
     * - Se añade `updatedAt` con timestamp del servidor.
     * - Requiere que el ID (uid) no esté vacío.
     *
     * @param animal Animal con los datos a persistir.
     * @throws IllegalArgumentException si `animal.uid` está vacío.
     */
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

    /**
     * Elimina un animal de Firestore.
     *
     * @param animalId ID del documento a eliminar.
     */
    suspend fun deleteAnimal(animalId: String) {
        animals().document(animalId).delete().await()
    }

    /**
     * Crea un animal en Firestore y, opcionalmente, sube su foto.
     *
     * Flujo:
     * 1) Se crea un nuevo documento con ID auto-generado.
     * 2) Si [photoBytes] no es null, se sube la imagen con [StorageDataSource] y se obtiene su URL.
     * 3) Se guardan los campos del animal junto con `createdAt` y `updatedAt`.
     *
     * Nota:
     * - Actualmente `photo` se guarda como `null` (ver comentario en el código).
     *   Para persistir la URL real, habría que cambiar `"photo" to null` por `"photo" to photoUrl`.
     *
     * @param animal Datos del animal a guardar.
     * @param photoBytes Bytes de la foto a subir (opcional).
     * @return ID del animal creado (ID del documento en "animals").
     */
    suspend fun createAnimal(
        animal: Animal,
        photoBytes: ByteArray?
    ): String {
        val ref = animals().document()
        val animalId = ref.id

        // Si hay foto, se sube al storage y se obtiene la URL resultante.
        val photoUrl: String? = if (photoBytes != null) {
            storage.uploadAnimalPhoto(animalId, photoBytes)
        } else {
            null
        }

        ref.set(
            mapOf(
                "name" to animal.name,
                "species" to animal.species,
                "photo" to null,  // cambiar esto a photoUrl para que puedan cargar las fotos.
                "dob" to animal.dob,
                "latitude" to animal.latitude,
                "longitude" to animal.longitude,
                "adoptionState" to animal.adoptionState.name,
                "volunteerId" to animal.volunteerId,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()

        // Log de depuración para verificar creación y posible URL subida.
        android.util.Log.d("ANIMALS", "Created animal id=$animalId photoUrl=$photoUrl")
        return animalId
    }
}
