package com.mindscale.games.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindscale.games.domain.model.Comparison
import com.mindscale.games.presentation.theme.*

@Composable
fun CenterAnswerRail(
    onAnswer: (Comparison) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val alpha = if (enabled) 1.0f else 0.40f

    Column(
        modifier = modifier.padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RailButton(
            symbol = "‹",
            accentColor = AccentRed,
            enabled = enabled,
            alpha = alpha,
            onClick = { onAnswer(Comparison.LESS) }
        )

        RailButton(
            symbol = "=",
            accentColor = AccentMint,
            enabled = enabled,
            alpha = alpha,
            onClick = { onAnswer(Comparison.EQUAL) }
        )

        RailButton(
            symbol = "›",
            accentColor = AccentPurple,
            enabled = enabled,
            alpha = alpha,
            onClick = { onAnswer(Comparison.GREATER) }
        )
    }
}

@Composable
private fun RailButton(
    symbol: String,
    accentColor: Color,
    enabled: Boolean,
    alpha: Float,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.95f else 1.0f,
        animationSpec = tween(durationMillis = 100),
        label = "railButtonPressScale"
    )

    Box(
        modifier = Modifier
            .size(54.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(SurfaceCard.copy(alpha = alpha))
            .border(
                width = 2.dp,
                color = accentColor.copy(alpha = if (enabled) 0.6f else 0.25f),
                shape = CircleShape
            )
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol,
            color = accentColor.copy(alpha = alpha),
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Preview
@Composable
fun CenterAnswerRailPreview() {
    MindScaleTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            CenterAnswerRail(onAnswer = {}, enabled = true)
            CenterAnswerRail(onAnswer = {}, enabled = false)
        }
    }
}
