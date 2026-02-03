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
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    animalId: String,
    viewModel: DetailViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState(initial = DetailUiState())
    val animal = uiState.animal

    val context = LocalContext.current

    BackHandler { onBack() }

    LaunchedEffect(animalId) {
        viewModel.observeAnimal(animalId)
    }

    LaunchedEffect(uiState.notFound) {
        if (uiState.notFound) onBack()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(animal?.name ?: "Detalle") }
            )
        },
        floatingActionButton = {
            if (uiState.volunteer != null) {
                val volunteer =
                FloatingActionButton(
                    onClick = { viewModel.openDialer(context = context, phone = uiState.volunteer!!.phone) }
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Llamar")
                }
            }
        }
    ) { padding ->

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

        val cardBackgroundColor = MaterialTheme.colorScheme.surfaceVariant

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
                // ✅ Foto dentro de la Card (con esquinas redondeadas arriba)
                AsyncImage(
                    model = animal.photo,
                    contentDescription = "Foto de ${animal.name}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                )

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

                    AssistChip(
                        onClick = { },
                        label = { Text("Estado: ${animal.adoptionState}") }
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "Aquí puedes poner más datos (edad, descripción, ubicación, etc.)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
