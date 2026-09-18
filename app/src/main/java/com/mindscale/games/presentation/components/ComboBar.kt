package com.mindscale.games.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindscale.games.presentation.theme.*

@Composable
fun ComboBar(
    progress: Float,
    multiplier: Int,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 300),
        label = "comboProgress"
    )

    var triggerPop by remember { mutableStateOf(false) }
    LaunchedEffect(multiplier) {
        if (multiplier > 1) {
            triggerPop = true
        }
    }

    val badgeScale by animateFloatAsState(
        targetValue = if (triggerPop) 1.25f else 1.0f,
        animationSpec = tween(durationMillis = 200),
        finishedListener = { triggerPop = false },
        label = "multiplierPop"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(28.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Track
        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(CircleShape)
                .background(SurfaceCardBorder)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .clip(CircleShape)
                    .background(AccentMint)
            )
        }

        // Multiplier Badge
        Box(
            modifier = Modifier
                .scale(badgeScale)
                .clip(RoundedCornerShape(8.dp))
                .background(if (multiplier > 1) AccentMint.copy(alpha = 0.2f) else SurfaceCard)
                .border(
                    width = 1.dp,
                    color = if (multiplier > 1) AccentMint else SurfaceCardBorder,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 8.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "×$multiplier",
                color = if (multiplier > 1) AccentMint else TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Preview
@Composable
fun ComboBarPreview() {
    MindScaleTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ComboBar(progress = 0.4f, multiplier = 1)
            ComboBar(progress = 0.8f, multiplier = 3)
        }
    }
}
