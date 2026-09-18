package com.mindscale.games.domain.model

sealed class Token {
    data class Number(val value: Double) : Token()
    data class Operator(val symbol: Char) : Token()   // '+' '-' '×' '÷'
    object OpenParen : Token()
    object CloseParen : Token()
}
