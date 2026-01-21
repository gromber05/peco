package com.gromber05.peco.data.repository

import com.gromber05.peco.data.remote.chat.ChatFirebaseDataSource
import com.gromber05.peco.data.remote.chat.dto.MessageDto
import com.gromber05.peco.data.remote.chat.mapper.toDomain
import com.gromber05.peco.model.data.chat.Conversation
import com.gromber05.peco.model.data.chat.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatRepositoryImpl(
    private val ds: ChatFirebaseDataSource
) : ChatRepository {

    override fun observeMyConversations(myUid: String, isVolunteer: Boolean): Flow<List<Conversation>> {
        val flow = if (isVolunteer) ds.observeConversationsByVolunteer(myUid)
        else ds.observeConversationsByUser(myUid)

        return flow.map { list -> list.map { (id, dto) -> dto.toDomain(id) } }
    }

    override fun observeMessages(conversationId: String): Flow<List<Message>> =
        ds.observeMessages(conversationId).map { list ->
            list.map { (id, dto) -> dto.toDomain(id, conversationId) }
        }

    override suspend fun createOrGetConversation(animalId: Int?, userId: String): Conversation {
        val (id, dto) = ds.createOrGetConversation(animalId, userId)
        return dto.toDomain(id)
    }

    override suspend fun sendMessage(conversationId: String, senderId: String, text: String) {
        val now = System.currentTimeMillis()
        ds.sendMessage(conversationId, MessageDto(senderId = senderId, text = text, createdAt = now))
    }

    override suspend fun closeConversation(conversationId: String) {
        ds.closeConversation(conversationId)
    }
}
