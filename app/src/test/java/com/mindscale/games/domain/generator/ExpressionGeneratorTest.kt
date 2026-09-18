package com.mindscale.games.domain.generator

import com.mindscale.games.domain.engine.ExpressionEvaluator
import com.mindscale.games.domain.model.Comparison
import com.mindscale.games.domain.model.Difficulty
import com.mindscale.games.domain.model.GameMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ExpressionGeneratorTest {

    private val generator: ExpressionGenerator = ExpressionGeneratorImpl()

    @Test
    fun testGenerate200RoundsPerDifficultyCorrectness() {
        for (difficulty in Difficulty.values()) {
            for (i in 1..200) {
                val round = generator.generateRound(difficulty, GameMode.CLASSIC)
                assertNotNull(round)
                
                // Assert tokens evaluate to stored values
                val leftEvaluated = ExpressionEvaluator.evaluate(round.left.tokens)
                val rightEvaluated = ExpressionEvaluator.evaluate(round.right.tokens)

                assertEquals(
                    "Left expression tokens evaluation mismatch at round $i for $difficulty",
                    leftEvaluated,
                    round.left.value,
                    0.0001
                )
                assertEquals(
                    "Right expression tokens evaluation mismatch at round $i for $difficulty",
                    rightEvaluated,
                    round.right.value,
                    0.0001
                )

                // Assert correctAnswer strictly matches leftValue vs rightValue comparison
                val diff = round.left.value - round.right.value
                val expectedComp = when {
                    kotlin.math.abs(diff) < 0.0001 -> Comparison.EQUAL
                    round.left.value < round.right.value -> Comparison.LESS
                    else -> Comparison.GREATER
                }

                assertEquals(
                    "Correct answer mismatch at round $i ($difficulty): left=${round.left.display} (${round.left.value}), right=${round.right.display} (${round.right.value})",
                    expectedComp,
                    round.correctAnswer
                )
            }
        }
    }
}
