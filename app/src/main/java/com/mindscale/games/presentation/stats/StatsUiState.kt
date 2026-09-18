package com.mindscale.games.presentation.stats

import com.mindscale.games.data.local.DifficultyAccuracy

data class StatsUiState(
    val totalCorrect: Int = 0,
    val totalWrong: Int = 0,
    val bestStreak: Int = 0,
    val accuracyList: List<DifficultyAccuracy> = emptyList(),
    val isEmpty: Boolean = true
)
