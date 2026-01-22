package com.gromber05.peco.ui.screens.animals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gromber05.peco.ui.components.AnimalCardHorizontal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalsScreen(
    onAnimalClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val animals =

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Animales") })
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = animals,
                key = { it.id }
            ) { animal ->
                AnimalCardHorizontal(
                    animal = animal,
                    onClick = { onAnimalClick(animal.id) }
                )
            }
        }
    }
}
