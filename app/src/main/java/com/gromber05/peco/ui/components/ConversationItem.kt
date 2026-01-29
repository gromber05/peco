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
import com.gromber05.peco.utils.formatTimestamp

@Composable
fun ConversationItem(
    conv: Conversation,
    currentUserId: String,
    onClick: () -> Unit
) {
    val isMine = conv.lastSenderId == currentUserId

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = conv.lastMessage.ifBlank { "Sin mensajes todavía" },
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = buildString {
                    append(if (isMine) "Tú: " else "Ellos: ")
                    append(formatTimestamp(conv.lastMessageAt))
                },
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

