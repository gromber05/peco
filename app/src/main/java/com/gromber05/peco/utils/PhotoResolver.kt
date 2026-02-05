package com.gromber05.peco.utils

import android.content.ContentResolver
import android.net.Uri

/**
 * Convierte una [Uri] en un array de bytes (`ByteArray`).
 *
 * Abre un `InputStream` a partir del [ContentResolver] y lee completamente
 * su contenido en memoria.
 *
 * Esta función se utiliza habitualmente para:
 * - Subir archivos (imágenes, documentos) a un backend o almacenamiento remoto.
 * - Procesar contenidos seleccionados mediante el sistema (galería, archivos, etc.).
 *
 * ⚠️ Nota:
 * - Asume que la URI es válida y accesible; si no lo es, se lanzará una excepción
 *   al forzar el operador `!!`.
 * - No es recomendable usarla con archivos muy grandes, ya que carga todo el
 *   contenido en memoria.
 *
 * @param contentResolver [ContentResolver] utilizado para abrir la URI.
 * @param uri URI del recurso a leer.
 * @return Contenido del recurso como [ByteArray].
 */
fun uriToBytes(contentResolver: ContentResolver, uri: Uri): ByteArray {
    return contentResolver.openInputStream(uri)!!.use { it.readBytes() }
}
