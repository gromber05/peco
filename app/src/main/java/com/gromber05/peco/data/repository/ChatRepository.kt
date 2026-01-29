package com.gromber05.peco.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.gromber05.peco.model.data.chat.ChatMessage
import com.gromber05.peco.model.data.chat.Conversation
import jakarta.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ChatRepository @Inject constructor(
    private val db: FirebaseFirestore
) {
    private fun conversations() = db.collection("conversations")

    suspend fun getOrCreateConversationId(animalId: String, myUid: String, otherUid: String): String {
        val id = conversationIdForAnimal(animalId, myUid, otherUid)
        val ref = conversations().document(id)

        ref.set(
            mapOf(
                "animalId" to animalId,
                "participants" to listOf(myUid, otherUid),
                "participantMap" to mapOf(myUid to true, otherUid to true),
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp(),
                "lastMessage" to "",
                "lastSenderId" to "",
                "lastMessageAt" to FieldValue.serverTimestamp()
            ),
            SetOptions.merge()
        ).await()

        return id
    }

    fun observeConversations(myUid: String): Flow<List<Conversation>> = callbackFlow {
        val reg = conversations()
            .whereEqualTo("participantMap.$myUid", true) // ✅ para que funcione el filtro rápido
            .orderBy("lastMessageAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) { close(err); return@addSnapshotListener }

                val list = snap?.documents.orEmpty().map { d ->
                    Conversation(
                        id = d.id,
                        participants = (d.get("participants") as? List<*>)?.filterIsInstance<String>().orEmpty(),
                        lastMessage = d.getString("lastMessage").orEmpty(),
                        lastSenderId = d.getString("lastSenderId").orEmpty(),
                        lastMessageAt = d.getTimestamp("lastMessageAt")?.toDate()?.time ?: 0L
                    )
                }

                trySend(list)
            }

        awaitClose { reg.remove() }
    }


    fun observeMessages(conversationId: String): Flow<List<ChatMessage>> = callbackFlow {
        val reg = conversations().document(conversationId)
            .collection("messages")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .limitToLast(80)
            .addSnapshotListener { snap, err ->
                if (err != null) { close(err); return@addSnapshotListener }
                val list = snap?.documents.orEmpty().map { d ->
                    ChatMessage(
                        id = d.id,
                        conversationId = conversationId,
                        senderId = d.getString("senderId").orEmpty(),
                        text = d.getString("text").orEmpty(),
                        createdAt = d.getTimestamp("createdAt")?.toDate()?.time ?: 0L
                    )
                }
                trySend(list)
            }

        awaitClose { reg.remove() }
    }

    suspend fun sendMessage(conversationId: String, myUid: String, text: String) {
        val convRef = conversations().document(conversationId)
        val msgRef = convRef.collection("messages").document()

        db.runBatch { batch ->
            batch.set(msgRef, mapOf(
                "senderId" to myUid,
                "text" to text,
                "type" to "text",
                "createdAt" to FieldValue.serverTimestamp()
            ))
            batch.set(convRef, mapOf(
                "lastMessage" to text,
                "lastSenderId" to myUid,
                "lastMessageAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            ), SetOptions.merge())
        }.await()
    }

    private fun conversationIdForAnimal(animalId: String, uid1: String, uid2: String): String {
        val pair = listOf(uid1, uid2).sorted().joinToString("_")
        return "a_${animalId}_$pair"
    }

}
