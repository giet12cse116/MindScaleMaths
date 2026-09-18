package com.mindscale.games.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mindscale.games.presentation.theme.AccentRed
import com.mindscale.games.presentation.theme.MindScaleTheme
import com.mindscale.games.presentation.theme.TextSecondary

@Composable
fun LivesIndicator(
    livesRemaining: Int,
    totalLives: Int = 3,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalLives) { index ->
            val isAlive = index < livesRemaining

            val scale by animateFloatAsState(
                targetValue = if (isAlive) 1.0f else 0.75f,
                animationSpec = tween(durationMillis = 250),
                label = "heartScale"
            )
            val alpha by animateFloatAsState(
                targetValue = if (isAlive) 1.0f else 0.25f,
                animationSpec = tween(durationMillis = 250),
                label = "heartAlpha"
            )

            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = if (isAlive) AccentRed else TextSecondary,
                modifier = Modifier
                    .size(20.dp)
                    .scale(scale)
                    .alpha(alpha)
            )
        }
    }
}

@Preview
@Composable
fun LivesIndicatorPreview() {
    MindScaleTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LivesIndicator(livesRemaining = 3)
            LivesIndicator(livesRemaining = 2)
            LivesIndicator(livesRemaining = 1)
            LivesIndicator(livesRemaining = 0)
        }
    }
}
