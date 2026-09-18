package com.mindscale.games.domain.generator

import com.mindscale.games.domain.engine.ExpressionEvaluator
import com.mindscale.games.domain.model.Comparison
import com.mindscale.games.domain.model.Difficulty
import com.mindscale.games.domain.model.DifficultyProfile
import com.mindscale.games.domain.model.Expression
import com.mindscale.games.domain.model.GameMode
import com.mindscale.games.domain.model.Round
import com.mindscale.games.domain.model.Token
import kotlin.random.Random

interface ExpressionGenerator {
    fun generateRound(difficulty: Difficulty, mode: GameMode): Round
}

class ExpressionGeneratorImpl(
    private val evaluator: ExpressionEvaluator = ExpressionEvaluator,
    private val random: Random = Random.Default
) : ExpressionGenerator {

    private val targetBag = mutableListOf<Comparison>()

    @Synchronized
    private fun nextTargetComparison(): Comparison {
        if (targetBag.isEmpty()) {
            targetBag.addAll(Comparison.values())
            targetBag.shuffle(random)
        }
        return targetBag.removeAt(0)
    }

    override fun generateRound(difficulty: Difficulty, mode: GameMode): Round {
        val profile = profileFor(difficulty)
        val targetComp = nextTargetComparison()

        var leftExpr: Expression
        var rightExpr: Expression

        var outerAttempts = 0
        while (true) {
            outerAttempts++
            leftExpr = generateRandomExpression(profile)
            val candidateRight = tryGenerateRightExpression(leftExpr, targetComp, profile)
            if (candidateRight != null) {
                rightExpr = candidateRight
                break
            }
            if (outerAttempts > 50) {
                // Emergency fallback
                leftExpr = generateRandomExpression(profile)
                rightExpr = generateRandomExpression(profile)
                val actualComp = evaluateComparison(leftExpr.value, rightExpr.value)
                return Round(
                    id = System.nanoTime(),
                    left = leftExpr,
                    right = rightExpr,
                    correctAnswer = actualComp,
                    difficulty = difficulty
                )
            }
        }

        val actualComp = evaluateComparison(leftExpr.value, rightExpr.value)
        return Round(
            id = System.nanoTime(),
            left = leftExpr,
            right = rightExpr,
            correctAnswer = actualComp,
            difficulty = difficulty
        )
    }

    private fun evaluateComparison(left: Double, right: Double): Comparison {
        val diff = left - right
        return when {
            kotlin.math.abs(diff) < 0.0001 -> Comparison.EQUAL
            left < right -> Comparison.LESS
            else -> Comparison.GREATER
        }
    }

    private fun tryGenerateRightExpression(
        leftExpr: Expression,
        targetComp: Comparison,
        profile: DifficultyProfile
    ): Expression? {
        val targetValue = leftExpr.value

        for (attempt in 0 until 25) {
            val candidate = generateRandomExpression(profile)
            val comp = evaluateComparison(leftExpr.value, candidate.value)

            if (targetComp == Comparison.EQUAL) {
                if (comp == Comparison.EQUAL && candidate.display != leftExpr.display) {
                    return candidate
                }
            } else if (comp == targetComp) {
                return candidate
            }
        }

        if (targetComp == Comparison.EQUAL) {
            val a = profile.operandRange.random(random).toDouble()
            val b = targetValue - a
            if (b > 0 && b.toInt().toDouble() == b) {
                val tokens = listOf(Token.Number(a), Token.Operator('+'), Token.Number(b))
                val display = evaluator.tokensToDisplay(tokens)
                if (display != leftExpr.display) {
                    return Expression(display, tokens, targetValue)
                }
            }
        } else {
            val candidate = generateRandomExpression(profile)
            val adjustedTokens = candidate.tokens.map { token ->
                if (token is Token.Number) {
                    val delta = if (targetComp == Comparison.GREATER) -5.0 else 5.0
                    val newVal = (token.value + delta).coerceAtLeast(1.0)
                    Token.Number(newVal)
                } else token
            }
            try {
                val valEvaluated = evaluator.evaluate(adjustedTokens)
                val comp = evaluateComparison(leftExpr.value, valEvaluated)
                if (comp == targetComp) {
                    val display = evaluator.tokensToDisplay(adjustedTokens)
                    return Expression(display, adjustedTokens, valEvaluated)
                }
            } catch (_: Exception) {}
        }

        return null
    }

    private fun generateRandomExpression(profile: DifficultyProfile): Expression {
        val numTerms = profile.termCount.random(random)
        val tokens = mutableListOf<Token>()

        for (i in 0 until numTerms) {
            if (i > 0) {
                val op = profile.allowedOps.random(random)
                tokens.add(Token.Operator(op))
            }
            val prevOp = tokens.lastOrNull()
            if (prevOp is Token.Operator && prevOp.symbol == '÷') {
                val divisor = profile.operandRange.random(random).coerceAtLeast(1)
                val multiplier = (1..6).random(random)
                val dividend = divisor * multiplier
                if (tokens.size >= 2 && tokens[tokens.size - 2] is Token.Number) {
                    tokens[tokens.size - 2] = Token.Number(dividend.toDouble())
                }
                tokens.add(Token.Number(divisor.toDouble()))
            } else {
                val valOperand = profile.operandRange.random(random).toDouble()
                tokens.add(Token.Number(valOperand))
            }
        }

        val finalTokens = if (profile.useParentheses && numTerms >= 3 && random.nextBoolean()) {
            addParentheses(tokens)
        } else {
            tokens
        }

        val value = evaluator.evaluate(finalTokens)
        val display = evaluator.tokensToDisplay(finalTokens)
        return Expression(display, finalTokens, value)
    }

    private fun addParentheses(tokens: List<Token>): List<Token> {
        val result = mutableListOf<Token>()
        if (tokens.size >= 3 && tokens[0] is Token.Number && tokens[1] is Token.Operator && tokens[2] is Token.Number) {
            result.add(Token.OpenParen)
            result.add(tokens[0])
            result.add(tokens[1])
            result.add(tokens[2])
            result.add(Token.CloseParen)
            for (i in 3 until tokens.size) {
                result.add(tokens[i])
            }
            return result
        }
        return tokens
    }
}
