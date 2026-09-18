package com.mindscale.games.presentation.settings

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
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
import com.mindscale.games.presentation.components.AnimatedBackgroundDots
import com.mindscale.games.presentation.components.SettingRow
import com.mindscale.games.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val soundEnabled = uiState.soundEnabled
    val hapticsEnabled = uiState.hapticsEnabled

    var showResetDialog by remember { mutableStateOf(false) }

    val switchColors = SwitchDefaults.colors(
        checkedThumbColor = AccentMint,
        checkedTrackColor = AccentMint.copy(alpha = 0.4f),
        uncheckedThumbColor = TextSecondary,
        uncheckedTrackColor = SurfaceCardBorder
    )

    fun openPlayStore(context: Context) {
        val packageName = context.packageName
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
            )
        }
    }

    fun shareApp(context: Context) {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                "Check out MindScale Maths, an awesome math comparison puzzle game! https://play.google.com/store/apps/details?id=${context.packageName}"
            )
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share MindScale Maths")
        context.startActivity(shareIntent)
    }

    Scaffold(
        containerColor = BackgroundDeep,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SETTINGS",
                        color = TextPrimary,
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
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Settings Card Container
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, SurfaceCardBorder, RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                ) {
                    Column {
                        SettingRow(
                            label = "Sound Effects",
                            trailing = {
                                Switch(
                                    checked = soundEnabled,
                                    onCheckedChange = { viewModel.toggleSound(it) },
                                    colors = switchColors
                                )
                            }
                        )

                        HorizontalDivider(color = SurfaceCardBorder, thickness = 1.dp)

                        SettingRow(
                            label = "Haptics & Vibration",
                            trailing = {
                                Switch(
                                    checked = hapticsEnabled,
                                    onCheckedChange = { viewModel.toggleHaptics(it) },
                                    colors = switchColors
                                )
                            }
                        )

                        HorizontalDivider(color = SurfaceCardBorder, thickness = 1.dp)

                        SettingRow(
                            label = "Rate Us",
                            onClick = { openPlayStore(context) },
                            trailing = {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Rate Us",
                                    tint = AccentAmber
                                )
                            }
                        )

                        HorizontalDivider(color = SurfaceCardBorder, thickness = 1.dp)

                        SettingRow(
                            label = "Share App",
                            onClick = { shareApp(context) },
                            trailing = {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share App",
                                    tint = AccentCyan
                                )
                            }
                        )

                        HorizontalDivider(color = SurfaceCardBorder, thickness = 1.dp)

                        SettingRow(
                            label = "Reset Game Progress",
                            trailing = {
                                TextButton(onClick = { showResetDialog = true }) {
                                    Text(
                                        text = "Reset",
                                        color = AccentRed,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        )

                        HorizontalDivider(color = SurfaceCardBorder, thickness = 1.dp)

                        SettingRow(
                            label = "App Version",
                            trailing = {
                                Text(
                                    text = "1.0.1",
                                    color = TextSecondary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Banner Ad below settings card
                com.mindscale.games.presentation.components.BannerAd()
            }
        }
    }

    // Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text(
                    text = "Reset game progress?",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "All your stats, scores, and streak data will be cleared. This action cannot be undone.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetGameProgress()
                        showResetDialog = false
                    }
                ) {
                    Text(
                        text = "Reset",
                        color = AccentRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(
                        text = "Cancel",
                        color = TextSecondary
                    )
                }
            },
            containerColor = SurfaceCard,
            shape = RoundedCornerShape(20.dp)
        )
    }
}
