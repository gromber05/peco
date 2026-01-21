package com.gromber05.peco.model.data

data class User(
    val id: Int,
    val username: String,
    val password: String,
    val email: String,
    val photo: String? = null,
    val isVolunteer: Boolean = false,
    val isAdmin: Boolean = false
)