package com.gromber05.peco.model.user

data class User(
    val id: Int,
    val username: String,
    val password: String,
    val email: String,
    val photo: String? = null,
    val role: UserRole
)