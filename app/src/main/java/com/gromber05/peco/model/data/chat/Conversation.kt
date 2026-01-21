package com.gromber05.peco.model.data.chat


data class Conversation(
    val id: String,
    val animalId: Int? = null,
    val userId: String,
    val volunteerId: String? = null,
    val status: ConversationStatus = ConversationStatus.OPEN,
    val lastMessage: String? = null,
    val updatedAt: Long = System.currentTimeMillis()
)