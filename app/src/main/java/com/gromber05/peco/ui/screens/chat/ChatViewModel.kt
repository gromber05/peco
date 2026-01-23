package com.gromber05.peco.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gromber05.peco.data.local.message.MessageEntity
import com.gromber05.peco.data.local.message.toDomain
import com.gromber05.peco.data.repository.ChatRepository
import com.gromber05.peco.data.repository.UserRepository
import com.gromber05.peco.model.data.chat.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repo: ChatRepository,
    private val session: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState

    fun startChatWith(otherUserId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val meId = session.currentUser.value?.id ?: throw Exception()
            val convoId = repo.getOrCreateConversationId(meId, otherUserId)

            repo.observeMessages(convoId).collect { msgs ->
                _uiState.value = ChatUiState(
                    isLoading = false,
                    conversationId = convoId,
                    messages = msgs.map {it.toDomain()}
                )
            }
        }
    }

    fun send(text: String) {
        val convoId = _uiState.value.conversationId ?: return
        val meId = session.currentUser.value?.id ?: throw Exception("DEBUG »» Error al encontrar la id del usuario")
        viewModelScope.launch {
            repo.sendMessage(convoId, meId, text)
        }
    }
}
