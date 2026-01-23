package com.gromber05.peco.ui.screens.chat

import com.gromber05.peco.model.data.chat.Message

data class ChatUiState(
    val isLoading: Boolean = true,
    val conversationId: Int? = null,
    val messages: List<Message> = emptyList()
)
