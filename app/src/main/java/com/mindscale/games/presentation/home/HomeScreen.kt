package com.mindscale.games.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindscale.games.presentation.components.AnimatedBackgroundDots
import com.mindscale.games.presentation.components.ModeCard
import com.mindscale.games.presentation.components.StatChip
import com.mindscale.games.presentation.navigation.NavRoutes
import com.mindscale.games.presentation.theme.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigate: (String) -> Unit
) {
    val bestStreak by viewModel.bestStreak.collectAsState(initial = 0)

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
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header with Icon Row (Stats, Settings, Info)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "MINDSCALE",
                            color = TextPrimary,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 3.sp
                        )
                        Text(
                            text = "Math Comparison Challenge",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(onClick = { onNavigate(NavRoutes.Stats.route) }) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Stats",
                                tint = AccentMint
                            )
                        }
                        IconButton(onClick = { onNavigate(NavRoutes.Settings.route) }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = TextSecondary
                            )
                        }
                        IconButton(onClick = { onNavigate(NavRoutes.Info.route) }) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Info",
                                tint = TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Best Streak Hero Chip
                StatChip(
                    value = "$bestStreak",
                    label = "Best Streak",
                    accentColor = AccentAmber,
                    modifier = Modifier.fillMaxWidth(0.6f)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Mode Cards Selection
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "SELECT GAME MODE",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )

                    ModeCard(
                        title = "CLASSIC",
                        tagline = "Endless practice mode with no pressure timer",
                        accentColor = AccentMint,
                        icon = Icons.Default.PlayArrow,
                        onClick = { onNavigate(NavRoutes.LevelSelect.createRoute("classic")) }
                    )

                    ModeCard(
                        title = "ARCADE",
                        tagline = "3 lives, per-round timer countdown, high intensity",
                        accentColor = AccentPurple,
                        icon = Icons.Default.Refresh,
                        onClick = { onNavigate(NavRoutes.LevelSelect.createRoute("arcade")) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // AdMob Test Banner
                com.mindscale.games.presentation.components.BannerAd()
            }
        }
    }
}
