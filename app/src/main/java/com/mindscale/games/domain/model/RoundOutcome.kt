package com.mindscale.games.domain.model

sealed class RoundOutcome {
    data class Correct(val streakAfter: Int) : RoundOutcome()
    data class Wrong(val correctAnswer: Comparison) : RoundOutcome()
    data class Solved(val correctAnswer: Comparison) : RoundOutcome()
    data class TimedOut(val correctAnswer: Comparison) : RoundOutcome()
    object HintUsed : RoundOutcome()
}
