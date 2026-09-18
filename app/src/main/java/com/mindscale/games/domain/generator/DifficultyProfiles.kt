package com.mindscale.games.domain.generator

import com.mindscale.games.domain.model.Difficulty
import com.mindscale.games.domain.model.DifficultyProfile

fun profileFor(difficulty: Difficulty): DifficultyProfile = when (difficulty) {
    Difficulty.EASY -> DifficultyProfile(
        operandRange = 1..12,
        allowedOps = listOf('+', '-'),
        termCount = 2..2,
        useParentheses = false
    )
    Difficulty.MEDIUM -> DifficultyProfile(
        operandRange = 1..20,
        allowedOps = listOf('+', '-', '×', '÷'),
        termCount = 2..3,
        useParentheses = false
    )
    Difficulty.HARD -> DifficultyProfile(
        operandRange = 1..25,
        allowedOps = listOf('+', '-', '×', '÷'),
        termCount = 3..4,
        useParentheses = true
    )
}

fun timerSecondsFor(difficulty: Difficulty): Int = when (difficulty) {
    Difficulty.EASY -> 10
    Difficulty.MEDIUM -> 30
    Difficulty.HARD -> 60
}
