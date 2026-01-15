package com.gromber05.peco.data.repository

import com.gromber05.peco.data.local.user.UserDao
import com.gromber05.peco.data.local.user.UserEntity
import com.gromber05.peco.model.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao
) {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    suspend fun insertUser(user: UserEntity) {
        userDao.insertUser(user)
    }

    suspend fun getUserByEmail(email: String): UserEntity? {
        return userDao.getUserByEmail(email)
    }

    suspend fun setCurrentUser(user: User) {
        _currentUser.emit(user)
    }

    suspend fun logout() {
        _currentUser.emit(null)
    }
}