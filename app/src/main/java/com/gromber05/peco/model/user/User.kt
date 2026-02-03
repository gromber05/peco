package com.gromber05.peco.model.user

data class User(
    val uid: String,
    val username: String,
    val email: String,
    val photo: String?,
    val role: UserRole,
    val phone: String
)