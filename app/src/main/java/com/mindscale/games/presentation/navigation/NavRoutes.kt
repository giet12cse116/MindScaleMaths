package com.mindscale.games.presentation.navigation

sealed class NavRoutes(val route: String) {
    object Splash : NavRoutes("splash")
    object Walkthrough : NavRoutes("walkthrough")
    object Home : NavRoutes("home")
    object LevelSelect : NavRoutes("level_select/{mode}") {
        fun createRoute(mode: String) = "level_select/$mode"
    }
    object Game : NavRoutes("game/{mode}/{difficulty}") {
        fun createRoute(mode: String, difficulty: String) = "game/$mode/$difficulty"
    }
    object Results : NavRoutes("results/{mode}") {
        fun createRoute(mode: String) = "results/$mode"
    }
    object Settings : NavRoutes("settings")
    object Info : NavRoutes("info")
    object Stats : NavRoutes("stats")
}
