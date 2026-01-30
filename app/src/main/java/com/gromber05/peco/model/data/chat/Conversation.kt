package com.gromber05.peco.model.data.chat

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Conversation(
    @DocumentId val id: String = "",
    val animalId: String = "",
    val participants: List<String> = emptyList(),
    val participantMap: Map<String, Boolean> = emptyMap(),
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
    val lastMessage: String = "",
    val lastSenderId: String = "",
    val lastMessageAt: Timestamp? = null
)