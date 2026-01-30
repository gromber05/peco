package com.gromber05.peco.ui.screens.conversation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.gromber05.peco.data.repository.ChatRepository
import com.gromber05.peco.model.data.chat.Conversation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val myUid: String? = auth.currentUser?.uid

    private val _uiState = MutableStateFlow(ConversationUiState())
    val uiState: StateFlow<ConversationUiState> = _uiState.asStateFlow()

    init {
        if (myUid == null) {
            _uiState.value = ConversationUiState(
                isLoading = false,
                error = "Usuario no autenticado"
            )
        } else {
            observeConversations(myUid)
        }
    }

    private fun observeConversations(uid: String) {
        chatRepository.observeConversationsForUser(uid)
            .onEach { list ->
                _uiState.value = ConversationUiState(
                    isLoading = false,
                    conversations = list
                )
            }
            .catch { e ->
                _uiState.value = ConversationUiState(
                    isLoading = false,
                    error = e.message ?: "Error cargando conversaciones"
                )
            }
            .launchIn(viewModelScope)
    }

    /**
     * Devuelve el conversationId al pulsar una conversación
     * (simple helper para la UI)
     */
    fun onConversationClicked(
        conversation: Conversation,
        onNavigate: (String) -> Unit
    ) {
        if (conversation.id.isBlank()) {
            _uiState.update {
                it.copy(error = "ConversationId inválido")
            }
            return
        }
        onNavigate(conversation.id)
    }

    /**
     * Limpia el error una vez mostrado (Snackbar, Toast, etc.)
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
