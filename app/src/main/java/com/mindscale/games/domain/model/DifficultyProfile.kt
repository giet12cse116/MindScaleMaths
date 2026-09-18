package com.mindscale.games.domain.model

data class DifficultyProfile(
    val operandRange: IntRange,
    val allowedOps: List<Char>,
    val termCount: IntRange,
    val useParentheses: Boolean
)
