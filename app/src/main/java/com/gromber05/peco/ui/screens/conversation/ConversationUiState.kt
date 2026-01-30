package com.gromber05.peco.ui.screens.conversation

import com.gromber05.peco.model.data.chat.Conversation

data class ConversationUiState(
    val isLoading: Boolean = true,
    val conversations: List<Conversation> = emptyList(),
    val error: String? = null
)