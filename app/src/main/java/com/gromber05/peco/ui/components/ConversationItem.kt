package com.gromber05.peco.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gromber05.peco.model.data.chat.Conversation

@Composable
fun ConversationItem(
    conv: Conversation,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(
                text = conv.lastMessage ?: "Sin mensajes todavía",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Estado: ${conv.status.name} · animalId: ${conv.animalId ?: "—"}",
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}
