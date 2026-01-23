package com.gromber05.peco.model.data.chat

data class Message(
    val id: Int,
    val conversationId: Int,
    val senderId: Int,
    val text: String,
    val createdAt: Long
)