package com.gromber05.peco.model.data.chat

data class ChatMessage(
    val id: String,
    val senderId: String,
    val text: String,
    val createdAt: Long
)