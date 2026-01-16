package com.gromber05.peco.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.gromber05.peco.model.AdoptionState
import com.gromber05.peco.model.events.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAddAnimalScreen(
    onBack: () -> Unit,
    viewModel: AdminAddAnimalViewModel = hiltViewModel(),
    modifier: Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.Error -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                is UiEvent.Success -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                UiEvent.LoggedOut -> {}
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Añadir animal") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (state.isSaving) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = state.species,
                onValueChange = viewModel::onSpeciesChange,
                label = { Text("Especie (Perro/Gato...)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = state.dob,
                onValueChange = viewModel::onDobChange,
                label = { Text("Nacimiento (YYYY-MM-DD o texto)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = state.photo,
                onValueChange = viewModel::onPhotoChange,
                label = { Text("Foto (URL opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = state.latitude,
                    onValueChange = viewModel::onLatChange,
                    label = { Text("Latitud") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = state.longitude,
                    onValueChange = viewModel::onLonChange,
                    label = { Text("Longitud") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            AdoptionStateDropdown(
                selected = state.adoptionState,
                onSelected = viewModel::onStateChange
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = viewModel::save,
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Guardar animal")
            }

            TextButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar")
            }
        }
    }
}

@Composable
private fun AdoptionStateDropdown(
    selected: AdoptionState,
    onSelected: (AdoptionState) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = remember { AdoptionState.entries }

    Column {
        Text(
            text = "Estado de adopción",
            style = MaterialTheme.typography.labelLarge
        )
        Spacer(Modifier.height(6.dp))
        Button(onClick = { expanded = true }) {
            Text(selected.value)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { state ->
                DropdownMenuItem(
                    text = { Text(state.value) },
                    onClick = {
                        onSelected(state)
                        expanded = false
                    }
                )
            }
        }
    }
}
