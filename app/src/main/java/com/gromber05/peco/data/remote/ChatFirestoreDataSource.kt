package com.gromber05.peco.data.remote

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.gromber05.peco.model.data.chat.ChatMessage
import com.gromber05.peco.model.data.chat.Conversation
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatFirestoreDataSource @Inject constructor(
    private val db: FirebaseFirestore
) {
    private fun conversations() = db.collection("conversations")

    fun conversationId(animalId: String, uid1: String, uid2: String): String {
        val pair = listOf(uid1, uid2).sorted().joinToString("_")
        return "a_${animalId}_$pair"
    }

    suspend fun getOrCreateConversation(animalId: String, myUid: String, otherUid: String): String {
        require(animalId.isNotBlank())
        require(myUid.isNotBlank())
        require(otherUid.isNotBlank())
        require(myUid != otherUid)

        val id = conversationId(animalId, myUid, otherUid)
        val ref = conversations().document(id)

        val snap = ref.get().await()
        if (snap.exists()) return id

        val data = mapOf(
            "animalId" to animalId,
            "participants" to listOf(myUid, otherUid),
            "participantMap" to mapOf(myUid to true, otherUid to true),
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp(),
            "lastMessage" to "",
            "lastSenderId" to "",
            "lastMessageAt" to FieldValue.serverTimestamp()
        )

        ref.set(data, SetOptions.merge()).await()
        return id
    }
    fun observeConversationsForUser(myUid: String): Flow<List<Conversation>> = callbackFlow {
        require(myUid.isNotBlank())

        val reg = conversations()
            .whereEqualTo("participantMap.$myUid", true)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) { close(err); return@addSnapshotListener }
                val list = snap?.documents.orEmpty().mapNotNull { it.toObject(Conversation::class.java) }
                trySend(list)
            }

        awaitClose { reg.remove() }
    }

    fun observeMessages(conversationId: String): Flow<List<ChatMessage>> = callbackFlow {
        val id = conversationId.trim()
        require(id.isNotBlank()) { "conversationId vacío" }
        require(!id.contains("/")) { "conversationId no debe contener '/': $id" }
        require(id != "conversations") { "conversationId inválido: $id" }

        val reg = conversations()
            .document(id)
            .collection("messages")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .limitToLast(80)
            .addSnapshotListener { snap, err ->
                if (err != null) { close(err); return@addSnapshotListener }
                val list = snap?.documents.orEmpty().mapNotNull { it.toObject(ChatMessage::class.java) }
                trySend(list)
            }

        awaitClose { reg.remove() }
    }

    suspend fun sendMessage(conversationId: String, myUid: String, text: String) {
        val id = conversationId.trim()
        val msg = text.trim()

        require(id.isNotBlank())
        require(myUid.isNotBlank())
        require(msg.isNotBlank())

        val convRef = conversations().document(id)
        val msgRef = convRef.collection("messages").document()

        db.runBatch { batch ->
            batch.set(msgRef, mapOf(
                "senderId" to myUid,
                "text" to msg,
                "type" to "text",
                "createdAt" to FieldValue.serverTimestamp()
            ))

            batch.set(convRef, mapOf(
                "lastMessage" to msg,
                "lastSenderId" to myUid,
                "lastMessageAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            ), SetOptions.merge())
        }.await()
    }
}
