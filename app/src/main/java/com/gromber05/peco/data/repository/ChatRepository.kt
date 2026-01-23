package com.gromber05.peco.data.repository

import com.gromber05.peco.data.local.conversation.ConversationDao
import com.gromber05.peco.data.local.conversation.ConversationEntity
import com.gromber05.peco.data.local.message.MessageDao
import com.gromber05.peco.data.local.message.MessageEntity
import jakarta.inject.Inject

class ChatRepository @Inject constructor(
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao
) {
    suspend fun getOrCreateConversationId(meId: Int, otherId: Int): Int {
        val existing = conversationDao.findBetweenUsers(meId, otherId)
        if (existing != null) return existing.id

        val newId = conversationDao.insert(
            ConversationEntity(userAId = meId, userBId = otherId)
        )
        return newId.toInt()
    }

    fun observeMessages(conversationId: Int) =
        messageDao.observeMessages(conversationId)

    suspend fun sendMessage(conversationId: Int, senderId: Int, text: String) {
        messageDao.insert(
            MessageEntity(
                conversationId = conversationId,
                senderId = senderId,
                text = text
            )
        )
    }
}

