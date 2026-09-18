package com.mindscale.games.presentation.results

import com.mindscale.games.domain.model.Difficulty
import com.mindscale.games.domain.model.GameMode

data class GameSessionSummary(
    val mode: GameMode,
    val difficulty: Difficulty,
    val correct: Int,
    val wrong: Int,
    val bestStreakThisRun: Int,
    val isNewBest: Boolean
)

object ResultsSessionHolder {
    var lastSummary: GameSessionSummary? = null
}
