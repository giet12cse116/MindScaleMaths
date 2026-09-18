package com.mindscale.games.domain.model

data class Round(
    val id: Long,
    val left: Expression,
    val right: Expression,
    val correctAnswer: Comparison,
    val difficulty: Difficulty
)
