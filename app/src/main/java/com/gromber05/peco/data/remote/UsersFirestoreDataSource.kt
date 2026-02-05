package com.gromber05.peco.data.remote

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.gromber05.peco.model.user.User
import com.gromber05.peco.model.user.UserRole
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DataSource remoto encargado de gestionar los perfiles de usuario en Firestore.
 *
 * Responsabilidades:
 * - Crear el perfil inicial del usuario tras el registro.
 * - Obtener el perfil de usuario (una vez o en tiempo real).
 * - Actualizar datos del perfil (username, foto).
 * - Integrarse con [StorageDataSource] para la subida de avatares.
 *
 * Arquitectura:
 * - Pertenece a la capa data/remote.
 * - Encapsula el acceso directo a Firestore, desacoplando la lógica de datos
 *   de ViewModels y Repositories.
 *
 * Consideraciones:
 * - Usa [SetOptions.merge] para evitar sobrescribir campos existentes.
 * - Usa timestamps del servidor para `createdAt` y `updatedAt`.
 * - Expone observación en tiempo real mediante [Flow].
 */
@Singleton
class UsersFirestoreDataSource @Inject constructor(
    /** Instancia de Firestore inyectada por Hilt. */
    private val db: FirebaseFirestore,
    /** DataSource de almacenamiento para subir y obtener avatares de usuario. */
    private val storage: StorageDataSource
) {

    /**
     * Referencia a la colección principal de usuarios.
     *
     * Estructura esperada:
     * - users (colección)
     *   - {uid} (documento)
     *     - username: String
     *     - email: String
     *     - photo: String?
     *     - role: String (enum name)
     *     - phone: String
     *     - createdAt: Timestamp
     *     - updatedAt: Timestamp
     */
    private fun users() = db.collection("users")

    /**
     * Crea o inicializa el perfil de un usuario en Firestore.
     *
     * Se ejecuta normalmente tras el registro del usuario en FirebaseAuth.
     * Usa `merge()` para no sobrescribir datos si el documento ya existe.
     *
     * @param uid UID del usuario.
     * @param username Nombre visible del usuario.
     * @param email Email del usuario.
     * @param role Rol del usuario dentro de la aplicación (por defecto USER).
     * @param phone Teléfono de contacto del usuario.
     */
    suspend fun createProfile(
        uid: String,
        username: String,
        email: String,
        role: UserRole = UserRole.USER,
        phone: String
    ) {
        users().document(uid).set(
            mapOf(
                "username" to username,
                "email" to email,
                "photo" to null,
                "role" to role.name,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp(),
                "phone" to phone
            ),
            SetOptions.merge()
        ).await()
    }

    /**
     * Actualiza la foto de perfil del usuario.
     *
     * Flujo:
     * 1) Sube la imagen a Firebase Storage mediante [StorageDataSource].
     * 2) Guarda la URL resultante en el documento del usuario.
     * 3) Actualiza el campo `updatedAt`.
     *
     * @param uid UID del usuario.
     * @param photoBytes Contenido del avatar en formato ByteArray.
     * @return URL pública del avatar subido.
     */
    suspend fun updateUserPhoto(uid: String, photoBytes: ByteArray): String {
        val url = storage.uploadUserAvatar(uid, photoBytes)

        users().document(uid)
            .set(
                mapOf(
                    "photo" to url,
                    "updatedAt" to FieldValue.serverTimestamp()
                ),
                SetOptions.merge()
            ).await()

        return url
    }

    /**
     * Obtiene el perfil de usuario una sola vez (lectura puntual).
     *
     * @param uid UID del usuario.
     * @return Un [User] si el documento existe, o `null` en caso contrario.
     */
    suspend fun getProfileOnce(uid: String): User? {
        val snap = db.collection("users").document(uid).get().await()
        if (!snap.exists()) return null

        val roleRaw = snap.getString("role") ?: UserRole.USER.name

        return User(
            uid = uid,
            username = snap.getString("username").orEmpty(),
            email = snap.getString("email").orEmpty(),
            photo = snap.getString("photo"),
            role = runCatching { UserRole.valueOf(roleRaw) }
                .getOrElse { UserRole.USER },
            phone = snap.getString("phone").orEmpty()
        )
    }

    /**
     * Observa en tiempo real el perfil de un usuario.
     *
     * Implementación:
     * - Usa `addSnapshotListener` convertido a [Flow] con [callbackFlow].
     * - Emite `null` si el documento no existe o ocurre un error.
     *
     * @param uid UID del usuario.
     * @return [Flow] que emite el perfil actualizado del usuario o `null`.
     */
    fun observeProfile(uid: String): Flow<User?> = callbackFlow {
        val reg = users().document(uid).addSnapshotListener { snap, err ->
            if (err != null || snap == null || !snap.exists()) {
                trySend(null)
                return@addSnapshotListener
            }

            val roleRaw = snap.getString("role") ?: UserRole.USER.name
            val profile = User(
                uid = uid,
                username = snap.getString("username").orEmpty(),
                email = snap.getString("email").orEmpty(),
                photo = snap.getString("photo"),
                role = runCatching { UserRole.valueOf(roleRaw) }
                    .getOrElse { UserRole.USER },
                phone = snap.getString("phone").orEmpty()
            )
            trySend(profile)
        }
        awaitClose { reg.remove() }
    }

    /**
     * Actualiza los datos básicos del perfil de usuario.
     *
     * Campos actualizados:
     * - username
     * - photo
     * - updatedAt
     *
     * @param uid UID del usuario.
     * @param username Nuevo nombre de usuario.
     * @param photo URL de la foto de perfil (puede ser null).
     */
    suspend fun updateProfile(uid: String, username: String, photo: String?) {
        db.collection("users")
            .document(uid)
            .set(
                mapOf(
                    "username" to username,
                    "photo" to photo,
                    "updatedAt" to FieldValue.serverTimestamp()
                ),
                SetOptions.merge()
            )
            .await()
    }
}