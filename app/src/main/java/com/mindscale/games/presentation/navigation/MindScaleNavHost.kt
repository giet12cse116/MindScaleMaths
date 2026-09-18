package com.mindscale.games.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mindscale.games.MindScaleApplication
import com.mindscale.games.core.ads.AdManager
import com.mindscale.games.core.analytics.AnalyticsManager
import com.mindscale.games.core.haptics.HapticManager
import com.mindscale.games.core.sound.SoundManager
import com.mindscale.games.data.local.RoundHistoryDatabase
import com.mindscale.games.data.local.UserPrefsRepository
import com.mindscale.games.data.repository.StatsRepository
import com.mindscale.games.domain.generator.ExpressionGeneratorImpl
import com.mindscale.games.domain.model.Difficulty
import com.mindscale.games.domain.model.GameMode
import com.mindscale.games.domain.usecase.EvaluateAnswerUseCase
import com.mindscale.games.domain.usecase.GenerateRoundUseCase
import com.mindscale.games.domain.usecase.ScoreUseCase
import com.mindscale.games.presentation.game.GameScreen
import com.mindscale.games.presentation.game.GameViewModel
import com.mindscale.games.presentation.game.GameViewModelFactory
import com.mindscale.games.presentation.home.HomeScreen
import com.mindscale.games.presentation.home.HomeViewModel
import com.mindscale.games.presentation.home.HomeViewModelFactory
import com.mindscale.games.presentation.info.InfoScreen
import com.mindscale.games.presentation.levelselect.LevelSelectScreen
import com.mindscale.games.presentation.onboarding.OnboardingViewModel
import com.mindscale.games.presentation.onboarding.OnboardingViewModelFactory
import com.mindscale.games.presentation.onboarding.WalkthroughScreen
import com.mindscale.games.presentation.results.ResultsScreen
import com.mindscale.games.presentation.settings.SettingsScreen
import com.mindscale.games.presentation.settings.SettingsViewModel
import com.mindscale.games.presentation.settings.SettingsViewModelFactory
import com.mindscale.games.presentation.splash.SplashScreen
import com.mindscale.games.presentation.splash.SplashViewModel
import com.mindscale.games.presentation.splash.SplashViewModelFactory
import com.mindscale.games.presentation.stats.StatsScreen
import com.mindscale.games.presentation.stats.StatsViewModel
import com.mindscale.games.presentation.stats.StatsViewModelFactory

