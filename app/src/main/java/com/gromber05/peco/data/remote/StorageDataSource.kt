package com.gromber05.peco.data.remote

import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageDataSource @Inject constructor(
    private val storage: FirebaseStorage
) {
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


    suspend fun uploadUserAvatar(uid: String, bytes: ByteArray): String {
        val ref = storage.reference
            .child("users")
            .child(uid)
            .child("avatar_${System.currentTimeMillis()}.jpg")

        ref.putBytes(bytes).await()
        return ref.downloadUrl.await().toString()
    }

}
