package com.gromber05.peco.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.gromber05.peco.model.data.LabelCount
import kotlin.math.max

/**
 * Un gráfico de barras horizontal minimalista para visualizar distribuciones estadísticas.
 * Calcula automáticamente las proporciones basándose en el valor máximo de la lista proporcionada.
 *
 * @param data Lista de objetos [LabelCount] que contienen la etiqueta y el valor numérico a graficar.
 * @param modifier Modificador para ajustar el layout externo del componente.
 */
@Composable
fun SimpleBarChart(
    data: List<LabelCount>,
    modifier: Modifier = Modifier
) {
    // Control de estado vacío: Evita errores de renderizado si no hay datos.
    if (data.isEmpty()) {
        Text("Sin datos", modifier = modifier.alpha(0.7f))
        return
    }

    /** * Determina el valor de referencia para el 100% de la barra.
     * Se usa max(1, ...) para evitar divisiones por cero.
     */
    val maxValue = max(1, data.maxOf { it.count })

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        data.forEach { item ->
            // Proporción de la barra actual respecto al máximo
            val fraction = item.count.toFloat() / maxValue.toFloat()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Etiqueta de la categoría (Ancho fijo para alineación vertical)
                Text(
                    text = item.label,
                    modifier = Modifier.width(90.dp),
                    style = MaterialTheme.typography.labelLarge
                )

                /**
                 * Contenedor de la barra (Background).
                 * Representa el espacio total disponible.
                 */
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .weight(1f)
                        .height(18.dp)
                ) {
                    /**
                     * Barra de progreso (Foreground).
                     * Su ancho es proporcional al valor del [item.count].
                     */
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .fillMaxWidth(fraction)
                            .height(18.dp)
                    ) {}
                }

                // Valor numérico final
                Text(
                    text = item.count.toString(),
                    modifier = Modifier.width(36.dp),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}