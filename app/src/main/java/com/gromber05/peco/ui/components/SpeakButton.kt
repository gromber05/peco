package com.gromber05.peco.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.gromber05.peco.utils.TtsSpeaker

/**
 * Un botón de acción que, al ser pulsado, lee en voz alta el texto proporcionado.
 * Ideal para mejorar la accesibilidad en fichas descriptivas o detalles de animales.
 *
 * @param textToRead El texto que el motor TTS procesará y reproducirá.
 * @param modifier Modificador para personalizar el estilo o posición del botón.
 */
@Composable
fun SpeakButton(
    textToRead: String,
    modifier: Modifier = Modifier
) {
    val ctx = LocalContext.current

    // Mantiene una instancia única del altavoz mientras el componente esté en el árbol de la UI.
    val speaker = remember { TtsSpeaker(ctx) }

    /**
     * Gestión del ciclo de vida:
     * Cuando el componente se elimina de la pantalla (onDispose), se liberan los recursos
     * del motor TTS para evitar fugas de memoria y procesos en segundo plano innecesarios.
     */
    DisposableEffect(Unit) {
        onDispose {
            speaker.release()
        }
    }

    IconButton(
        onClick = { speaker.speak(textToRead) },
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.35f)) // Fondo oscuro translúcido para visibilidad sobre imágenes
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
            contentDescription = "Leer en voz alta",
            tint = Color.White,
            modifier = Modifier.size(18.dp)
        )
    }
}