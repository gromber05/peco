package com.gromber05.peco.ui.screens.chat

import com.gromber05.peco.model.data.chat.Conversation

data class ChatUiState(
    val loading: Boolean = true,
    val conversations: List<Conversation> = emptyList(),
    val error: String? = null
)
