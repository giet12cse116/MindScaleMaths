package com.mindscale.games.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindscale.games.presentation.components.AnimatedBackgroundDots
import com.mindscale.games.presentation.components.PageIndicator
import com.mindscale.games.presentation.components.PrimaryButton
import com.mindscale.games.presentation.navigation.NavRoutes
import com.mindscale.games.presentation.theme.*
import kotlinx.coroutines.launch

data class OnboardingPageData(
    val iconEmoji: String,
    val headline: String,
    val description: String,
    val accentColor: Color
)

private val pages = listOf(
    OnboardingPageData(
        iconEmoji = "⚖️",
        headline = "Two Sides, One Truth",
        description = "Compare two expressions and decide: less, equal, or greater.",
        accentColor = AccentMint
    ),
    OnboardingPageData(
        iconEmoji = "🎯",
        headline = "Three Levels of Challenge",
        description = "Easy, Medium, and Hard scale the difficulty to match your skill.",
        accentColor = AccentPurple
    ),
    OnboardingPageData(
        iconEmoji = "⚡",
        headline = "Race the Clock",
        description = "Arcade mode adds pressure — chain correct answers for combo multipliers.",
        accentColor = AccentAmber
    ),
    OnboardingPageData(
        iconEmoji = "💡",
        headline = "Stuck? We've Got You",
        description = "Use Hint to reveal a side of the expression when you need help.",
        accentColor = AccentMint
    )
)

@Composable
fun WalkthroughScreen(
    viewModel: OnboardingViewModel,
    onNavigate: (String) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    val onFinishOnboarding = {
        viewModel.completeOnboarding {
            onNavigate(NavRoutes.Home.route)
        }
    }

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
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header Row with Skip button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (pagerState.currentPage < pages.size - 1) {
                        TextButton(
                            onClick = onFinishOnboarding
                        ) {
                            Text(
                                text = "Skip",
                                color = TextSecondary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Pager Content
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) { pageIndex ->
                    val page = pages[pageIndex]
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Glowing Icon Circle Card
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(SurfaceCard)
                                .border(
                                    width = 2.dp,
                                    color = page.accentColor.copy(alpha = 0.5f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = page.iconEmoji,
                                fontSize = 54.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(40.dp))

                        Text(
                            text = page.headline,
                            style = MaterialTheme.typography.displaySmall,
                            color = TextPrimary,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = page.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 24.sp
                        )
                    }
                }

                // Bottom Indicator, Native Ad, and Primary Action Button
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PageIndicator(
                        pageCount = pages.size,
                        currentPage = pagerState.currentPage
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Native Advanced Ad in between page indicator and action button
                    com.mindscale.games.presentation.components.NativeAd()

                    Spacer(modifier = Modifier.height(16.dp))

                    val isLastPage = pagerState.currentPage == pages.size - 1
                    PrimaryButton(
                        text = if (isLastPage) "Get Started" else "Next",
                        accentColor = AccentMint,
                        onClick = {
                            if (isLastPage) {
                                onFinishOnboarding()
                            } else {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
