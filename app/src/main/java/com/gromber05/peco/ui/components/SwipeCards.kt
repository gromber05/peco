package com.gromber05.peco.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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

private enum class SwipeDirection { LEFT, RIGHT }

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

        if (next != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(520.dp)
                    .padding(horizontal = 20.dp)
                    .graphicsLayer {
                        scaleX = 0.96f
                        scaleY = 0.96f
                        alpha = 0.75f
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
                        SwipeOverlay(fraction = swipeFraction, direction = direction)
                    }
                }
            }
        }
    }
}

@Composable
private fun SwipeableCard(
    modifier: Modifier = Modifier,
    onSwiped: (SwipeDirection) -> Unit,
    content: @Composable (swipeFraction: Float, direction: SwipeDirection?) -> Unit
) {
    val scope = rememberCoroutineScope()
    val config = LocalConfiguration.current
    val density = LocalDensity.current

    val thresholdPx = with(density) { (config.screenWidthDp.dp * 0.25f).toPx() }

    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }

    val direction: SwipeDirection? = when {
        offsetX.value > 20f -> SwipeDirection.RIGHT
        offsetX.value < -20f -> SwipeDirection.LEFT
        else -> null
    }

    val swipeFraction = (abs(offsetX.value) / thresholdPx).coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .graphicsLayer {
                translationX = offsetX.value
                translationY = offsetY.value
                rotationZ = (offsetX.value / (thresholdPx * 1.2f)).coerceIn(-1f, 1f) * 12f
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        scope.launch {
                            offsetX.snapTo(offsetX.value + dragAmount.x)
                            offsetY.snapTo(offsetY.value + dragAmount.y * 0.25f)
                        }
                    },
                    onDragEnd = {
                        val shouldSwipe = abs(offsetX.value) > thresholdPx
                        if (shouldSwipe) {
                            val dir = if (offsetX.value > 0) SwipeDirection.RIGHT else SwipeDirection.LEFT
                            scope.launch {
                                val targetX = if (dir == SwipeDirection.RIGHT) thresholdPx * 3.0f else -thresholdPx * 3.0f
                                offsetX.animateTo(targetX, spring(stiffness = Spring.StiffnessMediumLow))
                                onSwiped(dir)
                                offsetX.snapTo(0f)
                                offsetY.snapTo(0f)
                            }
                        } else {
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

@Composable
private fun SwipeOverlay(
    fraction: Float,
    direction: SwipeDirection?
) {
    if (direction == null || fraction <= 0.01f) return

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
            modifier = Modifier.alpha(fraction.coerceIn(0f, 1f))
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
