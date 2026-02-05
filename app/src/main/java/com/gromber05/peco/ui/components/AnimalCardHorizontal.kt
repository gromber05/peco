package com.gromber05.peco.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.gromber05.peco.model.data.Animal

/**
 * Representación horizontal compacta de la información de un animal.
 * Optimizado para listas densas o historiales de actividad.
 *
 * @param animal El objeto [Animal] con la información a mostrar.
 * @param onClick Acción que se ejecuta al realizar un toque simple en la tarjeta.
 * @param modifier Modificador para personalizar el diseño o comportamiento externo.
 * @param onErase Acción opcional que se dispara tras una pulsación larga (Long Click),
 * útil para eliminar elementos de una lista de favoritos.
 */
@Composable
fun AnimalCardHorizontal(
    animal: Animal,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onErase: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onErase
            ),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // Imagen del animal alineada a la izquierda (proporción 1:1 respecto a la altura)
            AsyncImage(
                model = animal.photo,
                contentDescription = "Foto de ${animal.name}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(120.dp)
                    .fillMaxHeight()
            )

            // Panel de información lateral
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Título y especie con manejo de desbordamiento de texto
                Column {
                    Text(
                        text = animal.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = animal.species,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Indicador de estado de adopción mediante un AssistChip
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AssistChip(
                        onClick = { /* Acción opcional al pulsar el chip */ },
                        label = {
                            Text(
                                text = animal.adoptionState.value,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    )
                }
            }
        }
    }
}