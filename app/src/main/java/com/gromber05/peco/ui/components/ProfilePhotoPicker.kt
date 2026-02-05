package com.gromber05.peco.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

/**
 * Componente interactivo para la gestión de la fotografía de perfil.
 * Presenta una interfaz de "marco" que permite previsualizar la imagen actual,
 * cambiarla mediante un selector o eliminarla.
 *
 * @param photoUri URI o URL de la imagen de perfil actual. Si está vacía, muestra un estado "Sin foto".
 * @param onPick Callback para disparar el proceso de selección de una nueva imagen.
 * @param onRemove Callback para eliminar la imagen actual y limpiar la selección.
 */
@Composable
fun ProfilePhotoPicker(
    photoUri: String,
    onPick: () -> Unit,
    onRemove: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)

    Card(
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(shape)
        ) {
            // --- ESTADO: SIN FOTO ---
            if (photoUri.isBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Sin foto",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(10.dp))
                        FilledTonalButton(onClick = onPick) {
                            Text("Subir foto")
                        }
                    }
                }
            }
            // --- ESTADO: FOTO SELECCIONADA ---
            else {
                // Previsualización de la imagen
                AsyncImage(
                    model = photoUri,
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Gradiente inferior para legibilidad de los botones sobre la imagen
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    androidx.compose.ui.graphics.Color.Transparent,
                                    MaterialTheme.colorScheme.scrim.copy(alpha = 0.55f)
                                ),
                                startY = 120f
                            )
                        )
                )

                // Botón de cierre rápido (esquina superior derecha)
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                ) {
                    IconButton(onClick = onRemove, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Quitar foto",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Acciones principales (inferior izquierda)
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilledTonalButton(onClick = onPick) {
                        Text("Cambiar")
                    }
                    OutlinedButton(
                        onClick = onRemove,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surface)
                    ) {
                        Text("Quitar", color = MaterialTheme.colorScheme.surface)
                    }
                }
            }
        }
    }
}