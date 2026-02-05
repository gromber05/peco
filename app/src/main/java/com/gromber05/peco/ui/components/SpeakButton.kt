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

@Composable
fun SpeakButton(
    textToRead: String,
    modifier: Modifier = Modifier
) {
    val ctx = LocalContext.current
    val speaker = remember { TtsSpeaker(ctx) }
    DisposableEffect(Unit) { onDispose { speaker.release() } }

    IconButton(
        onClick = {speaker.speak(textToRead)},
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.35f))
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
            contentDescription = "Leer en voz alta",
            tint = Color.White,
            modifier = Modifier.size(18.dp)
        )
    }
}

