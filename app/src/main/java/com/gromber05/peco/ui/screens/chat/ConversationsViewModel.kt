package com.gromber05.peco.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gromber05.peco.data.repository.ChatRepository
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

    fun start(myUid: String, isVolunteer: Boolean) {
        chatRepository.observeMyConversations(myUid, isVolunteer)
            .onEach { list -> _state.value = ConversationsUiState(loading = false, conversations = list) }
            .catch { e -> _state.value = ConversationsUiState(loading = false, error = e.message) }
            .launchIn(viewModelScope)
    }
}
