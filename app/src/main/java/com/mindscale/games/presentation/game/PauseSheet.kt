package com.mindscale.games.presentation.game

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindscale.games.presentation.components.PrimaryButton
import com.mindscale.games.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PauseSheet(
    onDismissRequest: () -> Unit,
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onQuitHome: () -> Unit
) {
    var showQuitDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = SurfaceCard,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "GAME PAUSED",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            PrimaryButton(
                text = "RESUME",
                accentColor = AccentMint,
                onClick = {
                    onResume()
                    onDismissRequest()
                }
            )

            OutlinedButton(
                onClick = {
                    onRestart()
                    onDismissRequest()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(50),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(AccentPurple)
                )
            ) {
                Text(
                    text = "RESTART RUN",
                    color = AccentPurple,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            TextButton(
                onClick = { showQuitDialog = true }
            ) {
                Text(
                    text = "Quit to Home",
                    color = AccentRed,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showQuitDialog) {
        AlertDialog(
            onDismissRequest = { showQuitDialog = false },
            title = {
                Text(
                    text = "Quit Session?",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "This will forfeit your current Arcade run.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showQuitDialog = false
                        onDismissRequest()
                        onQuitHome()
                    }
                ) {
                    Text(
                        text = "Quit",
                        color = AccentRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showQuitDialog = false }) {
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
