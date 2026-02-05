package com.gromber05.peco.ui.screens.admin

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.gromber05.peco.model.AdoptionState
import com.gromber05.peco.model.events.UiEvent
import com.gromber05.peco.ui.components.PhotoPicker

/**
 * Pantalla de administración para la creación de nuevos perfiles de animales.
 * Gestiona el formulario de entrada de datos, la selección de imágenes y la obtención
 * automática de la ubicación mediante GPS.
 *
 * @param onBack Callback para navegar hacia atrás tras cancelar o completar la acción.
 * @param viewModel ViewModel encargado de la lógica de negocio y persistencia.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAddAnimalScreen(
    onBack: () -> Unit,
    viewModel: AdminAddAnimalViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    /**
     * Lanzador de permisos para solicitar acceso a la ubicación.
     * Si se concede, activa la función de auto-rellenado de coordenadas en el ViewModel.
     */
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

    // Solicita los permisos automáticamente al entrar en la pantalla
    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    /**
     * Observador de eventos de un solo disparo (One-shot events).
     * Maneja el feedback visual mediante Toasts y la navegación tras el éxito.
     */
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.Error -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                is UiEvent.Success -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    onBack()
                }
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Indicador de carga durante la subida de datos/imágenes
            if (state.isSaving) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            // --- Formulario de datos básicos ---
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

            // Selector de fotografía con previsualización
            PhotoPicker(
                photoUri = state.photoUri ?: "",
                onPhotoSelected = viewModel::onPhotoSelected
            )

            // Acción manual para refrescar la ubicación GPS
            TextButton(
                onClick = viewModel::autoFillLocationIfNeeded,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Rellenar ubicación automáticamente")
            }

            // Selector de estado (Disponible, Pendiente, etc.)
            AdoptionStateDropdown(
                selected = state.adoptionState,
                onSelected = viewModel::onStateChange
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Botón de guardado final
            Button(
                onClick = { viewModel.save() },
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth()
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

/**
 * Componente interno para la selección del estado de adopción mediante un menú desplegable.
 */
@Composable
private fun AdoptionStateDropdown(
    selected: AdoptionState,
    onSelected: (AdoptionState) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = remember { AdoptionState.entries }

    Column(modifier = Modifier.fillMaxWidth()) {
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