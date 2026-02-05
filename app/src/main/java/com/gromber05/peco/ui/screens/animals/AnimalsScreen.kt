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


/**
 * Pantalla que muestra una lista de animales en formato listado vertical.
 *
 * Soporta dos modos principales controlados por [ownAnimals]:
 * - ownAnimals = false: listado genérico (por ejemplo favoritos).
 * - ownAnimals = true: listado de "Mis animales" (voluntario), con opciones extra
 *   como botón flotante para añadir y posibilidad de borrar.
 *
 * Funcionalidades:
 * - Manejo de navegación de vuelta con [BackHandler].
 * - Feedback al usuario con [SnackbarHost].
 * - Confirmación de borrado mediante [AlertDialog].
 * - Estados de carga / vacío / contenido según [uiState].
 *
 * @param modifier Modificador externo para personalizar el layout desde el caller.
 * @param viewModel ViewModel inyectado por Hilt que gestiona estado y acciones.
 * @param onAnimalClick Callback al pulsar sobre un animal (recibe animalId).
 * @param onBack Callback para volver atrás.
 * @param onAddAnimal Callback para añadir un animal (solo se muestra si ownAnimals=true).
 * @param ownAnimals Si `true`, muestra la variante "Mis animales" con acciones de voluntario.
 */
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
    /** Host del Snackbar para mostrar mensajes puntuales (ej. eliminado). */
    val snackbarHostState = remember { SnackbarHostState() }
    /** Scope para lanzar corrutinas desde acciones de UI (snackbar, etc.). */
    val scope = rememberCoroutineScope()

    /** Estado local: ID del animal pendiente de eliminar (si hay diálogo abierto). */
    var animalToDeleteId by remember { mutableStateOf<String?>(null) }
    /** Estado local: nombre del animal pendiente de eliminar (para mostrar en el diálogo). */
    var animalToDeleteName by remember { mutableStateOf<String?>(null) }

    /** Estado de UI del ViewModel (lista, carga, etc.). */
    val uiState by viewModel.uiState.collectAsState()

    /** Título dinámico según el modo de la pantalla. */
    val title = if (ownAnimals) "Mis animales" else "Animales"
    /** Texto para estado vacío, distinto si son mis animales o favoritos/listado. */
    val text1 = if (ownAnimals) "No tienes animales" else "No tienes favoritos"

    /**
     * Al entrar en pantalla, se establece el filtro en el ViewModel.
     * `ownAnimals` define qué lista debe cargar/mostrar.
     */
    LaunchedEffect(Unit) {
        viewModel.setFilter(ownAnimals)
    }

    /**
     * Intercepta el botón de atrás del sistema para ejecutar [onBack].
     */
    BackHandler {
        onBack()
    }

    /**
     * Diálogo de confirmación cuando el usuario intenta borrar un animal.
     * Se muestra únicamente cuando [animalToDeleteId] no es null.
     */
    if (animalToDeleteId != null) {
        AlertDialog(
            onDismissRequest = {
                // Cierra el diálogo limpiando el estado local
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
                        // Captura el ID antes de limpiar estado
                        val id = animalToDeleteId!!
                        animalToDeleteId = null
                        animalToDeleteName = null

                        // Ejecuta borrado en el ViewModel
                        viewModel.deleteAnimal(id)

                        // Muestra feedback al usuario
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
                        // Cancela borrado y cierra diálogo
                        animalToDeleteId = null
                        animalToDeleteName = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }


    /**
     * Scaffold principal de la pantalla.
     * Incluye:
     * - TopAppBar con título y botón de volver (solo si ownAnimals=true).
     * - FloatingActionButton para añadir (solo si ownAnimals=true).
     * - SnackbarHost para mensajes.
     * - Contenido central en un [Box] que cambia según estado.
     */
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
            /**
             * Renderizado según estado:
             * - Cargando: spinner
             * - Vacío: texto
             * - Contenido: LazyColumn con tarjetas por animal
             */
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                uiState.animals.isEmpty() -> {
                    Text(
                        text = text1,
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

                            /**
                             * Contenedor visual de cada animal.
                             * Se delega el contenido y acciones a [AnimalCardHorizontal].
                             */
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
                                        // Navega al detalle del animal
                                        onAnimalClick(animal.uid)
                                    },
                                    onErase = {
                                        // Abre diálogo de confirmación de borrado
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
