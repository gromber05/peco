package com.gromber05.peco.data.repository

import com.gromber05.peco.data.remote.AnimalsFirestoreDataSource
import com.gromber05.peco.data.remote.StorageDataSource
import com.gromber05.peco.model.data.Animal
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio encargado de gestionar la lógica de negocio y el acceso a datos de los animales.
 * Sirve como punto único de acceso para la gestión de la colección de animales en la aplicación.
 *
 * @property db Fuente de datos remota (Firestore) para realizar operaciones CRUD sobre los animales.
 */
@Singleton
class AnimalRepository @Inject constructor(private val db: AnimalsFirestoreDataSource) {

    /**
     * Expone un flujo continuo de la lista de animales registrados.
     * Al ser un [Flow], la interfaz de usuario se actualizará automáticamente ante cualquier
     * cambio en la base de datos de Firestore.
     *
     * @return Un flujo reactivo con la lista actualizada de objetos [Animal].
     */
    fun observeAnimals(): Flow<List<Animal>> = db.observeAnimals()

    /**
     * Registra un nuevo animal en el sistema.
     * Esta es una función de suspensión que maneja tanto la información textual como la imagen opcional.
     *
     * @param animal Objeto con la información del animal a crear.
     * @param photoBytes Arreglo de bytes de la fotografía del animal (opcional).
     */
    suspend fun createAnimal(animal: Animal, photoBytes: ByteArray?) = db.createAnimal(animal, photoBytes)

    /**
     * Elimina un animal del sistema mediante su identificador único.
     *
     * @param animalId El ID del documento del animal que se desea eliminar.
     */
    suspend fun deleteAnimal(animalId: String) = db.deleteAnimal(animalId)

    /**
     * Recupera la información detallada de un animal específico.
     *
     * @param animalId El identificador único del animal.
     * @return El objeto [Animal] si se encuentra, o null en caso contrario.
     */
    suspend fun getAnimalById(animalId: String) = db.getAnimalById(animalId)
}