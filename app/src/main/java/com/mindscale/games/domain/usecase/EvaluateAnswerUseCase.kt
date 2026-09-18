package com.mindscale.games.domain.usecase

import com.mindscale.games.domain.model.Comparison
import com.mindscale.games.domain.model.Round
import com.mindscale.games.domain.model.RoundOutcome

class EvaluateAnswerUseCase {
    operator fun invoke(round: Round, chosen: Comparison, currentStreak: Int): RoundOutcome {
        return if (chosen == round.correctAnswer) {
            RoundOutcome.Correct(streakAfter = currentStreak + 1)
        } else {
            RoundOutcome.Wrong(correctAnswer = round.correctAnswer)
        }
    }
}
