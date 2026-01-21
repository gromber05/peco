package com.gromber05.peco.data.remote.chat.mapper

import com.gromber05.peco.data.remote.chat.dto.ConversationDto
import com.gromber05.peco.model.data.chat.Conversation
import com.gromber05.peco.model.data.chat.ConversationStatus

fun ConversationDto.toDomain(id: String): Conversation {
    val st = runCatching { ConversationStatus.valueOf(status) }
        .getOrDefault(ConversationStatus.OPEN)

    return Conversation(
        id = id,
        animalId = animalId,
        userId = userId,
        volunteerId = volunteerId,
        status = st,
        lastMessage = lastMessage,
        updatedAt = updatedAt
    )
}

fun Conversation.toDto(): ConversationDto = ConversationDto(
    animalId = animalId,
    userId = userId,
    volunteerId = volunteerId,
    status = status.name,
    lastMessage = lastMessage,
    updatedAt = updatedAt
)