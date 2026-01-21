package com.gromber05.peco.data.remote.chat.mapper

import com.gromber05.peco.data.remote.chat.dto.MessageDto
import com.gromber05.peco.model.data.chat.Message

fun MessageDto.toDomain(id: String, conversationId: String): Message =
    Message(
        id = id,
        conversationId = conversationId,
        senderId = senderId,
        text = text,
        createdAt = createdAt
    )

fun Message.toDto(): MessageDto =
    MessageDto(
        senderId = senderId,
        text = text,
        createdAt = createdAt
    )