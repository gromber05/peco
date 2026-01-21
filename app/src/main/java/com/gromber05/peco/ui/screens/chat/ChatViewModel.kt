package com.gromber05.peco.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gromber05.peco.data.repository.ChatRepository
import com.gromber05.peco.model.data.chat.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ChatUiState())
    val state: StateFlow<ChatUiState> = _state.asStateFlow()

    fun start(conversationId: String) {
        chatRepository.observeMessages(conversationId)
            .onEach { msgs -> _state.value = _state.value.copy(loading = false, messages = msgs, error = null) }
            .catch { e -> _state.value = _state.value.copy(loading = false, error = e.message) }
            .launchIn(viewModelScope)
    }

    fun send(conversationId: String, myUid: String, text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return

        viewModelScope.launch {
            _state.value = _state.value.copy(sending = true)
            runCatching { chatRepository.sendMessage(conversationId, myUid, trimmed) }
                .onFailure { _state.value = _state.value.copy(error = it.message) }
            _state.value = _state.value.copy(sending = false)
        }
    }

    fun close(conversationId: String) {
        viewModelScope.launch { chatRepository.closeConversation(conversationId) }
    }
}
