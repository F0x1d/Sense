package com.f0x1d.sense.ui.widget

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp

@Composable
fun PulsatingDots(modifier: Modifier = Modifier, firstItemPadding: Boolean = false) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        PulsatingDot(
            modifier = Modifier.padding(start = if (firstItemPadding) 2.dp else 0.dp),
            offsetMillis = 0
        )
        PulsatingDot(offsetMillis = 200)
        PulsatingDot(offsetMillis = 400)
    }
}

@Composable
private fun PulsatingDot(modifier: Modifier = Modifier, offsetMillis: Int = 0) {
    Pulsating(offsetMillis = offsetMillis) {
        Surface(
            modifier = modifier.size(10.dp),
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            shape = CircleShape,
            content = {}
        )
    }
}

@Composable
private fun Pulsating(offsetMillis: Int, pulseFraction: Float = 1.2f, content: @Composable () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition()

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = pulseFraction,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(offsetMillis, StartOffsetType.Delay)
        )
    )

    Box(modifier = Modifier.scale(scale)) {
        content()
    }
}