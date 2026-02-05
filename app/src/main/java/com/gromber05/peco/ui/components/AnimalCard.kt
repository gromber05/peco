package com.gromber05.peco.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.gromber05.peco.model.AdoptionState
import com.gromber05.peco.model.data.Animal
import com.gromber05.peco.utils.LocationUtils.rememberCityFromLatLng
import com.gromber05.peco.utils.TtsSpeaker
import com.gromber05.peco.utils.fechaATexto
import com.gromber05.peco.utils.parseDateApi

/**
 * Componente visual que muestra la información resumida de un animal en formato de tarjeta.
 * Diseñado con un estilo moderno que incluye imágenes a sangre (full-bleed), gradientes
 * para legibilidad y soporte para accesibilidad mediante TTS.
 *
 * @param modifier Modificador para ajustar el diseño externo (padding, tamaño, etc.).
 * @param animal El objeto [Animal] cuyos datos se van a visualizar.
 * @param onDetails Callback que se ejecuta cuando el usuario pulsa la tarjeta.
 */
@Composable
fun AnimalCard(
    modifier: Modifier = Modifier,
    animal: Animal,
    onDetails: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onDetails),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .height(540.dp)
        ) {
            // --- Capa 1: Imagen de fondo ---
            if (!animal.photo.isNullOrBlank()) {
                AsyncImage(
                    model = animal.photo,
                    contentDescription = "Foto de ${animal.name}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Placeholder en caso de que no haya imagen
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Animal sin imagen",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(72.dp)
                    )
                }
            }

            // --- Capa 2: Gradiente inferior para legibilidad del texto ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.55f),
                                Color.Black.copy(alpha = 0.85f),
                            )
                        )
                    )
            )

            // --- Capa 3: Información del animal ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(18.dp)
            ) {
                Text(
                    text = animal.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Fila de etiquetas (Especie y Estado)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Chip(icon = Icons.Default.Pets, text = animal.species)
                    Chip(text = animal.adoptionState.value)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Detalles: Nacimiento, Ubicación y Botón de Voz
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Nacimiento: ${animal.dob}",
                            color = Color.White.copy(alpha = 0.9f),
                            style = MaterialTheme.typography.bodyMedium
                        )

                        // Conversión de coordenadas a nombre de ciudad
                        val city = rememberCityFromLatLng(
                            latitude = animal.latitude,
                            longitude = animal.longitude
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = city,
                                color = Color.White.copy(alpha = 0.9f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // Función de accesibilidad para lectura de la ficha
                    SpeakButton(
                        textToRead = "Información del animal. Se llama ${animal.name}. Es un ${animal.species}. Estado: ${animal.adoptionState.value}. Nació el: ${fechaATexto(animal.dob)}."
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Toca la tarjeta para ver detalles",
                    color = Color.White.copy(alpha = 0.75f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

/**
 * Componente auxiliar para mostrar etiquetas informativas con estilo de cápsula translúcida.
 */
@Composable
private fun Chip(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Surface(
        color = Color.White.copy(alpha = 0.18f),
        shape = RoundedCornerShape(999.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp).alpha(0.95f)
                )
            }
            Text(
                text = text,
                color = Color.White,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}