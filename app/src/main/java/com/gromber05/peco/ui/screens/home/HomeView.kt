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

/**
 * Vista principal de la pestaña "Inicio" dentro de [HomeScreen].
 *
 * Muestra un "deck" de tarjetas estilo Tinder para explorar animales y realizar swipes:
 * - Like (favorito)
 * - Dislike (descartar)
 *
 * Componentes clave:
 * - [TinderSwipeDeck]: contenedor que gestiona el comportamiento de swipe.
 * - [AnimalCard]: contenido visual de cada tarjeta.
 * - Botonera inferior para ejecutar acciones manuales (like/dislike) sobre la tarjeta actual.
 *
 * Arquitectura:
 * - Consume el estado desde [HomeViewModel.uiState] usando `collectAsState()`.
 * - Delegación de acciones (like/dislike/reset) al ViewModel.
 *
 * @param modifier Modificador externo para personalizar layout.
 * @param viewModel ViewModel compartido de Home que contiene el deck y acciones.
 * @param onDetails Callback para abrir el detalle de un animal (recibe animalId).
 */
@Composable
fun HomeView(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    onDetails: (String) -> Unit
) {
    /** Estado de UI observado desde el ViewModel (deck, listas, etc.). */
    val state by viewModel.uiState.collectAsState()

    /**
     * Layout principal:
     * - Zona superior (peso 1): deck de tarjetas swipeables.
     * - Zona inferior: botones manuales de dislike/like.
     */
    Column(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            /**
             * Deck de tarjetas tipo Tinder.
             *
             * Parámetros:
             * - items: lista de animales a mostrar (state.deck).
             * - keyOf: clave estable para Compose (uid del animal).
             * - cardContent: UI de cada tarjeta.
             * - onLike/onDislike: callbacks cuando se hace swipe.
             * - onEmpty: UI cuando no quedan más tarjetas.
             */
            TinderSwipeDeck(
                items = state.deck,
                modifier = Modifier.fillMaxSize(),
                keyOf = { it.uid },
                cardContent = { animal ->
                    AnimalCard(
                        animal = animal,
                        onDetails = {
                            // Navega al detalle del animal seleccionado
                            onDetails(animal.uid)
                        }
                    )
                },
                onLike = { animal -> viewModel.onLike(animal) },
                onDislike = { animal -> viewModel.onDislike(animal) },
                onEmpty = {
                    /**
                     * Estado vacío del deck: no quedan animales para mostrar.
                     * Se ofrece un botón para reiniciar el flujo de swipes.
                     */
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

        /**
         * Botonera inferior:
         * - Botón de descartar (dislike) para la tarjeta actual.
         * - Botón de me gusta (like) para la tarjeta actual.
         *
         * Permite controlar el deck sin hacer swipe físico/gestual.
         */
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
