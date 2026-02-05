package com.gromber05.peco.data.repository

import com.gromber05.peco.data.remote.UsersFirestoreDataSource
import com.gromber05.peco.model.user.User
import com.gromber05.peco.model.user.UserRole
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio encargado de la gestión de perfiles de usuario.
 * Provee métodos para la creación, consulta y actualización de la información personal
 * y profesional de los usuarios en Firestore.
 *
 * @property db Fuente de datos remota ([UsersFirestoreDataSource]) para operaciones de perfil.
 */
@Singleton
class UserRepository @Inject constructor(
    private val db: UsersFirestoreDataSource
) {
    /**
     * Crea un nuevo perfil de usuario en la base de datos tras el registro inicial.
     * * @param uid Identificador único del usuario (de Firebase Auth).
     * @param username Nombre de usuario elegido.
     * @param email Correo electrónico vinculado.
     * @param role Rol asignado (por defecto [UserRole.USER]).
     * @param phone Número de teléfono de contacto.
     */
    suspend fun createProfile(uid: String, username: String, email: String, role: UserRole = UserRole.USER, phone: String) =
        db.createProfile(uid, username, email, role, phone)

    /**
     * Recupera el perfil del usuario una sola vez.
     * Ideal para comprobaciones puntuales o precarga de datos que no requieren actualización constante.
     * * @param uid ID del usuario a consultar.
     * @return Objeto [User] si existe, o null en caso contrario.
     */
    suspend fun getProfileOnce(uid: String): User? = db.getProfileOnce(uid)

    /**
     * Observa los cambios en el perfil del usuario de manera reactiva.
     * Cualquier cambio en el documento de Firestore se reflejará automáticamente en el flujo.
     * * @param uid ID del usuario a observar.
     * @return Un [Flow] que emite el objeto [User] actualizado.
     */
    fun observeProfile(uid: String): Flow<User?> = db.observeProfile(uid)

    /**
     * Actualiza la información básica del perfil del usuario.
     * * @param uid ID del usuario.
     * @param username Nuevo nombre de usuario.
     * @param photo URL o referencia de la nueva foto de perfil (opcional).
     */
    suspend fun updateProfile(uid: String, username: String, photo: String?) =
        db.updateProfile(uid, username, photo)

    /**
     * Sube una nueva imagen de perfil al almacenamiento y actualiza la referencia en el perfil.
     * * @param uid ID del usuario.
     * @param photoBytes El archivo de imagen en formato de arreglo de bytes.
     * @return La URL de descarga de la imagen subida.
     */
    suspend fun updateUserPhoto(uid: String, photoBytes: ByteArray): String =
        db.updateUserPhoto(uid, photoBytes)
}