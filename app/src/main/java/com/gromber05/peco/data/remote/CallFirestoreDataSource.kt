package com.gromber05.peco.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.gromber05.peco.model.call.CallDoc
import com.gromber05.peco.model.call.IceDoc
import com.gromber05.peco.model.call.SdpDoc
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CallFirestoreDataSource(
    private val db: FirebaseFirestore
) {
    private fun calls() = db.collection("calls")

    fun callId(uid1: String, uid2: String): String =
        listOf(uid1, uid2).sorted().joinToString("_")

    suspend fun createOrSetRinging(callId: String, fromUid: String, toUid: String) {
        val doc = CallDoc(callId = callId, fromUid = fromUid, toUid = toUid, status = "ringing")
        calls().document(callId).set(doc).await()
    }

    suspend fun setStatus(callId: String, status: String) {
        calls().document(callId).update("status", status).await()
    }

    suspend fun writeOffer(callId: String, sdp: String) {
        calls().document(callId).collection("offers").document("offer")
            .set(SdpDoc(type = "offer", sdp = sdp)).await()
    }

    suspend fun writeAnswer(callId: String, sdp: String) {
        calls().document(callId).collection("answers").document("answer")
            .set(SdpDoc(type = "answer", sdp = sdp)).await()
    }

    suspend fun addCandidate(callId: String, who: String, ice: IceDoc) {
        calls().document(callId).collection("candidates")
            .document("${who}_${System.currentTimeMillis()}")
            .set(ice).await()
    }

    fun observeCall(callId: String): Flow<CallDoc?> = callbackFlow {
        val reg = calls().document(callId).addSnapshotListener { snap, _ ->
            trySend(snap?.toObject(CallDoc::class.java))
        }
        awaitClose { reg.remove() }
    }

    fun observeOffer(callId: String): Flow<SdpDoc?> = callbackFlow {
        val reg = calls().document(callId).collection("offers").document("offer")
            .addSnapshotListener { snap, _ ->
                trySend(snap?.toObject(SdpDoc::class.java))
            }
        awaitClose { reg.remove() }
    }

    fun observeAnswer(callId: String): Flow<SdpDoc?> = callbackFlow {
        val reg = calls().document(callId).collection("answers").document("answer")
            .addSnapshotListener { snap, _ ->
                trySend(snap?.toObject(SdpDoc::class.java))
            }
        awaitClose { reg.remove() }
    }

    fun observeCandidates(callId: String): Flow<List<IceDoc>> = callbackFlow {
        val reg = calls().document(callId).collection("candidates")
            .addSnapshotListener { snap, _ ->
                val list = snap?.documents?.mapNotNull { it.toObject(IceDoc::class.java) }.orEmpty()
                trySend(list)
            }
        awaitClose { reg.remove() }
    }

    suspend fun cleanup(callId: String) {
        calls().document(callId).collection("offers").document("offer").delete().await()
        calls().document(callId).collection("answers").document("answer").delete().await()
        val candSnap = calls().document(callId).collection("candidates").get().await()
        candSnap.documents.forEach { it.reference.delete() }
        calls().document(callId).delete().await()
    }
}
