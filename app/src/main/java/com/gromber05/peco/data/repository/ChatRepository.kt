package com.gromber05.peco.data.repository

import com.gromber05.peco.data.remote.ChatFirestoreDataSource
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val ds: ChatFirestoreDataSource
) {
    suspend fun getOrCreateConversationId(animalId: String, myUid: String, otherUid: String) =
        ds.getOrCreateConversation(animalId, myUid, otherUid)

    fun observeConversationsForUser(myUid: String) =
        ds.observeConversationsForUser(myUid)

    fun observeMessages(conversationId: String) =
        ds.observeMessages(conversationId)

    suspend fun sendMessage(conversationId: String, myUid: String, text: String) =
        ds.sendMessage(conversationId, myUid, text)
}
