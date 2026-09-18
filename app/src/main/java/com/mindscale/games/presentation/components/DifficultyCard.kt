package com.mindscale.games.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindscale.games.presentation.theme.*

@Composable
fun DifficultyCard(
    label: String,
    descriptor: String,
    starCount: Int,
    accentColor: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.02f else 1.0f,
        animationSpec = tween(durationMillis = 200),
        label = "selectedScale"
    )

    val borderAlpha by animateFloatAsState(
        targetValue = if (selected) 1.0f else 0.30f,
        animationSpec = tween(durationMillis = 200),
        label = "borderAlpha"
    )

    val borderColor by animateColorAsState(
        targetValue = accentColor.copy(alpha = borderAlpha),
        animationSpec = tween(durationMillis = 200),
        label = "borderColor"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .then(
                if (selected) {
                    Modifier.drawBehind {
                        drawRoundRect(
                            color = accentColor.copy(alpha = 0.20f),
                            size = size.copy(
                                width = size.width + 8.dp.toPx(),
                                height = size.height + 8.dp.toPx()
                            ),
                            topLeft = Offset(-4.dp.toPx(), -4.dp.toPx()),
                            cornerRadius = CornerRadius(24.dp.toPx())
                        )
                    }
                } else Modifier
            )
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceCard)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Header: Stars + Label
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(3) { index ->
                            val isFilled = index < starCount
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (isFilled) accentColor else TextSecondary.copy(alpha = 0.25f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = label.uppercase(),
                        color = accentColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Descriptor
                Text(
                    text = descriptor,
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Radio/Checkmark Circle
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (selected) accentColor else SurfaceCardBorder)
                    .border(
                        width = 1.dp,
                        color = if (selected) accentColor else SurfaceCardBorder,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = BackgroundDeep,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun DifficultyCardPreview() {
    MindScaleTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DifficultyCard(
                label = "EASY",
                descriptor = "2 terms · + −",
                starCount = 1,
                accentColor = AccentMint,
                selected = true,
                onClick = {}
            )
            DifficultyCard(
                label = "MEDIUM",
                descriptor = "3 terms · + − × ÷",
                starCount = 2,
                accentColor = AccentAmber,
                selected = false,
                onClick = {}
            )
            DifficultyCard(
                label = "HARD",
                descriptor = "3-4 terms · with ( )",
                starCount = 3,
                accentColor = AccentRed,
                selected = false,
                onClick = {}
            )
        }
    }
}
