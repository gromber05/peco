package com.gromber05.peco.data.repository

import com.gromber05.peco.data.remote.UsersFirestoreDataSource
import com.gromber05.peco.model.user.User
import com.gromber05.peco.model.user.UserRole
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val db: UsersFirestoreDataSource
) {
    suspend fun createProfile(uid: String, username: String, email: String, role: UserRole = UserRole.USER) = db.createProfile(uid, username, email, role)
    suspend fun getProfileOnce(uid: String): User? = db.getProfileOnce(uid)
    fun observeProfile(uid: String): Flow<User?> = db.observeProfile(uid)
    suspend fun updateProfile(uid: String, username: String, photo: String?) = db.updateProfile(uid, username, photo)

}