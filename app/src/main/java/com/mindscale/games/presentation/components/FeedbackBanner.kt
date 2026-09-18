package com.mindscale.games.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindscale.games.domain.model.Comparison
import com.mindscale.games.domain.model.RoundOutcome
import com.mindscale.games.presentation.theme.*

@Composable
fun FeedbackBanner(
    outcome: RoundOutcome?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = outcome != null,
            enter = fadeIn(tween(250)) + slideInVertically(initialOffsetY = { 20 }),
            exit = fadeOut(tween(150))
        ) {
            if (outcome != null) {
                val (text, color) = when (outcome) {
                    is RoundOutcome.Correct -> Pair("✓ Correct!", AccentMint)
                    is RoundOutcome.Wrong -> {
                        val symbol = when (outcome.correctAnswer) {
                            Comparison.LESS -> "‹"
                            Comparison.EQUAL -> "="
                            Comparison.GREATER -> "›"
                        }
                        Pair("✗ Answer: Left $symbol Right", AccentRed)
                    }
                    is RoundOutcome.Solved -> {
                        val symbol = when (outcome.correctAnswer) {
                            Comparison.LESS -> "‹"
                            Comparison.EQUAL -> "="
                            Comparison.GREATER -> "›"
                        }
                        Pair("✗ Answer: Left $symbol Right", AccentRed)
                    }
                    is RoundOutcome.TimedOut -> {
                        val symbol = when (outcome.correctAnswer) {
                            Comparison.LESS -> "‹"
                            Comparison.EQUAL -> "="
                            Comparison.GREATER -> "›"
                        }
                        Pair("⏱ Time's up! Answer: Left $symbol Right", AccentRed)
                    }
                    is RoundOutcome.HintUsed -> Pair("💡 Left side revealed", AccentAmber)
                }

                Text(
                    text = text,
                    color = color,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Preview
@Composable
fun FeedbackBannerPreview() {
    MindScaleTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FeedbackBanner(outcome = RoundOutcome.Correct(streakAfter = 5))
            FeedbackBanner(outcome = RoundOutcome.Wrong(correctAnswer = Comparison.LESS))
            FeedbackBanner(outcome = RoundOutcome.HintUsed)
        }
    }
}
