package com.gromber05.peco.ui.screens.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gromber05.peco.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val conversationId: String =
        savedStateHandle.get<String>("conversationId").orEmpty()

    init {
        require(conversationId.isNotBlank()) { "Falta conversationId en navegación" }
    }

    val messages = chatRepository.observeMessages(conversationId)

    fun send(myUid: String, text: String) = viewModelScope.launch {
        chatRepository.sendMessage(conversationId, myUid, text)
    }
}
