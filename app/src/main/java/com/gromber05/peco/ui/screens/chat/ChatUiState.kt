package com.gromber05.peco.ui.screens.chat

import com.gromber05.peco.model.data.chat.Message

data class ChatUiState(
    val loading: Boolean = true,
    val messages: List<Message> = emptyList(),
    val sending: Boolean = false,
    val error: String? = null
)