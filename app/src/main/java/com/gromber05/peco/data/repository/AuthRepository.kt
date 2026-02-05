package com.gromber05.peco.data.repository

import com.gromber05.peco.data.remote.AuthFirestoreDataSource
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class AuthRepository @Inject constructor(
    private val db: AuthFirestoreDataSource
) {
    fun isLoggedIn(): Boolean = db.isLoggedIn()
    fun currentUidFlow(): Flow<String?> = db.currentUidFlow()
    suspend fun signIn(email: String, password: String) = db.signIn(email, password)
    suspend fun signUp(email: String, password: String): String = db.signUp(email, password)
    suspend fun changePassword(currentPassword: String, newPassword: String) = db.changePassword(currentPassword, newPassword)
    fun signOut() = db.signOut()
    fun isLoggedInFlow(): Flow<Boolean?> = db.isLoggedInFlow()
    fun sendPasswordResetEmail(email: String) = db.sendPasswordResetEmail(email.trim())
}
