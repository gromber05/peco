package com.gromber05.peco.data.remote

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.gromber05.peco.model.SwipeAction
import com.gromber05.peco.model.data.Animal
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DataSource remoto encargado de gestionar los "swipes" de un usuario en Firestore.
 *
 * Estructura esperada en Firestore:
 * - users/{uid}/swipes/{animalId}
 *
 * Cada documento de swipe guarda información mínima del animal y la acción realizada
 * (LIKE/DISLIKE), permitiendo:
 * - Registrar swipes de forma idempotente (por animalId).
 * - Consultar en tiempo real los animales likeados.
 * - Consultar en tiempo real los animales ya swipeados (para filtrarlos en UI).
 * - Limpiar el historial de swipes del usuario.
 *
 * Diseño:
 * - Se usan [Flow] + [callbackFlow] para emitir cambios en tiempo real mediante listeners.
 * - Se usa [SetOptions.merge] para no sobrescribir campos existentes innecesariamente.
 */
@Singleton
class SwipesFirestoreDataSource @Inject constructor(
    /** Instancia de Firestore inyectada por Hilt. */
    private val db: FirebaseFirestore
) {

    /**
     * Referencia a la subcolección de swipes de un usuario.
     *
     * @param uid UID del usuario.
     * @return Referencia a `users/{uid}/swipes`.
     */
    private fun swipes(uid: String) = db.collection("users").document(uid).collection("swipes")

    /**
     * Registra (o actualiza) el swipe de un usuario sobre un animal.
     *
     * El documento se guarda con ID = `animal.uid`, lo que garantiza que:
     * - Un usuario solo tenga 1 swipe por animal.
     * - Repetir el swipe actualiza el documento existente (merge).
     *
     * Campos guardados:
     * - animalId: ID del animal
     * - animalSpecies: especie del animal (para estadísticas / filtros)
     * - action: acción realizada (LIKE / DISLIKE)
     * - createdAt: timestamp del servidor
     *
     * @param uid UID del usuario que realiza el swipe.
     * @param animal Animal sobre el que se realiza el swipe.
     * @param action Acción realizada.
     */
    suspend fun setSwipe(uid: String, animal: Animal, action: SwipeAction) {
        swipes(uid)
            .document(animal.uid)
            .set(
                mapOf(
                    "animalId" to animal.uid,
                    "animalSpecies" to animal.species,
                    "action" to action.name,
                    "createdAt" to FieldValue.serverTimestamp()
                ),
                SetOptions.merge()
            )
            .await()
    }

    /**
     * Observa en tiempo real los IDs de animales a los que el usuario ha dado "LIKE".
     *
     * Implementación:
     * - Query filtrada por `action == "LIKE"`.
     * - Emite un [Set] con los IDs (document IDs) de los swipes.
     *
     * En caso de error o snapshot null:
     * - Emite `emptySet()` para evitar crasheos y simplificar manejo en UI.
     *
     * @param uid UID del usuario.
     * @return [Flow] con el set de IDs likeados.
     */
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

    /**
     * Observa en tiempo real todos los IDs de animales que el usuario ha swipeado
     * (LIKE o DISLIKE), útil para filtrar animales ya vistos.
     *
     * En caso de error o snapshot null:
     * - Emite `emptySet()`.
     *
     * @param uid UID del usuario.
     * @return [Flow] con el set de IDs swipeados.
     */
    fun observeSwipedIds(uid: String): Flow<Set<String>> = callbackFlow {
        val reg = swipes(uid)
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

    /**
     * Observa en tiempo real los IDs de animales likeados por el usuario.
     *
     * Similar a [observeLikedAnimalIds], pero usando el enum [SwipeAction.LIKE]
     * para construir el filtro (evita hardcodear el string).
     *
     * En caso de error o snapshot null:
     * - Emite `emptySet()`.
     *
     * @param uid UID del usuario.
     * @return [Flow] con el set de IDs likeados.
     */
    fun observeLikedIds(uid: String): Flow<Set<String>> = callbackFlow {
        val reg = swipes(uid)
            .whereEqualTo("action", SwipeAction.LIKE.name)
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

    /**
     * Elimina todos los swipes de un usuario.
     *
     * Implementación:
     * - Lee todos los documentos de `users/{uid}/swipes`.
     * - Si está vacío, no hace nada.
     * - Si hay documentos, ejecuta un batch delete para borrar todo de una vez.
     *
     * @param uid UID del usuario.
     */
    suspend fun clearAll(uid: String) {
        val snapshot = swipes(uid).get().await()
        if (snapshot.isEmpty) return

        db.runBatch { batch ->
            snapshot.documents.forEach { doc -> batch.delete(doc.reference) }
        }.await()
    }
}
