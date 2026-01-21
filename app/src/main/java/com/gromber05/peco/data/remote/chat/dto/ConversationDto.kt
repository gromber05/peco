package com.gromber05.peco.data.remote.chat.dto


data class ConversationDto(
    val animalId: Int? = null,
    val userId: String = "",
    val volunteerId: String? = null,
    val status: String = "OPEN",
    val lastMessage: String? = null,
    val updatedAt: Long = 0L
)