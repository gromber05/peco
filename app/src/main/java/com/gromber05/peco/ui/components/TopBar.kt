package com.gromber05.peco.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Una barra superior (TopAppBar) personalizada con un diseño de cabecera expandida.
 * Presenta un saludo dinámico al usuario y utiliza una geometría redondeada en la parte inferior
 * para suavizar la transición hacia el contenido de la pantalla.
 *
 * @param name El nombre del usuario que se mostrará en el mensaje de bienvenida.
 */
@Composable
fun MyTopAppBar(name: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 140.dp), // Proporciona un área de cabecera generosa
        color = MaterialTheme.colorScheme.primaryContainer,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(
            bottomStart = 30.dp,
            bottomEnd = 30.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.Bottom // Alinea el texto en la base de la cabecera
        ) {
            Text(
                text = "¡Hola, $name!",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 30.sp,
                style = MaterialTheme.typography.headlineLarge
            )
        }
    }
}