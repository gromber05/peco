package com.gromber05.peco.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Una tarjeta informativa diseñada para mostrar métricas individuales o estadísticas clave.
 * Utiliza una jerarquía tipográfica para diferenciar claramente el concepto del valor numérico.
 *
 * @param title El nombre de la métrica o categoría (ej: "Total Animales", "Adoptados").
 * @param value El dato numérico o estado a mostrar (ej: "124", "85%").
 * @param modifier Modificador para ajustar el tamaño, peso o padding externo de la tarjeta.
 */
@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            // Etiqueta descriptiva: utiliza un estilo más pequeño y discreto
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Valor de la métrica: utiliza un estilo destacado para captar la atención
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}