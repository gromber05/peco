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
import com.gromber05.peco.model.AdoptionState
import com.gromber05.peco.ui.components.SimpleBarChart
import com.gromber05.peco.ui.components.StatCard
import com.gromber05.peco.ui.screens.animals.AnimalsViewModel
import com.gromber05.peco.ui.screens.home.HomeViewModel
import com.gromber05.peco.utils.generatePdf
import com.gromber05.peco.utils.openPdf
import com.gromber05.peco.utils.sharePdf
import kotlinx.coroutines.launch
import java.io.File

/**
 * Pantalla principal de administración.
 *
 * Esta pantalla organiza el panel de administración en pestañas (tabs) con tres secciones:
 * 1) Stats: métricas y estadísticas (dashboard).
 * 2) Gestión: acciones rápidas para administración (ej. añadir animal).
 * 3) Informes: generación y compartición de informes en PDF.
 *
 * Implementación:
 * - Usa [Scaffold] como contenedor principal.
 * - Usa [TabRow] para navegación interna por pestañas.
 * - Renderiza el contenido de cada pestaña con composables dedicados.
 *
 * @param onBack Callback para volver atrás (actualmente no usado dentro del código).
 * @param onAddAnimal Callback para navegar a la pantalla de creación de animal.
 * @param onManageAnimals Callback para navegar a la gestión avanzada de animales (actualmente comentado).
 * @param modifier Modificador externo para personalizar layout desde el caller.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    onBack: () -> Unit,
    onAddAnimal: () -> Unit,
    onManageAnimals: () -> Unit,
    modifier: Modifier
) {
    /** Índice de pestaña seleccionada: 0=Stats, 1=Gestión, 2=Informes. */
    var tab by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier,
    ) { padding ->
        Column(Modifier.padding(padding)) {

            /**
             * Barra de pestañas del panel de administración.
             * Cada pestaña tiene icono y texto.
             */
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

            /**
             * Contenido de la pestaña seleccionada.
             */
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

/**
 * Pestaña "Stats" del panel de administración.
 *
 * Muestra:
 * - Tarjetas con estadísticas resumidas (animales, likes, dislikes, estados).
 * - Gráficas de barras por especie y ranking de likes por especie.
 *
 * Implementación:
 * - Obtiene [AdminViewModel] con Hilt ([hiltViewModel]).
 * - Observa el estado de UI (StateFlow) con [collectAsState].
 * - Inicia la observación de estadísticas al entrar con [LaunchedEffect].
 *
 * @param viewModel ViewModel inyectado por Hilt (por defecto).
 */
@Composable
private fun AdminDashboardTab(
    viewModel: AdminViewModel = hiltViewModel()
) {
    /** Estado del dashboard (contadores, listas por especie, etc.). */
    val state by viewModel.uiState.collectAsState()

    /**
     * Se lanza una vez al entrar en el composable para arrancar la observación de stats.
     */
    LaunchedEffect(Unit) {
        viewModel.observeStats()
    }

    /**
     * Lista vertical con separaciones para mostrar tarjetas y gráficas.
     */
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
        ,
    ) {
        /**
         * Fila de estadísticas principales: total animales, likes y dislikes.
         */
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                StatCard("Animales", state.totalAnimals.toString(), modifier = Modifier.weight(1f))
                StatCard("Likes", state.likes.toString(), modifier = Modifier.weight(1f))
                StatCard("Dislikes", state.dislikes.toString(), modifier = Modifier.weight(1f))
            }
        }

        /**
         * Fila con animales por estado: disponibles, adoptados y pendientes.
         */
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                StatCard("Disponibles", state.available.toString(), modifier = Modifier.weight(1f))
                StatCard("Adoptados", state.adopted.toString(), modifier = Modifier.weight(1f))
                StatCard("Pendientes", state.pending.toString(), modifier = Modifier.weight(1f))
            }
        }

        /**
         * Gráfico de barras: distribución de animales por especie (top 6).
         */
        item {
            Text("Animales por especie", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            SimpleBarChart(data = state.bySpecies.take(6))
        }

        /**
         * Gráfico de barras: ranking de especies con más likes.
         */
        item {
            Text("Top especies con más likes", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            SimpleBarChart(data = state.topLikedSpecies)
        }
    }
}

