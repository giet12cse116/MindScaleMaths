package com.mindscale.games.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Test

class ScoreUseCaseTest {

    private val scoreUseCase = ScoreUseCase()

    @Test
    fun testOnCorrectIncrementsStreakAndComboProgress() {
        val initial = ScoreUseCase.ScoreState()
        val s1 = scoreUseCase.onCorrect(initial)

        assertEquals(1, s1.streak)
        assertEquals(1, s1.comboMultiplier)
        assertEquals(0.2f, s1.comboProgress, 0.001f)
    }

    @Test
    fun testComboMultiplierTierUpAtThreshold() {
        var state = ScoreUseCase.ScoreState()
        // 5 consecutive correct answers (0.2f * 5 = 1.0f)
        repeat(4) {
            state = scoreUseCase.onCorrect(state)
        }
        assertEquals(4, state.streak)
        assertEquals(1, state.comboMultiplier)
        assertEquals(0.8f, state.comboProgress, 0.001f)

        // 5th answer triggers tier up to multiplier = 2 and resets progress to 0f
        state = scoreUseCase.onCorrect(state)
        assertEquals(5, state.streak)
        assertEquals(2, state.comboMultiplier)
        assertEquals(0f, state.comboProgress, 0.001f)
    }

    @Test
    fun testOnWrongOrSolvedResetsStreakAndMultiplier() {
        val active = ScoreUseCase.ScoreState(streak = 10, comboMultiplier = 3, comboProgress = 0.6f)
        val reset = scoreUseCase.onWrongOrSolved(active)

        assertEquals(0, reset.streak)
        assertEquals(1, reset.comboMultiplier)
        assertEquals(0f, reset.comboProgress, 0.001f)
    }
}
