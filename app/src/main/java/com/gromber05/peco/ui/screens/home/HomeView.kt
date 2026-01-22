package com.gromber05.peco.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gromber05.peco.ui.components.AnimalCard
import com.gromber05.peco.ui.components.TinderSwipeDeck

@Composable
fun HomeView(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            TinderSwipeDeck(
                items = state.deck,
                modifier = Modifier.fillMaxSize(),
                keyOf = { it.id },
                cardContent = { animal ->
                    AnimalCard(animal = animal) { }
                },
                onLike = { animal -> viewModel.onLike(animal) },
                onDislike = { animal -> viewModel.onDislike(animal) },
                onEmpty = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No hay más animales por hoy 🐾")
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { viewModel.resetSwipes() }) {
                            Text("Volver a empezar")
                        }
                    }
                }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledTonalIconButton(
                onClick = { viewModel.dislikeCurrent() },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Filled.Close, contentDescription = "Descartar")
            }

            FilledIconButton(
                onClick = { viewModel.likeCurrent() },
                modifier = Modifier.size(64.dp)
            ) {
                Icon(Icons.Filled.Favorite, contentDescription = "Me gusta")
            }
        }
    }
}
