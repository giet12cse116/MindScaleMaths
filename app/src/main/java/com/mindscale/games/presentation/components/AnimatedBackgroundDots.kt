package com.mindscale.games.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mindscale.games.presentation.theme.BackgroundDeep
import com.mindscale.games.presentation.theme.MindScaleTheme
import com.mindscale.games.presentation.theme.SurfaceCardBorder

@Composable
fun AnimatedBackgroundDots(
    modifier: Modifier = Modifier,
    dotSpacingDp: Float = 28f,
    dotRadiusDp: Float = 1.5f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "BackgroundDrift")
    val offsetX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 16f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetX"
    )
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetY"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDeep)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val spacing = dotSpacingDp.dp.toPx()
            val radius = dotRadiusDp.dp.toPx()
            val dotColor = SurfaceCardBorder.copy(alpha = 0.25f)

            val width = size.width
            val height = size.height

            var y = (offsetY % spacing) - spacing
            while (y < height + spacing) {
                var x = (offsetX % spacing) - spacing
                while (x < width + spacing) {
                    drawCircle(
                        color = dotColor,
                        radius = radius,
                        center = Offset(x, y)
                    )
                    x += spacing
                }
                y += spacing
            }
        }
    }
}

@Preview
@Composable
fun AnimatedBackgroundDotsPreview() {
    MindScaleTheme {
        AnimatedBackgroundDots()
    }
}
