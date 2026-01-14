package com.gromber05.peco.model

data class User(
    private val id: Int,
    private val username: String,
    private val password: String,
    private val email: String,
    private val photo: String? = null
)
