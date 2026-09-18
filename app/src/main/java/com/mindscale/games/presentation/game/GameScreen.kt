package com.mindscale.games.presentation.game

import android.app.Activity
import android.content.ContextWrapper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindscale.games.core.ads.AdManager
import com.mindscale.games.domain.generator.timerSecondsFor
import com.mindscale.games.domain.model.Difficulty
import com.mindscale.games.domain.model.GameMode
import com.mindscale.games.presentation.components.*
import com.mindscale.games.presentation.navigation.NavRoutes
import com.mindscale.games.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    mode: String,
    difficulty: String,
    onNavigate: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: GameViewModel,
    adManager: AdManager? = null
) {
    val context = LocalContext.current
    val activity = remember(context) {
        var ctx = context
        while (ctx is ContextWrapper) {
            if (ctx is Activity) break
            ctx = ctx.baseContext
        }
        ctx as? Activity
    }

    val uiState by viewModel.uiState.collectAsState()
    val session = uiState.session
    val round = uiState.round

    val isArcade = session.mode == GameMode.ARCADE
    val modeAccentColor = if (isArcade) AccentPurple else AccentMint
    val modeTitle = if (isArcade) "ARCADE" else "CLASSIC"

    var showPauseSheet by remember { mutableStateOf(false) }
    var scratchPadText by remember { mutableStateOf("") }
    var hasBannerTriggered by remember { mutableStateOf(false) }

    LaunchedEffect(session.correct, session.wrong, uiState.isAnswered) {
        if (uiState.isAnswered) {
            hasBannerTriggered = true
        } else if (session.correct == 0 && session.wrong == 0) {
            hasBannerTriggered = false
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navEvent.collect { event ->
            when (event) {
                is NavEvent.ToResults -> {
                    onNavigate(NavRoutes.Results.createRoute(mode))
                }
            }
        }
    }

    Scaffold(
        containerColor = BackgroundDeep,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Static Mode Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(modeAccentColor.copy(alpha = 0.15f))
                                .border(1.dp, modeAccentColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = modeTitle,
                                color = modeAccentColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                        }

                        // Difficulty Tabs
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Difficulty.values().forEach { diff ->
                                val isSelected = session.difficulty == diff
                                val tabColor = when (diff) {
                                    Difficulty.EASY -> AccentMint
                                    Difficulty.MEDIUM -> AccentAmber
                                    Difficulty.HARD -> AccentRed
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) tabColor.copy(alpha = 0.2f) else SurfaceCard)
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) tabColor else SurfaceCardBorder,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            scratchPadText = ""
                                            hasBannerTriggered = false
                                            viewModel.onDifficultyChange(diff)
                                        }
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = diff.name,
                                        color = if (isSelected) tabColor else TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (isArcade) {
                                viewModel.pauseTimer()
                                showPauseSheet = true
                            } else {
                                onBack()
                            }
                        }
                    ) {
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
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top Section: Stats, Timer & Combo
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Stat & Arcade Controls Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isArcade) {
                            TimerRing(
                                secondsLeft = session.secondsLeft ?: 0,
                                totalSeconds = timerSecondsFor(session.difficulty)
                            )

                            LivesIndicator(livesRemaining = session.livesRemaining)
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = if (isArcade) Modifier else Modifier.fillMaxWidth()
                        ) {
                            StatChip(
                                value = "${session.correct}",
                                label = "Correct",
                                accentColor = AccentMint,
                                modifier = Modifier.weight(1f)
                            )
                            StatChip(
                                value = "${session.wrong}",
                                label = "Wrong",
                                accentColor = AccentRed,
                                modifier = Modifier.weight(1f)
                            )
                            StatChip(
                                value = "${session.streak}",
                                label = "Streak",
                                accentColor = AccentAmber,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Combo Bar
                    ComboBar(
                        progress = session.comboProgress,
                        multiplier = session.comboMultiplier
                    )
                }

                // Middle Section: Expression Cards + Center Answer Rail
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ExpressionCard(
                        label = "LEFT",
                        expression = round.left.display,
                        revealedValue = uiState.revealedLeftValue,
                        state = uiState.leftState,
                        modifier = Modifier
                            .weight(1f)
                            .height(170.dp)
                    )

                    CenterAnswerRail(
                        onAnswer = { choice -> viewModel.onCompare(choice) },
                        enabled = !uiState.isAnswered
                    )

                    ExpressionCard(
                        label = "RIGHT",
                        expression = round.right.display,
                        revealedValue = uiState.revealedRightValue,
                        state = uiState.rightState,
                        modifier = Modifier
                            .weight(1f)
                            .height(170.dp)
                    )
                }
                // Feedback Banner
                FeedbackBanner(outcome = uiState.outcome)

                // Bottom Action Column
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Hint Button Section
                    val hintsLeft = uiState.session.hintsRemaining
                    val isOutOfHints = hintsLeft <= 0
                    val hintColor = if (isOutOfHints) AccentPurple else AccentAmber
                    val hintText = if (isOutOfHints) "💡 Hint (Watch ad +1)" else "💡 Hint ($hintsLeft)"

                    OutlinedButton(
                        onClick = { viewModel.onHintClicked(activity, adManager) },
                        enabled = !uiState.isAnswered && !uiState.hintUsed,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = hintColor
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(
                                if (!uiState.isAnswered && !uiState.hintUsed) hintColor.copy(alpha = 0.5f) else SurfaceCardBorder
                            )
                        )
                    ) {
                        Text(
                            text = hintText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    // Banner Ad shown persistently in Classic mode once triggered
                    AnimatedVisibility(
                        visible = !isArcade && hasBannerTriggered,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        com.mindscale.games.presentation.components.BannerAd()
                    }

                    // Next Round Button
                    PrimaryButton(
                        text = "NEXT ROUND",
                        enabled = uiState.isAnswered,
                        accentColor = AccentMint,
                        onClick = {
                            scratchPadText = ""
                            viewModel.onNext()
                        }
                    )
                }

                // Scratch Pad
                ScratchPad(
                    text = scratchPadText,
                    onTextChange = { scratchPadText = it },
                    onClear = { scratchPadText = "" }
                )
            }
        }
    }

    // Out of Hearts (Lives Exhausted) Rewarded Ad Dialog
    if (uiState.showOutOfLivesDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.giveUpSession() },
            title = {
                Text(
                    text = "OUT OF HEARTS!",
                    color = AccentRed,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            },
            text = {
                Text(
                    text = "Watch a short ad to receive +1 Heart and keep your streak alive, or give up.",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        activity?.let { act ->
                            var rewardEarned = false
                            adManager?.showRewardedAd(
                                activity = act,
                                onRewardEarned = {
                                    rewardEarned = true
                                    viewModel.onRewardedHeartEarned()
                                },
                                onAdDismissed = {
                                    if (!rewardEarned) {
                                        viewModel.giveUpSession()
                                    }
                                }
                            )
                        } ?: viewModel.giveUpSession()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "📺 WATCH AD",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.giveUpSession() }) {
                    Text(
                        text = "Give Up",
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            containerColor = SurfaceCard,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Arcade Pause Sheet
    if (showPauseSheet) {
        PauseSheet(
            onDismissRequest = { showPauseSheet = false },
            onResume = { viewModel.resumeTimer() },
            onRestart = {
                scratchPadText = ""
                hasBannerTriggered = false
                viewModel.restartSession()
            },
            onQuitHome = {
                viewModel.cancelTimer()
                onBack()
            }
        )
    }
}
