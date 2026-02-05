package com.gromber05.peco.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.gromber05.peco.model.data.LabelCount
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


/**
 * DataSource de estadísticas para el panel de administración usando Firestore.
 *
 * Este DataSource expone métricas en tiempo real mediante [Flow], transformando
 * listeners de Firestore en Flows con [callbackFlow].
 *
 * Estadísticas que ofrece:
 * - Totales de animales y conteos por estado (AVAILABLE / ADOPTED / PENDING).
 * - Distribución de animales por especie.
 * - Conteo global de swipes por acción (LIKE / DISLIKE).
 * - Ranking de especies más likeadas.
 *
 * ⚠️ Importante:
 * - Se usa `collectionGroup("swipes")`, lo que agrega swipes de *toda* la base de datos
 *   (todas las subcolecciones llamadas "swipes" bajo cualquier documento).
 *   Si necesitas limitar por protectora, usuario, etc., deberías añadir filtros
 *   (por ejemplo `whereEqualTo("shelterId", ...)` o `whereEqualTo("uid", ...)`).
 *
 * Diseño:
 * - Para reducir coste y carga de datos, se mapean documentos a modelos mínimos
 *   ([AnimalMini] y [SwipeMini]) usando solo los campos necesarios.
 */
@Singleton
class AdminStatsFirestoreDataSource @Inject constructor(
    /** Instancia de Firestore inyectada con Hilt. */
    private val db: FirebaseFirestore
) {
    /**
     * Referencia a la colección principal de animales.
     *
     * Estructura esperada:
     * - animals (colección)
     *   - {animalId} (documento)
     *     - species: String
     *     - adoptionState: String
     *     - ...
     */
    private fun animals() = db.collection("animals")

    /**
     * Devuelve el número total de animales registrados.
     *
     * Implementación:
     * - Observa en tiempo real la colección "animals" ([observeAnimals])
     * - Emite el tamaño de la lista.
     *
     * @return [Flow] que emite el total de animales cuando hay cambios.
     */
    fun totalAnimals(): Flow<Int> =
        observeAnimals().map { it.size }

    /**
     * Devuelve el número de animales disponibles ("AVAILABLE").
     *
     * @return [Flow] que emite el conteo en tiempo real.
     */
    fun availableAnimals(): Flow<Int> =
        countAnimalsByState("AVAILABLE")

    /**
     * Devuelve el número de animales adoptados ("ADOPTED").
     *
     * @return [Flow] que emite el conteo en tiempo real.
     */
    fun adoptedAnimals(): Flow<Int> =
        countAnimalsByState("ADOPTED")

    /**
     * Devuelve el número de animales pendientes ("PENDING").
     *
     * @return [Flow] que emite el conteo en tiempo real.
     */
    fun pendingAnimals(): Flow<Int> =
        countAnimalsByState("PENDING")

    /**
     * Cuenta animales por estado de adopción.
     *
     * Nota:
     * - La comparación es case-insensitive para tolerar datos en Firestore.
     *
     * @param state Estado objetivo a contar.
     * @return [Flow] con el número de animales en ese estado.
     */
    private fun countAnimalsByState(state: String): Flow<Int> =
        observeAnimals().map { list ->
            list.count { it.adoptionState.equals(state, ignoreCase = true) }
        }

    /**
     * Devuelve el número de animales agrupados por especie.
     *
     * Salida:
     * - Lista de [LabelCount] con:
     *   - label = especie
     *   - count = número de animales de esa especie
     *
     * Además, ordena la lista por cantidad descendente.
     *
     * @return [Flow] con el ranking de especies por nº de animales.
     */
    fun animalsBySpecies(): Flow<List<LabelCount>> =
        observeAnimals().map { animals ->
            animals
                .groupBy { it.species }
                .map { (species, list) ->
                    LabelCount(species, list.size)
                }
                .sortedByDescending { it.count }
        }

    /**
     * Referencia a todas las subcolecciones llamadas "swipes" en Firestore.
     *
     * `collectionGroup("swipes")` busca en toda la BD, independientemente del padre.
     */
    private fun swipes() = db.collectionGroup("swipes")

    /**
     * Query de swipes filtrados por acción.
     *
     * @param action Acción a filtrar (ej. "LIKE" / "DISLIKE").
     * @return Query con el filtro aplicado.
     */
    private fun swipesByAction(action: String) =
        db.collectionGroup("swipes").whereEqualTo("action", action)

    /**
     * Devuelve el número total de "LIKE" (global).
     *
     * Implementación:
     * - Usa un listener directo sobre la query filtrada `action=LIKE`
     * - Devuelve el `snap.size()` para no mapear documentos si solo se quiere el conteo.
     *
     * @return [Flow] que emite el número total de likes en tiempo real.
     */
    fun likes(): Flow<Int> = callbackFlow {
        val reg = swipesByAction("LIKE").addSnapshotListener { snap, err ->
            if (err != null) { close(err); return@addSnapshotListener }
            trySend(snap?.size() ?: 0)
        }
        awaitClose { reg.remove() }
    }

    /**
     * Devuelve el número total de "DISLIKE" (global).
     *
     * @return [Flow] que emite el número total de dislikes en tiempo real.
     */
    fun dislikes(): Flow<Int> =
        countSwipesByAction("DISLIKE")

    /**
     * Cuenta swipes por acción a partir de [observeSwipes].
     *
     * @param action Acción objetivo a contar.
     * @return [Flow] con el número de swipes que coinciden con la acción.
     */
    private fun countSwipesByAction(action: String): Flow<Int> =
        observeSwipes().map { list ->
            list.count { it.action == action }
        }

    /**
     * Devuelve un ranking de especies más "likeadas".
     *
     * Flujo:
     * - Observa todos los swipes ([observeSwipes])
     * - Filtra los "LIKE"
     * - Agrupa por `animalSpecies`
     * - Mapea a [LabelCount] y ordena descendentemente
     *
     * @return [Flow] con la lista de especies ordenadas por nº de likes.
     */
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

    /**
     * Modelo mínimo para representar un animal en estadísticas.
     *
     * Se limita a los campos estrictamente necesarios para:
     * - Contar por estado
     * - Agrupar por especie
     */
    private data class AnimalMini(
        val species: String,
        val adoptionState: String
    )

    /**
     * Observa la colección "animals" en tiempo real y emite una lista de [AnimalMini].
     *
     * Campos leídos desde Firestore:
     * - species: String (si falta -> "Unknown")
     * - adoptionState: String (si falta -> "AVAILABLE")
     *
     * Manejo de recursos:
     * - El listener se elimina cuando el Flow se cancela ([awaitClose]).
     *
     * @return [Flow] con lista de animales reducidos.
     */
    private fun observeAnimals(): Flow<List<AnimalMini>> = callbackFlow {
        val reg = animals().addSnapshotListener { snap, err ->
            if (err != null) {
                close(err)
                return@addSnapshotListener
            }

            val list = snap?.documents.orEmpty().map { d ->
                val species = d.getString("species") ?: "Unknown"
                val state = d.getString("adoptionState") ?: "AVAILABLE"
                AnimalMini(species, state)
            }

            trySend(list)
        }

        awaitClose { reg.remove() }
    }

    /**
     * Modelo mínimo para representar un swipe en estadísticas.
     *
     * Se limita a:
     * - action: tipo de swipe (LIKE / DISLIKE)
     * - animalSpecies: especie del animal al que se aplicó el swipe
     */
    private data class SwipeMini(
        val action: String,
        val animalSpecies: String
    )

    /**
     * Observa en tiempo real todos los documentos de `collectionGroup("swipes")`.
     *
     * Requisitos para mapear un documento:
     * - Debe tener `action` y `animalSpecies` no nulos.
     * - Si falta algún campo, el documento se descarta (mapNotNull).
     *
     * Manejo de recursos:
     * - El listener se elimina cuando el Flow se cancela ([awaitClose]).
     *
     * @return [Flow] con lista de swipes reducidos.
     */
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
