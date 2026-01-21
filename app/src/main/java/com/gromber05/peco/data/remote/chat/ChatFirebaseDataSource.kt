package com.gromber05.peco.data.remote.chat

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.gromber05.peco.data.remote.chat.dto.ConversationDto
import com.gromber05.peco.data.remote.chat.dto.MessageDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ChatFirebaseDataSource(
    private val db: FirebaseFirestore
) {
    private val conversations = db.collection("conversations")

    fun observeConversationsByUser(userId: String): Flow<List<Pair<String, ConversationDto>>> = callbackFlow {
        val sub = conversations
            .whereEqualTo("userId", userId)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null || snap == null) return@addSnapshotListener
                val list = snap.documents.mapNotNull { doc ->
                    val dto = doc.toObject(ConversationDto::class.java) ?: return@mapNotNull null
                    doc.id to dto
                }
                trySend(list)
            }
        awaitClose { sub.remove() }
    }

    fun observeConversationsByVolunteer(volunteerId: String): Flow<List<Pair<String, ConversationDto>>> = callbackFlow {
        val sub = conversations
            .whereEqualTo("volunteerId", volunteerId)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null || snap == null) return@addSnapshotListener
                val list = snap.documents.mapNotNull { doc ->
                    val dto = doc.toObject(ConversationDto::class.java) ?: return@mapNotNull null
                    doc.id to dto
                }
                trySend(list)
            }
        awaitClose { sub.remove() }
    }

    fun observeMessages(conversationId: String): Flow<List<Pair<String, MessageDto>>> = callbackFlow {
        val sub = conversations.document(conversationId)
            .collection("messages")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null || snap == null) return@addSnapshotListener
                val list = snap.documents.mapNotNull { doc ->
                    val dto = doc.toObject(MessageDto::class.java) ?: return@mapNotNull null
                    doc.id to dto
                }
                trySend(list)
            }
        awaitClose { sub.remove() }
    }

    suspend fun createOrGetConversation(animalId: Int?, userId: String): Pair<String, ConversationDto> {
        val q = conversations
            .whereEqualTo("userId", userId)
            .whereEqualTo("animalId", animalId)
            .whereEqualTo("status", "OPEN")
            .get().await()

        val existing = q.documents.firstOrNull()
        if (existing != null) {
            val dto = existing.toObject(ConversationDto::class.java)
                ?: ConversationDto(userId = userId, animalId = animalId)
            return existing.id to dto
        }

        val now = System.currentTimeMillis()
        val newDto = ConversationDto(
            animalId = animalId,
            userId = userId,
            volunteerId = null,
            status = "OPEN",
            lastMessage = null,
            updatedAt = now
        )

        val docRef = conversations.add(newDto).await()
        return docRef.id to newDto
    }

    suspend fun sendMessage(conversationId: String, msg: MessageDto) {
        val convRef = conversations.document(conversationId)
        convRef.collection("messages").add(msg).await()
        convRef.update(
            mapOf(
                "lastMessage" to msg.text,
                "updatedAt" to msg.createdAt
            )
        ).await()
    }

    suspend fun closeConversation(conversationId: String) {
        conversations.document(conversationId).update("status", "CLOSED").await()
    }
}