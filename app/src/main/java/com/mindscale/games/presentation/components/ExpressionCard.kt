package com.mindscale.games.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindscale.games.presentation.theme.*

@Composable
fun ExpressionCard(
    label: String,
    expression: String,
    revealedValue: Double?,
    state: CardState,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = state.borderColor(),
        animationSpec = tween(durationMillis = 200),
        label = "cardBorderColor"
    )

    var triggerPulse by remember { mutableStateOf(false) }
    LaunchedEffect(state) {
        if (state == CardState.CORRECT || state == CardState.WRONG) {
            triggerPulse = true
        } else {
            triggerPulse = false
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (triggerPulse) 1.03f else 1.0f,
        animationSpec = tween(durationMillis = 150),
        finishedListener = { triggerPulse = false },
        label = "cardScalePulse"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(24.dp))
            .background(SurfaceCard)
            .border(
                width = if (state == CardState.IDLE) 1.5.dp else 2.5.dp,
                color = borderColor,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Label (Top Left alignment within column)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = label.uppercase(),
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Expression Display
            Text(
                text = expression,
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Revealed Numerical Value
            AnimatedVisibility(
                visible = revealedValue != null,
                enter = fadeIn(tween(200)) + slideInVertically(initialOffsetY = { 10 }),
                exit = fadeOut(tween(150))
            ) {
                if (revealedValue != null) {
                    val formattedVal = if (revealedValue == revealedValue.toLong().toDouble()) {
                        revealedValue.toLong().toString()
                    } else {
                        revealedValue.toString()
                    }
                    Text(
                        text = "= $formattedVal",
                        color = AccentMint,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                } else {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Preview
@Composable
fun ExpressionCardPreview() {
    MindScaleTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ExpressionCard(
                label = "LEFT",
                expression = "7 × 8",
                revealedValue = 56.0,
                state = CardState.CORRECT,
                modifier = Modifier
                    .weight(1f)
                    .height(160.dp)
            )
            ExpressionCard(
                label = "RIGHT",
                expression = "(60 - 5) + 1",
                revealedValue = null,
                state = CardState.IDLE,
                modifier = Modifier
                    .weight(1f)
                    .height(160.dp)
            )
        }
    }
}
