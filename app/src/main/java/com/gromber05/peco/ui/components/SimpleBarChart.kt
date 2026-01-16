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

@Composable
fun SimpleBarChart(
    data: List<LabelCount>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) {
        Text("Sin datos", modifier = modifier.alpha(0.7f))
        return
    }

    val maxValue = max(1, data.maxOf { it.count })

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        data.forEach { item ->
            val fraction = item.count.toFloat() / maxValue.toFloat()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = item.label,
                    modifier = Modifier.width(90.dp),
                    style = MaterialTheme.typography.labelLarge
                )

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .weight(1f)
                        .height(18.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .fillMaxWidth(fraction)
                            .height(18.dp)
                    ) {}
                }

                Text(
                    text = item.count.toString(),
                    modifier = Modifier.width(36.dp),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
