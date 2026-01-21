package com.gromber05.peco.ui.screens.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gromber05.peco.ui.components.MessageBubble

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    conversationId: String,
    myUid: String,
    canClose: Boolean,
    onBack: () -> Unit,
    vm: ChatViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()
    var input by remember { mutableStateOf("") }

    LaunchedEffect(conversationId) { vm.start(conversationId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat") },
                navigationIcon = { IconButton(onClick = onBack) { Text("←") } },
                actions = {
                    if (canClose) {
                        TextButton(onClick = { vm.close(conversationId) }) { Text("Cerrar") }
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            if (state.loading) LinearProgressIndicator(Modifier.fillMaxWidth())

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.messages) { msg ->
                    MessageBubble(msg = msg, isMine = msg.senderId == myUid)
                }
            }

            Divider()

            Row(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Escribe un mensaje…") },
                    singleLine = true
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        vm.send(conversationId, myUid, input)
                        input = ""
                    },
                    enabled = !state.sending
                ) { Text("Enviar") }
            }

            state.error?.let {
                Text(
                    text = "Error: $it",
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
