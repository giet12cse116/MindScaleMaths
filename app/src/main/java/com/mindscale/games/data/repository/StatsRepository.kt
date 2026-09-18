package com.mindscale.games.data.repository

import com.mindscale.games.data.local.DifficultyAccuracy
import com.mindscale.games.data.local.RoundHistoryDao
import com.mindscale.games.data.local.RoundHistoryEntity
import com.mindscale.games.domain.model.Difficulty
import com.mindscale.games.domain.model.GameMode
import com.mindscale.games.domain.model.RoundOutcome
import kotlinx.coroutines.flow.Flow

class StatsRepository(private val dao: RoundHistoryDao) {
    fun totalCorrect(): Flow<Int> = dao.totalCorrect()
    fun totalWrong(): Flow<Int> = dao.totalWrong()
    fun accuracyByDifficulty(): Flow<List<DifficultyAccuracy>> = dao.accuracyByDifficulty()

    suspend fun recordRound(mode: GameMode, difficulty: Difficulty, outcome: RoundOutcome) {
        val outcomeTypeStr = when (outcome) {
            is RoundOutcome.Correct -> "correct"
            is RoundOutcome.Wrong -> "wrong"
            is RoundOutcome.Solved -> "solved"
            is RoundOutcome.TimedOut -> "timed_out"
            RoundOutcome.HintUsed -> return
        }

        dao.insert(
            RoundHistoryEntity(
                timestamp = System.currentTimeMillis(),
                mode = mode.name.lowercase(),
                difficulty = difficulty.name.lowercase(),
                wasCorrect = outcome is RoundOutcome.Correct,
                outcomeType = outcomeTypeStr
            )
        )
    }

    suspend fun clearAll() = dao.clearAll()
}
