package com.mindscale.games.presentation.game

import com.mindscale.games.domain.model.Difficulty
import com.mindscale.games.domain.model.GameMode
import com.mindscale.games.domain.model.Round
import com.mindscale.games.domain.model.RoundOutcome
import com.mindscale.games.presentation.results.GameSessionSummary
import com.mindscale.games.presentation.theme.CardState

data class GameSessionState(
    val mode: GameMode,
    val difficulty: Difficulty,
    val correct: Int = 0,
    val wrong: Int = 0,
    val streak: Int = 0,
    val comboMultiplier: Int = 1,
    val comboProgress: Float = 0f,
    val livesRemaining: Int = 3,
    val hintsRemaining: Int = 3,
    val secondsLeft: Int? = null
)

data class GameUiState(
    val session: GameSessionState,
    val round: Round,
    val leftState: CardState = CardState.IDLE,
    val rightState: CardState = CardState.IDLE,
    val revealedLeftValue: Double? = null,
    val revealedRightValue: Double? = null,
    val outcome: RoundOutcome? = null,
    val isAnswered: Boolean = false,
    val hintUsed: Boolean = false,
    val showOutOfLivesDialog: Boolean = false
)

sealed class NavEvent {
    data class ToResults(val summary: GameSessionSummary) : NavEvent()
}
