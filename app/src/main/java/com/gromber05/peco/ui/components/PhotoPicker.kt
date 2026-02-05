package com.gromber05.peco.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

/**
 * Componente de selección de imágenes que permite al usuario elegir una foto de su galería.
 * Gestiona automáticamente el lanzamiento del selector del sistema, la lectura de bytes
 * para su posterior carga en el servidor y la previsualización local.
 *
 * @param photoUri URI de la imagen seleccionada actualmente (en formato String).
 * @param onPhotoSelected Callback que se dispara al elegir o quitar una foto.
 * Devuelve el [ByteArray] (para subir a Storage) y el [String] de la URI (para la previsualización).
 */
@Composable
fun PhotoPicker(
    photoUri: String,
    onPhotoSelected: (ByteArray?, String) -> Unit
) {
    val context = LocalContext.current

    /**
     * Launcher para la actividad de selección de contenido.
     * Utiliza el contrato [GetContent] para abrir la galería o el explorador de archivos.
     */
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            // Se abre el flujo de entrada del ContentResolver para convertir la URI en bytes
            val bytes = context.contentResolver.openInputStream(uri)!!.use { it.readBytes() }
            onPhotoSelected(bytes, uri.toString())
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Etiqueta del campo
        Text("Foto", style = MaterialTheme.typography.labelLarge)

        // Botón de acción principal para abrir la galería
        Button(
            onClick = { pickImageLauncher.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (photoUri.isBlank()) "Elegir foto" else "Cambiar foto")
        }

        // Sección de previsualización: solo se muestra si hay una URI válida
        if (photoUri.isNotBlank()) {
            AsyncImage(
                model = photoUri,
                contentDescription = "Foto del animal",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )

            // Opción para resetear la selección
            TextButton(
                onClick = { onPhotoSelected(null, "") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Quitar foto")
            }
        }
    }
}