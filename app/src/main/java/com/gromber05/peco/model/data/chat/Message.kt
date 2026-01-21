package com.gromber05.peco.model.data.chat

data class Message(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val text: String,
    val createdAt: Long = System.currentTimeMillis()
)