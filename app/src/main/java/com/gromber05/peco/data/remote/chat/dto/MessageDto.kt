package com.gromber05.peco.data.remote.chat.dto

data class MessageDto(
    val senderId: String = "",
    val text: String = "",
    val createdAt: Long = 0L
)