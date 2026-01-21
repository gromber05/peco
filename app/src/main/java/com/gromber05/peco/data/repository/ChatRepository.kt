package com.gromber05.peco.data.repository

import com.gromber05.peco.model.data.chat.Conversation
import com.gromber05.peco.model.data.chat.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun observeMyConversations(myUid: String, isVolunteer: Boolean): Flow<List<Conversation>>
    fun observeMessages(conversationId: String): Flow<List<Message>>

    suspend fun createOrGetConversation(animalId: Int?, userId: String): Conversation
    suspend fun sendMessage(conversationId: String, senderId: String, text: String)
    suspend fun closeConversation(conversationId: String)
}
