package com.mindscale.games.domain.usecase

class ScoreUseCase {

    companion object {
        const val COMBO_STEP = 0.2f
    }

    data class ScoreState(
        val streak: Int = 0,
        val comboMultiplier: Int = 1,
        val comboProgress: Float = 0f
    )

    fun onCorrect(current: ScoreState): ScoreState {
        val newStreak = current.streak + 1
        val newProgress = current.comboProgress + COMBO_STEP
        return if (newProgress >= 0.999f) {
            ScoreState(
                streak = newStreak,
                comboMultiplier = current.comboMultiplier + 1,
                comboProgress = 0f
            )
        } else {
            ScoreState(
                streak = newStreak,
                comboMultiplier = current.comboMultiplier,
                comboProgress = newProgress
            )
        }
    }

    fun onWrongOrSolved(current: ScoreState): ScoreState {
        return ScoreState(
            streak = 0,
            comboMultiplier = 1,
            comboProgress = 0f
        )
    }
}
