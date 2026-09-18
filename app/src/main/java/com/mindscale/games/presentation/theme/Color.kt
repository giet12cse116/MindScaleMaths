package com.mindscale.games.presentation.theme

import androidx.compose.ui.graphics.Color

val BackgroundDeep = Color(0xFF0B0F1A)     // near-black navy, app background
val SurfaceCard = Color(0xFF121826)        // card backgrounds
val SurfaceCardBorder = Color(0xFF232B3D)  // idle card border

val AccentMint = Color(0xFF2EE6A8)         // primary accent / "correct" green
val AccentRed = Color(0xFFFF4D6D)          // "wrong" state
val AccentAmber = Color(0xFFFFC94D)        // hint / warning state
val AccentPurple = Color(0xFF8B7CFF)       // arcade / secondary accent
val AccentCyan = Color(0xFF38BDF8)         // info / share accent

val TextPrimary = Color(0xFFF5F7FA)
val TextSecondary = Color(0xFF8A93A6)

enum class CardState { IDLE, HINT, CORRECT, WRONG }

fun CardState.borderColor(): Color = when (this) {
    CardState.IDLE -> SurfaceCardBorder
    CardState.HINT -> AccentAmber
    CardState.CORRECT -> AccentMint
    CardState.WRONG -> AccentRed
}
