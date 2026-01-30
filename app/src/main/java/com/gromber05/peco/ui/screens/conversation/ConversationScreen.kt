package com.gromber05.peco.ui.screens.conversation

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.gromber05.peco.ui.components.ConversationItem

@Composable
fun ConversationsScreen(
    onBack: () -> Unit,
    onOpenChat: (String) -> Unit,
    viewModel: ConversationViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    when {
        state.isLoading -> {
            CircularProgressIndicator()
        }

        state.error != null -> {
            Text("Error: ${state.error}")
        }

        state.conversations.isEmpty() -> {
            Text("No tienes conversaciones todavía")
        }

        else -> {
            LazyColumn {
                items(state.conversations) { conversation ->
                    ConversationItem(
                        conversation = conversation,
                        onClick = {
                            viewModel.onConversationClicked(conversation, onOpenChat)
                        }
                    )
                }
            }
        }
    }
}
