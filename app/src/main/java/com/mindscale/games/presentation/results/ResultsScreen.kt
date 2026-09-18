package com.mindscale.games.presentation.results

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindscale.games.domain.model.Difficulty
import com.mindscale.games.domain.model.GameMode
import com.mindscale.games.presentation.components.AnimatedBackgroundDots
import com.mindscale.games.presentation.components.PrimaryButton
import com.mindscale.games.presentation.components.StatChip
import com.mindscale.games.presentation.navigation.NavRoutes
import com.mindscale.games.presentation.theme.*

@Composable
fun ResultsScreen(
    mode: String,
    onNavigate: (String) -> Unit
) {
    val summary = ResultsSessionHolder.lastSummary ?: GameSessionSummary(
        mode = if (mode.equals("arcade", ignoreCase = true)) GameMode.ARCADE else GameMode.CLASSIC,
        difficulty = Difficulty.EASY,
        correct = 0,
        wrong = 0,
        bestStreakThisRun = 0,
        isNewBest = false
    )

    var animateIn by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        animateIn = true
    }

    val contentAlpha by animateFloatAsState(
        targetValue = if (animateIn) 1.0f else 0.0f,
        animationSpec = tween(durationMillis = 400),
        label = "contentAlpha"
    )

    val badgeScale by animateFloatAsState(
        targetValue = if (animateIn) 1.0f else 0.3f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "bouncyBadgeScale"
    )

    Scaffold(
        containerColor = BackgroundDeep
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedBackgroundDots()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .alpha(contentAlpha),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Title & Celebration Badge
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "RUN COMPLETE",
                        color = TextPrimary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (summary.isNewBest) {
                        Box(
                            modifier = Modifier
                                .scale(badgeScale)
                                .clip(RoundedCornerShape(50))
                                .background(AccentAmber.copy(alpha = 0.2f))
                                .border(1.5.dp, AccentAmber, RoundedCornerShape(50))
                                .padding(horizontal = 20.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "🏆 NEW BEST STREAK!",
                                color = AccentAmber,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                // Summary Stats Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.dp, SurfaceCardBorder, RoundedCornerShape(24.dp)),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "SESSION SUMMARY",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatChip(
                                value = "${summary.correct}",
                                label = "Correct",
                                accentColor = AccentMint,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            StatChip(
                                value = "${summary.wrong}",
                                label = "Wrong",
                                accentColor = AccentRed,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            StatChip(
                                value = "${summary.bestStreakThisRun}",
                                label = "Best Streak",
                                accentColor = AccentAmber,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Action Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PrimaryButton(
                        text = "PLAY AGAIN",
                        accentColor = AccentMint,
                        onClick = {
                            onNavigate(
                                NavRoutes.Game.createRoute(
                                    mode = summary.mode.name.lowercase(),
                                    difficulty = summary.difficulty.name.lowercase()
                                )
                            )
                        }
                    )

                    // Native Advanced Ad in between PLAY AGAIN and HOME buttons
                    com.mindscale.games.presentation.components.NativeAd()

                    OutlinedButton(
                        onClick = { onNavigate(NavRoutes.Home.route) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(50),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(SurfaceCardBorder)
                        )
                    ) {
                        Text(
                            text = "HOME",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Preview
@Composable
fun ResultsScreenPreview() {
    MindScaleTheme {
        ResultsSessionHolder.lastSummary = GameSessionSummary(
            mode = GameMode.ARCADE,
            difficulty = Difficulty.MEDIUM,
            correct = 12,
            wrong = 3,
            bestStreakThisRun = 8,
            isNewBest = true
        )
        ResultsScreen(
            mode = "arcade",
            onNavigate = {}
        )
    }
}
