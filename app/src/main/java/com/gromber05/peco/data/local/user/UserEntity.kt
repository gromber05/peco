package com.gromber05.peco.data.local.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val email: String,
    val password: String,
    val photo: String? = null,
    val isVolunteer: Boolean = false,
    val isAdmin: Boolean = false
)