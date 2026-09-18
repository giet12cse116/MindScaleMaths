package com.mindscale.games.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindscale.games.presentation.theme.*

@Composable
fun TimerRing(
    secondsLeft: Int,
    totalSeconds: Int,
    modifier: Modifier = Modifier
) {
    val fraction = if (totalSeconds > 0) (secondsLeft.toFloat() / totalSeconds).coerceIn(0f, 1f) else 0f

    val ringColor = when {
        fraction > 0.50f -> AccentMint
        fraction >= 0.20f -> AccentAmber
        else -> AccentRed
    }

    val isUrgent = secondsLeft in 1..4
    val pulseScale by if (isUrgent) {
        val infiniteTransition = rememberInfiniteTransition(label = "TimerPulse")
        infiniteTransition.animateFloat(
            initialValue = 1.0f,
            targetValue = 1.08f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 300, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulseScale"
        )
    } else {
        rememberUpdatedState(1.0f)
    }

    Box(
        modifier = modifier
            .size(52.dp)
            .scale(pulseScale),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 3.dp.toPx()
            // Track
            drawCircle(
                color = SurfaceCardBorder,
                style = Stroke(width = strokeWidth)
            )
            // Progress Arc
            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = 360f * fraction,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Text(
            text = "$secondsLeft",
            color = ringColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Preview
@Composable
fun TimerRingPreview() {
    MindScaleTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TimerRing(secondsLeft = 40, totalSeconds = 45)
            TimerRing(secondsLeft = 15, totalSeconds = 45)
            TimerRing(secondsLeft = 4, totalSeconds = 45)
            TimerRing(secondsLeft = 0, totalSeconds = 45)
        }
    }
}
