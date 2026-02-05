package com.gromber05.peco.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.abs

/** Representa las direcciones posibles hacia las cuales se puede deslizar una carta. */
private enum class SwipeDirection { LEFT, RIGHT }

/**
 * Un componente de mazo de cartas interactivo al estilo Tinder.
 * Gestiona una pila de elementos donde el usuario puede deslizar la carta superior
 * hacia la derecha (Like) o hacia la izquierda (Dislike).
 *
 * @param items Lista de elementos de tipo [T] que componen el mazo.
 * @param modifier Ajustes de diseño para el contenedor del mazo.
 * @param cardContent Contenido visual que se renderizará dentro de cada carta.
 * @param onLike Callback ejecutado cuando un elemento es deslizado a la derecha.
 * @param onDislike Callback ejecutado cuando un elemento es deslizado a la izquierda.
 * @param onEmpty Composable que se muestra cuando no quedan más elementos en el mazo.
 * @param keyOf Función para extraer una clave única de cada elemento (mejora el rendimiento de recomposición).
 */
@Composable
fun <T> TinderSwipeDeck(
    items: List<T>,
    modifier: Modifier = Modifier,
    cardContent: @Composable (item: T) -> Unit,
    onLike: (T) -> Unit,
    onDislike: (T) -> Unit,
    onEmpty: @Composable () -> Unit = { Text("No hay más animales 🐾") },
    keyOf: (T) -> Any = { it as Any }
) {
    if (items.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) { onEmpty() }
        return
    }

    val top = items.first()
    val next = items.getOrNull(1)

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        // --- Carta de Fondo (Siguiente en la lista) ---
        if (next != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(520.dp)
                    .padding(horizontal = 20.dp)
                    .graphicsLayer {
                        scaleX = 0.96f // Ligeramente más pequeña para dar profundidad
                        scaleY = 0.96f
                        alpha = 0.75f // Más tenue que la principal
                    }
            ) {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(Modifier.fillMaxSize()) { cardContent(next) }
                }
            }
        }

        // --- Carta Superior (Interactivo) ---
        // 'key' asegura que el estado del swipe se resetee completamente al cambiar de ítem
        key(keyOf(top)) {
            SwipeableCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(540.dp)
                    .padding(horizontal = 16.dp),
                onSwiped = { direction ->
                    when (direction) {
                        SwipeDirection.RIGHT -> onLike(top)
                        SwipeDirection.LEFT -> onDislike(top)
                    }
                }
            ) { swipeFraction, direction ->
                Card(
                    shape = RoundedCornerShape(30.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                ) {
                    Box(Modifier.fillMaxSize()) {
                        cardContent(top)
                        // Superposición visual (ME GUSTA / NOPE)
                        SwipeOverlay(fraction = swipeFraction, direction = direction)
                    }
                }
            }
        }
    }
}

/**
 * Wrapper encargado de detectar gestos y aplicar transformaciones físicas a la carta.
 */
@Composable
private fun SwipeableCard(
    modifier: Modifier = Modifier,
    onSwiped: (SwipeDirection) -> Unit,
    content: @Composable (swipeFraction: Float, direction: SwipeDirection?) -> Unit
) {
    val scope = rememberCoroutineScope()
    val config = LocalConfiguration.current
    val density = LocalDensity.current

    // Umbral de decisión: 25% del ancho de la pantalla
    val thresholdPx = with(density) { (config.screenWidthDp.dp * 0.25f).toPx() }

    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }

    val direction: SwipeDirection? = when {
        offsetX.value > 20f -> SwipeDirection.RIGHT
        offsetX.value < -20f -> SwipeDirection.LEFT
        else -> null
    }

    // Calcula qué tan cerca está la carta del umbral (0.0 a 1.0)
    val swipeFraction = (abs(offsetX.value) / thresholdPx).coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .graphicsLayer {
                translationX = offsetX.value
                translationY = offsetY.value
                // Efecto de rotación: rota hasta 12 grados según el desplazamiento lateral
                rotationZ = (offsetX.value / (thresholdPx * 1.2f)).coerceIn(-1f, 1f) * 12f
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        scope.launch {
                            offsetX.snapTo(offsetX.value + dragAmount.x)
                            // El movimiento vertical es amortiguado (0.25f) para mayor control
                            offsetY.snapTo(offsetY.value + dragAmount.y * 0.25f)
                        }
                    },
                    onDragEnd = {
                        val shouldSwipe = abs(offsetX.value) > thresholdPx
                        if (shouldSwipe) {
                            val dir = if (offsetX.value > 0) SwipeDirection.RIGHT else SwipeDirection.LEFT
                            scope.launch {
                                // Lanza la carta fuera de la pantalla
                                val targetX = if (dir == SwipeDirection.RIGHT) thresholdPx * 5.0f else -thresholdPx * 5.0f
                                offsetX.animateTo(targetX, spring(stiffness = Spring.StiffnessMediumLow))
                                onSwiped(dir)
                            }
                        } else {
                            // Regresa al centro con un efecto de muelle (spring)
                            scope.launch {
                                offsetX.animateTo(0f, spring(stiffness = Spring.StiffnessMedium))
                                offsetY.animateTo(0f, spring(stiffness = Spring.StiffnessMedium))
                            }
                        }
                    }
                )
            }
    ) {
        content(swipeFraction, direction)
    }
}

/**
 * Muestra etiquetas flotantes sobre la carta para indicar la acción inminente.
 */
@Composable
private fun SwipeOverlay(
    fraction: Float,
    direction: SwipeDirection?
) {
    if (direction == null || fraction <= 0.05f) return

    val (text, container, textColor) = when (direction) {
        SwipeDirection.RIGHT -> Triple(
            "ME GUSTA",
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        )
        SwipeDirection.LEFT -> Triple(
            "NOPE",
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = if (direction == SwipeDirection.RIGHT) Alignment.TopStart else Alignment.TopEnd
    ) {
        Surface(
            color = container,
            shape = RoundedCornerShape(14.dp),
            tonalElevation = 6.dp,
            modifier = Modifier.alpha(fraction) // La visibilidad aumenta conforme se arrastra
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                fontWeight = FontWeight.Black,
                color = textColor
            )
        }
    }
}