/**
 * Pestaña "Gestión" del panel de administración.
 *
 * Contiene acciones rápidas típicas del rol administrador, por ejemplo:
 * - Añadir un nuevo animal (botón principal).
 * - (Opcional) acceder a gestión avanzada de animales (comentado en el código).
 *
 * @param onAddAnimal Callback para navegar a la creación de animal.
 * @param onManageAnimals Callback para navegar a gestión/edición de animales (actualmente comentado).
 */
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

        /**
         * Botón principal para crear un animal.
         */
        Button(onClick = onAddAnimal, modifier = Modifier.fillMaxWidth()) {
            Text("➕ Añadir animal")
        }

        /*
        * Acción opcional para una pantalla de gestión completa.
        * Actualmente está comentada en el código.
        *
        * OutlinedButton(onClick = onManageAnimals, modifier = Modifier.fillMaxWidth()) {
        Text("📋 Gestionar animales (editar/borrar)")
        }*/
    }
}

/**
 * Pestaña "Informes" del panel de administración.
 *
 * Permite generar informes en PDF con el listado de animales y ofrecer acciones:
 * - Generar PDF.
 * - Abrir último PDF generado.
 * - Compartir PDF.
 * - Configurar filtros (diálogo).
 *
 * Implementación:
 * - Usa [AnimalsViewModel] para obtener los animales.
 * - Genera el PDF con [generatePdf].
 * - Abre/Comparte el PDF con [openPdf] y [sharePdf].
 * - Controla UI state local: `isGenerating`, `error`, `lastFile`, y filtros.
 *
 * Nota:
 * - Los filtros se muestran en UI, pero actualmente no se aplican al listado que
 *   se pasa a [generatePdf] (se podría aplicar filtrado antes de generar).
 *
 * @param animalsViewModel ViewModel inyectado por Hilt (por defecto).
 */
@Composable
private fun AdminReportsTab(
    animalsViewModel: AnimalsViewModel = hiltViewModel(),
    authViewModel: HomeViewModel = hiltViewModel()
) {
    /** Contexto necesario para generar/abrir/compartir ficheros. */
    val context = LocalContext.current
    /** Scope para lanzar corrutinas desde la UI (eventos de botón). */
    val scope = rememberCoroutineScope()

    /** Estado local: indica si se está generando el informe. */
    var isGenerating by remember { mutableStateOf(false) }
    /** Estado local: mensaje de error si algo falla. */
    var error by remember { mutableStateOf<String?>(null) }
    /** Estado local: referencia al último PDF generado. */
    var lastFile by remember { mutableStateOf<File?>(null) }

    /** Control de visibilidad del diálogo de filtros. */
    var showFilters by remember { mutableStateOf(false) }

    /** Filtro: solo animales asignados al voluntario actual (pendiente de aplicación real). */
    var onlyMyAnimals by remember { mutableStateOf(false) }
    /** Filtro: solo favoritos (pendiente de aplicación real). */
    var onlyFavorites by remember { mutableStateOf(false) }
    /** Filtro: solo adoptados (pendiente de aplicación real). */
    var onlyAdopted by remember { mutableStateOf(false) }

    /**
     * Diálogo de filtros del informe (modal).
     * Permite seleccionar opciones antes de generar el PDF.
     */
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

    /**
     * Contenido principal de la pestaña Informes.
     */
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Informes", style = MaterialTheme.typography.titleLarge)

        /**
         * Botón para generar el PDF del informe.
         * Mientras se genera, muestra un spinner y bloquea el botón.
         */
        Button(
            onClick = {
                scope.launch {
                    isGenerating = true
                    error = null
                    try {
                        val animals = animalsViewModel.getAllAnimalsOnce()

                        val filteredAnimals = animals
                            .asSequence()
                            .let { seq ->
                                var s = seq

                                if (onlyAdopted) {
                                    s = s.filter { it.adoptionState == AdoptionState.ADOPTED }
                                }

                                val favorites = animalsViewModel.uiState.value.animals

                                if (onlyFavorites) {
                                    s = s.filter { it in favorites }
                                }


                                if (onlyMyAnimals) {
                                    val myUid = authViewModel.uiState.value.userUid
                                    s = s.filter { it.volunteerId == myUid }
                                }

                                s
                            }
                            .toList()

                        val file = generatePdf(context, filteredAnimals)
                        lastFile = file

                    } catch (e: Exception) {
                        // Captura errores de lectura/generación y los muestra en UI.
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

        /** Muestra error si existe. */
        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        /**
         * Acciones disponibles cuando ya hay un PDF generado:
         * - Abrir
         * - Compartir
         */
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

        /**
         * Botón para abrir el diálogo de filtros.
         * (Actualmente los filtros se guardan en estado, pero no se aplican al generar el PDF.)
         */
        OutlinedButton(
            onClick = { showFilters = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Filtros del informe")
        }
    }
}
