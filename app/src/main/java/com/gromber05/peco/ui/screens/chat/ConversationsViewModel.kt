package com.gromber05.peco.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gromber05.peco.data.repository.ChatRepository
import com.gromber05.peco.model.data.chat.ChatMessage
import com.gromber05.peco.model.data.chat.Conversation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject


@HiltViewModel
class ConversationsViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ConversationsUiState())
    val state: StateFlow<ConversationsUiState> = _state.asStateFlow()

    fun conversationsFlow(myUid: String) = chatRepository.observeMessages(myUid)

    fun messagesToConversations(messages: List<ChatMessage>): List<Conversation> {
        return messages
            .groupBy { it.conversationId }
            .map { (convId, msgs) ->
                val last = msgs.maxBy { it.createdAt }
                Conversation(
                    id = convId,
                    participants = emptyList(),
                    lastMessage = last.text,
                    lastSenderId = last.senderId,
                    lastMessageAt = last.createdAt
                )
            }
            .sortedByDescending { it.lastMessageAt }
    }

}
