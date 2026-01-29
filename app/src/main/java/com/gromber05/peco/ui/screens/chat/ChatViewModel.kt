package com.gromber05.peco.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gromber05.peco.data.repository.ChatRepository
import com.gromber05.peco.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repo: ChatRepository,
    private val session: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState

    fun messagesFlow(conversationId: String) =
        repo.observeMessages(conversationId)

    fun send(conversationId: String, myUid: String, text: String) {
        viewModelScope.launch {
            repo.sendMessage(conversationId, myUid, text)
        }
    }
}
