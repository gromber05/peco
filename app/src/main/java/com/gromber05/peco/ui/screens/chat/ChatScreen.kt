package com.gromber05.peco.ui.screens.chat

import androidx.compose.runtime.Composable

@Composable
fun ChatScreen(
    conversationId: String,
    onBack: () -> Unit
) {
    androidx.compose.material3.Text("Chat: $conversationId")
}
