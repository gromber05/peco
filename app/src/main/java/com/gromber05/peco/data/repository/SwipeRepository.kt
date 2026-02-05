package com.gromber05.peco.data.repository

import com.gromber05.peco.data.remote.SwipesFirestoreDataSource
import com.gromber05.peco.model.SwipeAction
import com.gromber05.peco.model.data.Animal
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio encargado de gestionar las interacciones de deslizamiento (swipes) de los usuarios.
 * Registra y consulta las preferencias (likes/dislikes) de los usuarios sobre los animales.
 *
 * @property db Fuente de datos remota ([SwipesFirestoreDataSource]) para persistir los movimientos.
 */
@Singleton
class SwipeRepository @Inject constructor(
    private val db: SwipesFirestoreDataSource
) {
    /**
     * Registra una acción de deslizamiento realizada por un usuario sobre un animal específico.
     * * @param uid Identificador único del usuario que realiza la acción.
     * @param animal El objeto [Animal] sobre el cual se interactúa.
     * @param action El tipo de acción realizada (ej: LIKE, DISLIKE) definida en [SwipeAction].
     */
    suspend fun setSwipe(uid: String, animal: Animal, action: SwipeAction) = db.setSwipe(uid, animal, action)

    /**
     * Expone un flujo con el conjunto de IDs de animales que han recibido un "Like" por el usuario.
     * Útil para mostrar la lista de favoritos o animales de interés.
     * * @param uid ID del usuario actual.
     * @return Un [Flow] que emite un [Set] con los IDs de los animales marcados con Like.
     */
    fun observeLikedAnimalIds(uid: String): Flow<Set<String>> = db.observeLikedAnimalIds(uid)

    /**
     * Observa todos los IDs de animales con los que el usuario ya ha interactuado (ya sea Like o Dislike).
     * Fundamental para filtrar y no volver a mostrar animales ya deslizados en el deck principal.
     * * @param uid ID del usuario actual.
     * @return Un [Flow] con el conjunto total de IDs ya procesados.
     */
    fun observeSwipedIds(uid: String): Flow<Set<String>> = db.observeSwipedIds(uid)

    /**
     * Similar a [observeLikedAnimalIds], obtiene el flujo de IDs que tienen una interacción positiva.
     * * @param uid ID del usuario actual.
     */
    fun observeLikedIds(uid: String): Flow<Set<String>> = db.observeLikedIds(uid)

    /**
     * Elimina todo el historial de interacciones de un usuario.
     * Se utiliza normalmente para reiniciar las sugerencias o limpiar datos de cuenta.
     * * @param uid ID del usuario cuyo historial se desea vaciar.
     */
    suspend fun clearAll(uid: String) = db.clearAll(uid)
}