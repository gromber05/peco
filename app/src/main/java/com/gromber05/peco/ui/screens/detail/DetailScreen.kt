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
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.gromber05.peco.ui.components.AnimalCardHorizontal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    animalId: Int,
    viewModel: DetailViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val animal by viewModel.animal.collectAsState(initial = null)
    val uiState by viewModel.uiState.collectAsState(initial = DetailUiState())

    when {
        uiState.isLoading -> {
            CircularProgressIndicator()
        }
    }

    BackHandler {
        onBack()
    }

    LaunchedEffect(animalId) {
        viewModel.loadAnimal(animalId)
    }

    LaunchedEffect(uiState.notFound) {
        if (uiState.notFound) onBack()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(animal?.name ?: "Detalle") }
            )
        }
    ) { padding ->
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

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AsyncImage(
                model = animal?.photo,
                contentDescription = "Foto de ${animal?.name}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(animal?.name ?: "", style = MaterialTheme.typography.headlineSmall)
                Text(
                    text = "Especie: ${animal?.species}",
                    style = MaterialTheme.typography.bodyLarge
                )

                AssistChip(
                    onClick = { },
                    label = { Text("Estado: ${animal?.adoptionState}") }
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
