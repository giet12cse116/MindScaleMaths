package com.mindscale.games.domain.usecase

import com.mindscale.games.domain.generator.ExpressionGenerator
import com.mindscale.games.domain.model.Difficulty
import com.mindscale.games.domain.model.GameMode
import com.mindscale.games.domain.model.Round

class GenerateRoundUseCase(
    private val generator: ExpressionGenerator
) {
    operator fun invoke(difficulty: Difficulty, mode: GameMode): Round =
        generator.generateRound(difficulty, mode)
}
