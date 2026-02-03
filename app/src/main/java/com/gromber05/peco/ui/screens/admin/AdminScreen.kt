package com.gromber05.peco.ui.screens.admin


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.gromber05.peco.ui.components.SimpleBarChart

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
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Gestión", style = MaterialTheme.typography.titleLarge)
        Text("Acciones rápidas para administrar la protectora.")

        Button(onClick = onAddAnimal, modifier = Modifier.fillMaxWidth()) {
            Text("➕ Añadir animal")
        }

        OutlinedButton(onClick = onManageAnimals, modifier = Modifier.fillMaxWidth()) {
            Text("📋 Gestionar animales (editar/borrar)")
        }
    }
}

@Composable
private fun AdminReportsTab() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Informes", style = MaterialTheme.typography.titleLarge)

        Button(
            onClick = {
                // TODO: generar informe (CSV/PDF)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generar informe (CSV/PDF)")
        }

        OutlinedButton(
            onClick = {
                // TODO: abrir pantalla filtros informe
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Filtros del informe")
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(Modifier.padding(14.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(6.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}
