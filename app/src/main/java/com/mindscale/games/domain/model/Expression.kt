package com.mindscale.games.domain.model

data class Expression(
    val display: String,     // "7 × 8", "(60 - 5) + 1"
    val tokens: List<Token>,
    val value: Double
)
