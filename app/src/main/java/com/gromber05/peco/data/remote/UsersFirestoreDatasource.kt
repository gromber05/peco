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

@Singleton
class UsersFirestoreDatasource @Inject constructor(private val db: FirebaseFirestore) {
    private fun users() = db.collection("users")

    suspend fun createProfile(uid: String, username: String, email: String, role: UserRole = UserRole.USER) {
        users().document(uid).set(
            mapOf(
                "username" to username,
                "email" to email,
                "photo" to null,
                "role" to role.name,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            ),
            SetOptions.merge()
        ).await()
    }

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
                .getOrElse { UserRole.USER }

        )
    }

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
                    .getOrElse { UserRole.USER }
            )
            trySend(profile)
        }
        awaitClose { reg.remove() }
    }

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