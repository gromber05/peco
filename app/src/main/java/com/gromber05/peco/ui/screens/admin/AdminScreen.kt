package com.gromber05.peco.ui.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.gromber05.peco.ui.components.SimpleBarChart
import com.gromber05.peco.ui.components.StatCard
import com.gromber05.peco.ui.screens.animals.AnimalsViewModel
import com.gromber05.peco.utils.generatePdf
import com.gromber05.peco.utils.openPdf
import com.gromber05.peco.utils.sharePdf
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    onBack: () -> Unit,
    onAddAnimal: () -> Unit,
    onManageAnimals: () -> Unit,
    modifier: Modifier
) {
    var tab by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier,
    ) { padding ->
        Column(Modifier.padding(padding)) {

            TabRow(selectedTabIndex = tab) {
                Tab(
                    selected = tab == 0,
                    onClick = { tab = 0 },
                    text = { Text("Stats") },
                    icon = { Icon(Icons.Filled.Assessment, contentDescription = null) }
                )
                Tab(
                    selected = tab == 1,
                    onClick = { tab = 1 },
                    text = { Text("Gestión") },
                    icon = { Icon(Icons.Filled.Pets, contentDescription = null) }
                )
                Tab(
                    selected = tab == 2,
                    onClick = { tab = 2 },
                    text = { Text("Informes") },
                    icon = { Icon(Icons.Filled.Description, contentDescription = null) }
                )
            }

            when (tab) {
                0 -> AdminDashboardTab()
                1 -> AdminManagementTab(
                    onAddAnimal = onAddAnimal,
                    onManageAnimals = onManageAnimals
                )
                2 -> AdminReportsTab()
            }
        }
    }
}

@Composable
private fun AdminDashboardTab(
    viewModel: AdminViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.observeStats()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
        ,
    ) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                StatCard("Animales", state.totalAnimals.toString(), modifier = Modifier.weight(1f))
                StatCard("Likes", state.likes.toString(), modifier = Modifier.weight(1f))
                StatCard("Dislikes", state.dislikes.toString(), modifier = Modifier.weight(1f))
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                StatCard("Disponibles", state.available.toString(), modifier = Modifier.weight(1f))
                StatCard("Adoptados", state.adopted.toString(), modifier = Modifier.weight(1f))
                StatCard("Pendientes", state.pending.toString(), modifier = Modifier.weight(1f))
            }
        }

        item {
            Text("Animales por especie", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            SimpleBarChart(data = state.bySpecies.take(6))
        }

        item {
            Text("Top especies con más likes", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            SimpleBarChart(data = state.topLikedSpecies)
        }
    }
}

@Composable
private fun AdminManagementTab(
    onAddAnimal: () -> Unit,
    onManageAnimals: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Gestión", style = MaterialTheme.typography.titleLarge)
        Text("Acciones rápidas para administrar la protectora.")

        Button(onClick = onAddAnimal, modifier = Modifier.fillMaxWidth()) {
            Text("➕ Añadir animal")
        }

        /*
        * OutlinedButton(onClick = onManageAnimals, modifier = Modifier.fillMaxWidth()) {
        Text("📋 Gestionar animales (editar/borrar)")
        }*/
    }
}

@Composable
private fun AdminReportsTab(
    animalsViewModel: AnimalsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isGenerating by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var lastFile by remember { mutableStateOf<File?>(null) }

    var showFilters by remember { mutableStateOf(false) }

    var onlyMyAnimals by remember { mutableStateOf(false) }
    var onlyFavorites by remember { mutableStateOf(false) }
    var onlyAdopted by remember { mutableStateOf(false) }

    if (showFilters) {
        AlertDialog(
            onDismissRequest = { showFilters = false },
            title = { Text("Filtros del informe") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = onlyMyAnimals,
                            onCheckedChange = { onlyMyAnimals = it }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Solo mis animales (voluntario)")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = onlyFavorites,
                            onCheckedChange = { onlyFavorites = it }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Solo favoritos")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = onlyAdopted,
                            onCheckedChange = { onlyAdopted = it }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Solo adoptados")
                    }

                    Text(
                        text = "Puedes aplicar estos filtros antes de generar el PDF.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showFilters = false
                    }
                ) {
                    Text("Aplicar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showFilters = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Informes", style = MaterialTheme.typography.titleLarge)

        Button(
            onClick = {
                scope.launch {
                    isGenerating = true
                    error = null
                    try {
                        val animals = animalsViewModel.getAllAnimalsOnce()
                        val file = generatePdf(context, animals)
                        lastFile = file
                    } catch (e: Exception) {
                        error = e.message ?: "Error generando el informe"
                    } finally {
                        isGenerating = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isGenerating
        ) {
            if (isGenerating) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(8.dp))
                Text("Generando…")
            } else {
                Text("Generar informe (PDF)")
            }
        }

        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        lastFile?.let { file ->
            OutlinedButton(
                onClick = { openPdf(context, file) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Abrir último PDF")
            }

            OutlinedButton(
                onClick = { sharePdf(context, file) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Compartir PDF")
            }
        }

        OutlinedButton(
            onClick = { showFilters = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Filtros del informe")
        }
    }
}