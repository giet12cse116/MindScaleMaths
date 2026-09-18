package com.mindscale.games.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindscale.games.presentation.theme.BackgroundDeep
import com.mindscale.games.presentation.theme.MindScaleTheme
import com.mindscale.games.presentation.theme.SurfaceCard
import com.mindscale.games.presentation.theme.TextPrimary
import com.mindscale.games.presentation.theme.TextSecondary

@Composable
fun SettingRow(
    label: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    trailing: @Composable () -> Unit
) {
    val rowModifier = if (onClick != null) {
        modifier.clickable(onClick = onClick)
    } else {
        modifier
    }
    Row(
        modifier = rowModifier
            .fillMaxWidth()
            .background(SurfaceCard)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        trailing()
    }
}

@Preview
@Composable
fun SettingRowPreview() {
    MindScaleTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(BackgroundDeep)
        ) {
            SettingRow(
                label = "Sound Effects",
                trailing = { Switch(checked = true, onCheckedChange = {}) }
            )
            Spacer(modifier = Modifier.height(8.dp))
            SettingRow(
                label = "App Version",
                trailing = { Text("1.0.0", color = TextSecondary, fontSize = 14.sp) }
            )
        }
    }
}
