package com.mindscale.games.domain.engine

import com.mindscale.games.domain.model.Token

object ExpressionEvaluator {

    fun evaluate(tokens: List<Token>): Double {
        if (tokens.isEmpty()) throw IllegalArgumentException("Empty token list")
        val parser = Parser(tokens)
        val result = parser.parseExpression()
        if (parser.hasNext()) {
            throw IllegalArgumentException("Unexpected token after expression evaluation: ${parser.peek()}")
        }
        return result
    }

    fun tokensToDisplay(tokens: List<Token>): String {
        val sb = StringBuilder()
        var prevToken: Token? = null

        for (token in tokens) {
            when (token) {
                is Token.Number -> {
                    if (prevToken != null && prevToken !is Token.OpenParen) {
                        sb.append(" ")
                    }
                    val numVal = token.value
                    if (numVal == numVal.toLong().toDouble()) {
                        sb.append(numVal.toLong().toString())
                    } else {
                        sb.append(numVal.toString())
                    }
                }
                is Token.Operator -> {
                    if (sb.isNotEmpty()) {
                        sb.append(" ")
                    }
                    sb.append(token.symbol)
                }
                is Token.OpenParen -> {
                    if (prevToken != null && prevToken !is Token.OpenParen && prevToken !is Token.Operator) {
                        sb.append(" ")
                    }
                    sb.append("(")
                }
                is Token.CloseParen -> {
                    sb.append(")")
                }
            }
            prevToken = token
        }
        return sb.toString()
    }

    private class Parser(private val tokens: List<Token>) {
        private var index = 0

        fun hasNext(): Boolean = index < tokens.size
        fun peek(): Token = tokens[index]

        fun consume(): Token {
            val token = tokens[index]
            index++
            return token
        }

        // expression = term (('+' | '-') term)*
        fun parseExpression(): Double {
            var value = parseTerm()
            while (hasNext()) {
                val current = peek()
                if (current is Token.Operator && (current.symbol == '+' || current.symbol == '-')) {
                    consume()
                    val nextTerm = parseTerm()
                    if (current.symbol == '+') {
                        value += nextTerm
                    } else {
                        value -= nextTerm
                    }
                } else {
                    break
                }
            }
            return value
        }

        // term = factor (('×' | '÷' | '*' | '/') factor)*
        private fun parseTerm(): Double {
            var value = parseFactor()
            while (hasNext()) {
                val current = peek()
                if (current is Token.Operator && (current.symbol == '×' || current.symbol == '÷' || current.symbol == '*' || current.symbol == '/')) {
                    consume()
                    val nextFactor = parseFactor()
                    if (current.symbol == '×' || current.symbol == '*') {
                        value *= nextFactor
                    } else {
                        if (nextFactor == 0.0) {
                            throw ArithmeticException("Division by zero")
                        }
                        value /= nextFactor
                    }
                } else {
                    break
                }
            }
            return value
        }

        // factor = Number | '(' expression ')'
        private fun parseFactor(): Double {
            if (!hasNext()) throw IllegalArgumentException("Unexpected end of expression")
            return when (val current = consume()) {
                is Token.Number -> current.value
                is Token.OpenParen -> {
                    val value = parseExpression()
                    if (!hasNext() || peek() !is Token.CloseParen) {
                        throw IllegalArgumentException("Missing closing parenthesis")
                    }
                    consume() // consume CloseParen
                    value
                }
                else -> throw IllegalArgumentException("Unexpected token in expression: $current")
            }
        }
    }
}
