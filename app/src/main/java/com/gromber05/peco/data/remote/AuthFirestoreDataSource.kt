package com.gromber05.peco.data.remote

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthFirestoreDataSource @Inject constructor(
    private val auth: FirebaseAuth
) {
    fun isLoggedIn(): Boolean = auth.currentUser != null
    fun currentUidFlow(): Flow<String?> = callbackFlow {
        trySend(auth.currentUser?.uid)

        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser?.uid)
        }

        auth.addAuthStateListener(listener)

        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }

    fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
    }

    suspend fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun signUp(email: String, password: String): String {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        return result.user!!.uid
    }

    suspend fun changePassword(currentPassword: String, newPassword: String) {
        val user = auth.currentUser ?: throw IllegalStateException("No hay usuario logueado")
        val email = user.email ?: throw IllegalStateException("El usuario no tiene email")

        val credential = EmailAuthProvider.getCredential(email, currentPassword)

        user.reauthenticate(credential).await()
        user.updatePassword(newPassword).await()
    }

    fun signOut() {
        auth.signOut()
    }

    fun isLoggedInFlow(): Flow<Boolean?> =
        currentUidFlow()
            .map { uid -> uid != null }
            .distinctUntilChanged()

}