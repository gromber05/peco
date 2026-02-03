package com.gromber05.peco.model.call

data class CallDoc(
    val callId: String = "",
    val fromUid: String = "",
    val toUid: String = "",
    val status: String = "idle", // ringing | accepted | ended
    val createdAt: Long = System.currentTimeMillis()
)
