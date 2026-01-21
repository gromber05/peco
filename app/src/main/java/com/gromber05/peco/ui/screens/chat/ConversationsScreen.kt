package com.gromber05.peco.ui.screens.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gromber05.peco.ui.components.ConversationItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationsScreen(
    myUid: String,
    isVolunteer: Boolean,
    onOpenChat: (String) -> Unit,
    onBack: () -> Unit,
    vm: ConversationsViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()

    LaunchedEffect(myUid, isVolunteer) { vm.start(myUid, isVolunteer) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isVolunteer) "Chats asignados" else "Mis chats") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("←") }
                }
            )
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when {
                state.loading -> CircularProgressIndicator(Modifier.padding(24.dp))
                state.error != null -> Text("Error: ${state.error}", Modifier.padding(24.dp))
                state.conversations.isEmpty() -> Text("No hay conversaciones aún.", Modifier.padding(24.dp))
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.conversations) { conv ->
                        ConversationItem(conv = conv) {
                            onOpenChat(conv.id)
                        }
                    }
                }
            }
        }
    }
}
