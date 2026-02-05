package com.gromber05.peco.data.repository

import com.gromber05.peco.data.remote.AdminStatsFirestoreDataSource
import com.gromber05.peco.model.data.LabelCount
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio encargado de gestionar las estadísticas administrativas de la aplicación.
 * * Actúa como mediador entre la fuente de datos de Firestore ([AdminStatsFirestoreDataSource])
 * y la capa de UI o dominio, exponiendo los datos mediante flujos reactivos ([Flow]).
 *
 * @property db Fuente de datos remota que provee la información estadística.
 */
@Singleton
class AdminStatsRepository @Inject constructor(
    private val db: AdminStatsFirestoreDataSource
) {
    /** Recupera el conteo total de animales registrados. */
    fun totalAnimals(): Flow<Int> = db.totalAnimals()

    /** Recupera el número de animales que se encuentran disponibles para adopción. */
    fun availableAnimals(): Flow<Int> = db.availableAnimals()

    /** Recupera el número total de animales que ya han sido adoptados. */
    fun adoptedAnimals(): Flow<Int> = db.adoptedAnimals()

    /** Recupera el número de animales en estado pendiente de proceso. */
    fun pendingAnimals(): Flow<Int> = db.pendingAnimals()

    /** * Obtiene una lista detallada de la cantidad de animales agrupados por su especie.
     * @return Un [Flow] que contiene una lista de objetos [LabelCount].
     */
    fun animalsBySpecies(): Flow<List<LabelCount>> = db.animalsBySpecies()

    /** Recupera el conteo global de "Me gusta" (likes) recibidos. */
    fun likes(): Flow<Int> = db.likes()

    /** Recupera el conteo global de "No me gusta" (dislikes) recibidos. */
    fun dislikes(): Flow<Int> = db.dislikes()

    /** * Obtiene el ranking de las especies con mayor número de interacciones positivas.
     * @return Un [Flow] con la lista de especies y sus respectivos conteos.
     */
    fun topLikedSpecies(): Flow<List<LabelCount>> = db.topLikedSpecies()
}