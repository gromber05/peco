package com.gromber05.peco.data.local.message

import com.gromber05.peco.model.data.chat.Message

fun Message.toEntity(): MessageEntity =
    MessageEntity(
        id = id,
        conversationId = conversationId,
        senderId = senderId,
        text = text,
        createdAt = createdAt
    )


fun MessageEntity.toDomain(): Message =
    Message(
        id = id,
        conversationId = conversationId,
        senderId = senderId,
        text = text,
        createdAt = createdAt
    )
