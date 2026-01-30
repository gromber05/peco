package com.gromber05.peco.model.data.chat

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class ChatMessage(
    @DocumentId val id: String = "",
    val senderId: String = "",
    val text: String = "",
    val type: String = "text",
    val createdAt: Timestamp? = null
)