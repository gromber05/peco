package com.gromber05.peco.data.remote

import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DataSource encargado de gestionar el almacenamiento de archivos en Firebase Storage.
 *
 * Responsabilidades:
 * - Subir imágenes de animales y devolver su URL pública.
 * - Subir avatares de usuario y devolver su URL pública.
 *
 * Arquitectura:
 * - Pertenece a la capa data/remote.
 * - Encapsula el acceso directo a FirebaseStorage para desacoplar
 *   la lógica de almacenamiento del resto de la aplicación.
 *
 * Consideraciones:
 * - Las subidas se realizan de forma asíncrona usando corrutinas y `await()`.
 * - Los nombres de archivo incluyen un timestamp para evitar colisiones.
 */
@Singleton
class StorageDataSource @Inject constructor(
    /** Instancia singleton de FirebaseStorage inyectada mediante Hilt. */
    private val storage: FirebaseStorage
) {

    /**
     * Sube la foto principal de un animal a Firebase Storage.
     *
     * Ruta generada:
     * - animals/{animalId}/main_{timestamp}.jpg
     *
     * Flujo:
     * 1) Se crea una referencia única usando el ID del animal y la fecha actual.
     * 2) Se suben los bytes de la imagen.
     * 3) Se obtiene y devuelve la URL pública de descarga.
     *
     * En caso de error:
     * - Se registra el error en Logcat.
     * - Se relanza la excepción para que capas superiores puedan manejarla.
     *
     * @param animalId ID del animal al que pertenece la foto.
     * @param bytes Contenido de la imagen en formato ByteArray.
     * @return URL pública de la imagen subida.
     *
     * @throws Exception si la subida falla.
     */
    suspend fun uploadAnimalPhoto(animalId: String, bytes: ByteArray): String {
        val ref = storage.reference
            .child("animals")
            .child(animalId)
            .child("main_${System.currentTimeMillis()}.jpg")

        try {
            ref.putBytes(bytes).await()
            return ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            android.util.Log.e("STORAGE", "Upload failed animalId=$animalId", e)
            throw e
        }
    }

    /**
     * Sube el avatar de un usuario a Firebase Storage.
     *
     * Ruta generada:
     * - users/{uid}/avatar_{timestamp}.jpg
     *
     * Flujo:
     * 1) Se crea una referencia única basada en el UID del usuario.
     * 2) Se suben los bytes de la imagen.
     * 3) Se devuelve la URL pública de descarga.
     *
     * @param uid Identificador único del usuario.
     * @param bytes Contenido de la imagen del avatar.
     * @return URL pública del avatar subido.
     *
     * @throws Exception si la subida falla.
     */
    suspend fun uploadUserAvatar(uid: String, bytes: ByteArray): String {
        val ref = storage.reference
            .child("users")
            .child(uid)
            .child("avatar_${System.currentTimeMillis()}.jpg")

        ref.putBytes(bytes).await()
        return ref.downloadUrl.await().toString()
    }

}
