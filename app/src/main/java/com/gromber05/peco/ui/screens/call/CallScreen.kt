package com.gromber05.peco.ui.screens.call

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CallScreen(
    state: CallUiState,
    onMute: () -> Unit,
    onHangUp: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Estado: ${state.status}", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Text("Conexión: ${state.connection}")
        Spacer(Modifier.height(8.dp))
        state.error?.let { Text("Error: $it", color = MaterialTheme.colorScheme.error) }

        Spacer(Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onMute) {
                Text(if (state.muted) "Activar micro" else "Silenciar")
            }
            Button(onClick = onHangUp, colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )) {
                Text("Colgar")
            }
        }
    }
}
