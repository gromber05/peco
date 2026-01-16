package com.gromber05.peco.data.repository

import com.gromber05.peco.data.local.user.UserDao
import com.gromber05.peco.data.local.user.UserEntity
import com.gromber05.peco.data.session.AppPreferences
import com.gromber05.peco.model.data.User
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

    suspend fun saveSession(email: String, isAdmin: Boolean) { appPrefs.saveSession(email, isAdmin)}


    suspend fun login(email: String, password: String): Boolean {
        val user = userDao.getUserByEmail(email)

        if (user == null) return false

        appPrefs.saveSession(user.email, user.isAdmin)
        return true
    }

    suspend fun logout() {
        _currentUser.emit(null)
        appPrefs.clearSession()
    }
}