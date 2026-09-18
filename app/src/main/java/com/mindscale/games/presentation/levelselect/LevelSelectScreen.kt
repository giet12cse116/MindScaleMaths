package com.mindscale.games.presentation.levelselect

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindscale.games.domain.model.Difficulty
import com.mindscale.games.presentation.components.AnimatedBackgroundDots
import com.mindscale.games.presentation.components.BannerAd
import com.mindscale.games.presentation.components.DifficultyCard
import com.mindscale.games.presentation.components.PrimaryButton
import com.mindscale.games.presentation.navigation.NavRoutes
import com.mindscale.games.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelSelectScreen(
    mode: String,
    onNavigate: (String) -> Unit,
    onBack: () -> Unit
) {
    var selectedDifficulty by remember { mutableStateOf<Difficulty?>(Difficulty.EASY) }

    val isArcade = mode.equals("arcade", ignoreCase = true)
    val modeAccentColor = if (isArcade) AccentPurple else AccentMint
    val modeTitle = if (isArcade) "ARCADE" else "CLASSIC"

    Scaffold(
        containerColor = BackgroundDeep,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = modeTitle,
                        color = modeAccentColor,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDeep
                )
            )
        }
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
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Subtitle
                Text(
                    text = "Choose your difficulty",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Difficulty Cards Stack
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DifficultyCard(
                        label = "EASY",
                        descriptor = "2 terms · + −",
                        starCount = 1,
                        accentColor = AccentMint,
                        selected = selectedDifficulty == Difficulty.EASY,
                        onClick = { selectedDifficulty = Difficulty.EASY }
                    )

                    DifficultyCard(
                        label = "MEDIUM",
                        descriptor = "3 terms · + − × ÷",
                        starCount = 2,
                        accentColor = AccentAmber,
                        selected = selectedDifficulty == Difficulty.MEDIUM,
                        onClick = { selectedDifficulty = Difficulty.MEDIUM }
                    )

                    DifficultyCard(
                        label = "HARD",
                        descriptor = "3-4 terms · with ( )",
                        starCount = 3,
                        accentColor = AccentRed,
                        selected = selectedDifficulty == Difficulty.HARD,
                        onClick = { selectedDifficulty = Difficulty.HARD }
                    )
                }

                // Pinned PLAY Button
                PrimaryButton(
                    text = "PLAY",
                    enabled = selectedDifficulty != null,
                    accentColor = modeAccentColor,
                    onClick = {
                        selectedDifficulty?.let { diff ->
                            onNavigate(
                                NavRoutes.Game.createRoute(
                                    mode = mode,
                                    difficulty = diff.name.lowercase()
                                )
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Ad Banner under Continue / Play button
                BannerAd()
            }
        }
    }
}

@Preview
@Composable
fun LevelSelectScreenClassicPreview() {
    MindScaleTheme {
        LevelSelectScreen(
            mode = "classic",
            onNavigate = {},
            onBack = {}
        )
    }
}

@Preview
@Composable
fun LevelSelectScreenArcadePreview() {
    MindScaleTheme {
        LevelSelectScreen(
            mode = "arcade",
            onNavigate = {},
            onBack = {}
        )
    }
}
