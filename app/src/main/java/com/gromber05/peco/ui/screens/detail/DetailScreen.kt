package com.gromber05.peco.ui.screens.detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.gromber05.peco.utils.LocationUtils.rememberCityFromLatLng

/**
 * Pantalla de detalle de un animal.
 *
 * Funcionalidades:
 * - Carga/observa en tiempo real la información del animal a partir de [animalId].
 * - Muestra estados de carga y "no encontrado".
 * - Permite volver atrás mediante TopAppBar y [BackHandler].
 * - Renderiza la foto con Coil ([AsyncImage]) y datos principales del animal.
 * - Convierte coordenadas (lat/lon) a ciudad usando [rememberCityFromLatLng].
 *
 * Arquitectura:
 * - Sigue MVVM: la UI consume [DetailViewModel.uiState] y reacciona a cambios.
 * - [DetailViewModel] se obtiene con Hilt usando [hiltViewModel].
 *
 * @param modifier Modificador externo para personalizar el layout.
 * @param animalId ID del animal a mostrar (document ID / uid).
 * @param viewModel ViewModel del detalle (inyectado por Hilt por defecto).
 * @param onBack Callback para volver a la pantalla anterior.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    animalId: String,
    viewModel: DetailViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    /**
     * Estado de UI del detalle. Se proporciona un estado inicial para evitar nulls
     * durante la primera composición.
     */
    val uiState by viewModel.uiState.collectAsState(initial = DetailUiState())

    /** Acceso directo al animal actual si está cargado. */
    val animal = uiState.animal

    /**
     * Intercepta el botón físico de "atrás" para ejecutar [onBack].
     */
    BackHandler { onBack() }

    /**
     * Observa/carga el animal cuando cambia [animalId].
     * Esto permite reutilizar la pantalla para distintos IDs.
     */
    LaunchedEffect(animalId) {
        viewModel.observeAnimal(animalId)
    }

    /**
     * Si el ViewModel marca el animal como no encontrado, se navega hacia atrás.
     * (Por ejemplo, si el documento ha sido eliminado o el ID es inválido.)
     */
    LaunchedEffect(uiState.notFound) {
        if (uiState.notFound) onBack()
    }

    /**
     * Scaffold con TopAppBar:
     * - Título dinámico (nombre del animal si existe).
     * - Icono de navegación para volver atrás.
     */
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(animal?.name ?: "Detalle")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver atrás"
                        )
                    }
                }
            )
        }
    ) { padding ->

        /**
         * Estado de carga: muestra un spinner centrado y sale del Scaffold.
         */
        if (uiState.isLoading) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        /**
         * Estado "no encontrado": si no hay animal tras cargar, se muestra un mensaje.
         */
        if (animal == null) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Animal no encontrado")
            }
            return@Scaffold
        }

        /** Color de fondo de la tarjeta (Material3). */
        val cardBackgroundColor = MaterialTheme.colorScheme.surfaceVariant

        /**
         * Tarjeta principal con toda la información del animal.
         * Incluye imagen arriba y detalles abajo.
         */
        Card(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                /**
                 * Imagen del animal usando Coil.
                 * - Se muestra a modo "cover" (Crop).
                 * - Se redondea la parte superior para encajar con la Card.
                 */
                AsyncImage(
                    model = animal.photo,
                    contentDescription = "Foto de ${animal.name}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                )

                /**
                 * Bloque de detalles: nombre, especie, nacimiento, ciudad y estado de adopción.
                 */
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(animal.name, style = MaterialTheme.typography.headlineSmall)

                    Text(
                        text = "Especie: ${animal.species}",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Text(
                        text = "Año de Nacimiento: ${animal.dob}",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    /**
                     * Obtiene el nombre de la ciudad a partir de las coordenadas del animal.
                     * Se usa `remember` internamente para evitar recomputar de forma innecesaria.
                     */
                    val city = rememberCityFromLatLng(
                        latitude = animal.latitude,
                        longitude = animal.longitude
                    )

                    Text(
                        text = "Ciudad: $city",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    /**
                     * Chip informativo para mostrar el estado de adopción.
                     * onClick está vacío porque funciona como elemento informativo.
                     */
                    AssistChip(
                        onClick = { },
                        label = { Text("Estado: ${animal.adoptionState}") }
                    )

                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }
}