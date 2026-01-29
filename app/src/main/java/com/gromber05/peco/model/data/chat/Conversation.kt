package com.gromber05.peco.model.data.chat


data class Conversation(
    val id: String,
    val participants: List<String>,
    val lastMessage: String,
    val lastSenderId: String,
    val lastMessageAt: Long
)