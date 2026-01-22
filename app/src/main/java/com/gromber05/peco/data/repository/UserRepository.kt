package com.gromber05.peco.data.repository

import com.gromber05.peco.data.local.user.UserDao
import com.gromber05.peco.data.local.user.UserEntity
import com.gromber05.peco.data.local.user.toUser
import com.gromber05.peco.data.session.AppPreferences
import com.gromber05.peco.model.user.User
import com.gromber05.peco.model.user.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val appPrefs: AppPreferences
) {

    val isLoggedIn = appPrefs.isLoggedIn
    val isAdmin = appPrefs.isAdmin
    val sessionEmail = appPrefs.email

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

    suspend fun saveSession(email: String, role: UserRole) { appPrefs.saveSession(email, role)}


    suspend fun login(email: String, password: String): Boolean {
        val user = userDao.getUserByEmail(email) ?: return false

        appPrefs.saveSession(user.email, user.role)
        return true
    }

    suspend fun logout() {
        _currentUser.emit(null)
        appPrefs.clearSession()
    }

    suspend fun updateUser(user: UserEntity)  {
        userDao.updateUser(user)
    }

    suspend fun refreshCurrentUserFromEmail(email: String) {
        val entity = getUserByEmail(email) ?: return
        setCurrentUser(entity.toUser())
    }
}