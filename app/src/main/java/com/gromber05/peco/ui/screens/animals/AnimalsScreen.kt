package com.gromber05.peco.ui.screens.animals

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.gromber05.peco.ui.components.AnimalCardHorizontal
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalsScreen(
    modifier: Modifier = Modifier,
    viewModel: AnimalsViewModel = hiltViewModel(),
    onAnimalClick: (String) -> Unit,
    onBack: () -> Unit,
    onAddAnimal: () -> Unit = {},
    ownAnimals: Boolean = false
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var animalToDeleteId by remember { mutableStateOf<String?>(null) }
    var animalToDeleteName by remember { mutableStateOf<String?>(null) }

    val uiState by viewModel.uiState.collectAsState()
    val title = if (ownAnimals) "Mis animales" else "Animales"
    val texto1 = if (ownAnimals) "No tienes animales" else "No tienes favoritos"

    LaunchedEffect(Unit) {
        viewModel.setFilter(ownAnimals)
    }

    BackHandler {
        onBack()
    }

    if (animalToDeleteId != null) {
        AlertDialog(
            onDismissRequest = {
                animalToDeleteId = null
                animalToDeleteName = null
            },
            title = { Text("Eliminar animal") },
            text = {
                Text("¿Seguro que quieres eliminar ${animalToDeleteName ?: "este animal"}? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val id = animalToDeleteId!!
                        animalToDeleteId = null
                        animalToDeleteName = null

                        viewModel.deleteAnimal(id)

                        scope.launch {
                            snackbarHostState.showSnackbar("Animal eliminado")
                        }
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        animalToDeleteId = null
                        animalToDeleteName = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = title)
                },
                navigationIcon = {
                    if (ownAnimals) {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver atrás"
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (ownAnimals) {
                ExtendedFloatingActionButton(
                    onClick = onAddAnimal,
                    icon = {
                        Icon(Icons.Default.Edit, contentDescription = null)
                    },
                    text = {
                        Text("Añadir animal")
                    }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                uiState.animals.isEmpty() -> {
                    Text(
                        text = texto1,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.animals, key = { it.uid }) { animal ->

                            Card(
                                modifier = Modifier.fillMaxSize(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                AnimalCardHorizontal(
                                    animal = animal,
                                    onClick = {
                                        onAnimalClick(animal.uid)
                                    },
                                    onErase = {
                                        animalToDeleteId = animal.uid
                                        animalToDeleteName = animal.name
                                    }

                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
