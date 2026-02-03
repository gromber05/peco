package com.gromber05.peco.model.call

data class SdpDoc(
    val type: String = "", // "offer" | "answer"
    val sdp: String = ""
)