@Composable
fun MindScaleNavHost(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current.applicationContext
    val userPrefsRepository = remember(context) { UserPrefsRepository(context) }
    val database = remember(context) { RoundHistoryDatabase.getInstance(context) }
    val statsRepository = remember(database) { StatsRepository(database.roundHistoryDao()) }

    val soundManager = remember(context) { SoundManager(context, userPrefsRepository) }
    val hapticManager = remember(context) { HapticManager(context, userPrefsRepository) }
    val analyticsManager = remember(context) { AnalyticsManager(context) }
    val adManager = remember(context) { AdManager(context) }

    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            backStackEntry.destination.route?.let { route ->
                analyticsManager.logScreenView(route)
            }
        }
    }

    // Shared domain use cases
    val generator = remember { ExpressionGeneratorImpl() }
    val generateRoundUseCase = remember { GenerateRoundUseCase(generator) }
    val evaluateAnswerUseCase = remember { EvaluateAnswerUseCase() }
    val scoreUseCase = remember { ScoreUseCase() }

    NavHost(
        navController = navController,
        startDestination = NavRoutes.Splash.route,
        enterTransition = { fadeIn(tween(300)) + slideInHorizontally(tween(300)) { fullWidth -> fullWidth / 4 } },
        exitTransition = { fadeOut(tween(250)) + slideOutHorizontally(tween(250)) { fullWidth -> -fullWidth / 4 } },
        popEnterTransition = { fadeIn(tween(300)) + slideInHorizontally(tween(300)) { fullWidth -> -fullWidth / 4 } },
        popExitTransition = { fadeOut(tween(250)) + slideOutHorizontally(tween(250)) { fullWidth -> fullWidth / 4 } }
    ) {
        composable(NavRoutes.Splash.route) {
            val splashViewModel: SplashViewModel = viewModel(
                factory = SplashViewModelFactory(userPrefsRepository)
            )
            val appOpenAdManager = remember(context) {
                (context as? MindScaleApplication)?.appOpenAdManager
            }
            SplashScreen(
                viewModel = splashViewModel,
                appOpenAdManager = appOpenAdManager,
                onNavigate = { destination ->
                    navController.navigate(destination) {
                        popUpTo(NavRoutes.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.Walkthrough.route) {
            val onboardingViewModel: OnboardingViewModel = viewModel(
                factory = OnboardingViewModelFactory(userPrefsRepository)
            )
            WalkthroughScreen(
                viewModel = onboardingViewModel,
                onNavigate = { destination ->
                    navController.navigate(destination) {
                        popUpTo(NavRoutes.Walkthrough.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.Home.route) {
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModelFactory(userPrefsRepository)
            )
            HomeScreen(
                viewModel = homeViewModel,
                onNavigate = { route ->
                    navController.navigate(route)
                }
            )
        }

        composable(
            route = NavRoutes.LevelSelect.route,
            arguments = listOf(
                navArgument("mode") { type = NavType.StringType }
            )
        ) { navBackStackEntry ->
            val mode = navBackStackEntry.arguments?.getString("mode") ?: "classic"
            LevelSelectScreen(
                mode = mode,
                onNavigate = { route -> navController.navigate(route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.Settings.route) {
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModelFactory(userPrefsRepository, statsRepository)
            )
            SettingsScreen(
                viewModel = settingsViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.Info.route) {
            InfoScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.Stats.route) {
            val statsViewModel: StatsViewModel = viewModel(
                factory = StatsViewModelFactory(statsRepository, userPrefsRepository)
            )
            StatsScreen(
                viewModel = statsViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = NavRoutes.Game.route,
            arguments = listOf(
                navArgument("mode") { type = NavType.StringType },
                navArgument("difficulty") { type = NavType.StringType }
            )
        ) { navBackStackEntry ->
            val modeStr = navBackStackEntry.arguments?.getString("mode") ?: "classic"
            val diffStr = navBackStackEntry.arguments?.getString("difficulty") ?: "normal"

            val gameMode = if (modeStr.equals("arcade", ignoreCase = true)) GameMode.ARCADE else GameMode.CLASSIC
            val difficultyEnum = when (diffStr.lowercase()) {
                "medium" -> Difficulty.MEDIUM
                "hard" -> Difficulty.HARD
                else -> Difficulty.EASY
            }

            val gameViewModel: GameViewModel = viewModel(
                factory = GameViewModelFactory(
                    generateRound = generateRoundUseCase,
                    evaluateAnswer = evaluateAnswerUseCase,
                    scoreUseCase = scoreUseCase,
                    userPrefsRepository = userPrefsRepository,
                    initialMode = gameMode,
                    initialDifficulty = difficultyEnum,
                    soundManager = soundManager,
                    hapticManager = hapticManager,
                    statsRepository = statsRepository,
                    analyticsManager = analyticsManager
                )
            )

            GameScreen(
                mode = modeStr,
                difficulty = diffStr,
                onNavigate = { route ->
                    if (route.startsWith("results")) {
                        navController.navigate(route) {
                            popUpTo(NavRoutes.Game.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(route)
                    }
                },
                onBack = { navController.popBackStack() },
                viewModel = gameViewModel,
                adManager = adManager
            )
        }

        composable(
            route = NavRoutes.Results.route,
            arguments = listOf(
                navArgument("mode") { type = NavType.StringType }
            )
        ) { navBackStackEntry ->
            val mode = navBackStackEntry.arguments?.getString("mode") ?: "classic"
            ResultsScreen(
                mode = mode,
                onNavigate = { route ->
                    if (route == NavRoutes.Home.route) {
                        navController.navigate(NavRoutes.Home.route) {
                            popUpTo(NavRoutes.Home.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(route)
                    }
                }
            )
        }
    }
}
