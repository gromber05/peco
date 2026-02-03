package com.gromber05.peco.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun PhotoPicker(
    photoUri: String,
    onPhotoSelected: (ByteArray?, String) -> Unit
) {
    val context = LocalContext.current

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val bytes = context.contentResolver.openInputStream(uri)!!.use { it.readBytes() }
            onPhotoSelected(bytes, uri.toString())
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Foto", style = MaterialTheme.typography.labelLarge)

        Button(
            onClick = { pickImageLauncher.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (photoUri.isBlank()) "Elegir foto" else "Cambiar foto")
        }

        if (photoUri.isNotBlank()) {
            AsyncImage(
                model = photoUri,
                contentDescription = "Foto del animal",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
            TextButton(
                onClick = { onPhotoSelected(null, "") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Quitar foto")
            }
        }
    }
}
