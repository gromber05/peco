package com.gromber05.peco.data.repository

import com.gromber05.peco.data.remote.ChatFirestoreDataSource
import com.gromber05.peco.model.data.chat.ChatMessage
import com.gromber05.peco.model.data.chat.Conversation
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class ChatRepository @Inject constructor(
    private val db: ChatFirestoreDataSource
) {
    suspend fun getOrCreateConversationId(animalId: String, myUid: String, otherUid: String): String = db.getOrCreateConversationId(animalId, myUid, otherUid)
    fun observeConversations(myUid: String): Flow<List<Conversation>> = db.observeConversations(myUid)
    fun observeMessages(conversationId: String): Flow<List<ChatMessage>> = db.observeMessages(conversationId)
    suspend fun sendMessage(conversationId: String, myUid: String, text: String) = db.sendMessage(conversationId, myUid, text)
}
