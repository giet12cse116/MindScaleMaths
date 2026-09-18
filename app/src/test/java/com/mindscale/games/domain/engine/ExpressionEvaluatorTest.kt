package com.mindscale.games.domain.engine

import com.mindscale.games.domain.model.Token
import org.junit.Assert.assertEquals
import org.junit.Test

class ExpressionEvaluatorTest {

    @Test
    fun testOperatorPrecedence() {
        // 2 + 3 × 4 = 14
        val tokens = listOf(
            Token.Number(2.0),
            Token.Operator('+'),
            Token.Number(3.0),
            Token.Operator('×'),
            Token.Number(4.0)
        )
        val result = ExpressionEvaluator.evaluate(tokens)
        assertEquals(14.0, result, 0.0001)
    }

    @Test
    fun testParenthesesOverridePrecedence() {
        // (2 + 3) × 4 = 20
        val tokens = listOf(
            Token.OpenParen,
            Token.Number(2.0),
            Token.Operator('+'),
            Token.Number(3.0),
            Token.CloseParen,
            Token.Operator('×'),
            Token.Number(4.0)
        )
        val result = ExpressionEvaluator.evaluate(tokens)
        assertEquals(20.0, result, 0.0001)
    }

    @Test
    fun testDivision() {
        // 12 ÷ 3 = 4
        val tokens = listOf(
            Token.Number(12.0),
            Token.Operator('÷'),
            Token.Number(3.0)
        )
        val result = ExpressionEvaluator.evaluate(tokens)
        assertEquals(4.0, result, 0.0001)
    }

    @Test(expected = ArithmeticException::class)
    fun testDivideByZeroThrows() {
        val tokens = listOf(
            Token.Number(5.0),
            Token.Operator('÷'),
            Token.Number(0.0)
        )
        ExpressionEvaluator.evaluate(tokens)
    }

    @Test
    fun testTokensToDisplayFormatting() {
        val tokens = listOf(
            Token.OpenParen,
            Token.Number(60.0),
            Token.Operator('-'),
            Token.Number(5.0),
            Token.CloseParen,
            Token.Operator('+'),
            Token.Number(1.0)
        )
        val display = ExpressionEvaluator.tokensToDisplay(tokens)
        assertEquals("(60 - 5) + 1", display)
    }
}
