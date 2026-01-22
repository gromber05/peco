package com.gromber05.peco.data.local.user

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gromber05.peco.model.user.UserRole

@Entity(tableName = "users")
data class UserEntity (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val email: String,
    val password: String,
    val photo: String? = null,
    val role: UserRole = UserRole.USER
)