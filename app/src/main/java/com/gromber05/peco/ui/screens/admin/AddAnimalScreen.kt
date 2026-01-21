package com.gromber05.peco.ui.screens.admin

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import coil.compose.AsyncImage
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

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                result[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (granted) {
            viewModel.autoFillLocationIfNeeded()
            Toast.makeText(context, "Se ha actualizado la ubicación", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

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

            PhotoPicker(
                photoUri = state.photoUri,
                onPhotoPicked = viewModel::onPhotoUriChange
            )

            TextButton(
                onClick = viewModel::autoFillLocationIfNeeded,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Rellenar ubicación automáticamente")
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

    Column(
        modifier = Modifier.fillMaxWidth(0.9f)
    ){
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

@Composable
private fun PhotoPicker(
    photoUri: String,
    onPhotoPicked: (String) -> Unit
) {
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) onPhotoPicked(uri.toString())
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Foto", style = MaterialTheme.typography.labelLarge)

        Button(
            onClick = { pickImageLauncher.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (photoUri.isBlank()) "Elegir foto" else "Cambiar foto")
        }

        if (photoUri.isNotBlank()) {
            AsyncImage(
                model = photoUri,
                contentDescription = "Foto del animal",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
            TextButton(
                onClick = { onPhotoPicked("") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Quitar foto")
            }
        }
    }
}
