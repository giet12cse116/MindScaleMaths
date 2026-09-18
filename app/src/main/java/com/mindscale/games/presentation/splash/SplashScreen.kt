package com.mindscale.games.presentation.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindscale.games.presentation.components.AnimatedBackgroundDots
import com.mindscale.games.presentation.navigation.NavRoutes
import com.mindscale.games.presentation.theme.AccentMint
import com.mindscale.games.presentation.theme.TextPrimary

import android.app.Activity
import android.content.ContextWrapper
import androidx.compose.ui.platform.LocalContext
import com.mindscale.games.core.ads.AppOpenAdManager

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    appOpenAdManager: AppOpenAdManager? = null,
    onNavigate: (String) -> Unit
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

    val destination by viewModel.destination.collectAsState()
    var hasHandledNavigation by remember { mutableStateOf(false) }

    LaunchedEffect(destination) {
        destination?.let { dest ->
            if (hasHandledNavigation) return@LaunchedEffect
            hasHandledNavigation = true

            val targetRoute = when (dest) {
                SplashDestination.Walkthrough -> NavRoutes.Walkthrough.route
                SplashDestination.Home -> NavRoutes.Home.route
            }

            if (activity != null && appOpenAdManager != null) {
                appOpenAdManager.showAdIfAvailableOrWait(activity, maxWaitMs = 2500L) {
                    onNavigate(targetRoute)
                }
            } else {
                onNavigate(targetRoute)
            }
        }
    }

    var startAnim by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        startAnim = true
    }

    val scale by animateFloatAsState(
        targetValue = if (startAnim) 1.0f else 0.8f,
        animationSpec = tween(durationMillis = 500),
        label = "logoScale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (startAnim) 1.0f else 0.0f,
        animationSpec = tween(durationMillis = 500),
        label = "logoAlpha"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedBackgroundDots()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .scale(scale)
                .alpha(alpha)
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = TextPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 48.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    ) {
                        append("Mind")
                    }
                    withStyle(
                        style = SpanStyle(
                            color = AccentMint,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 48.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    ) {
                        append("Scale")
                    }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "MATH COMPARISON GAME",
                color = AccentMint.copy(alpha = 0.7f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp
            )
        }
    }
}
