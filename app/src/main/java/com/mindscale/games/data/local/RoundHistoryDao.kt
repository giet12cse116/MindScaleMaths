package com.mindscale.games.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RoundHistoryDao {
    @Insert
    suspend fun insert(entity: RoundHistoryEntity)

    @Query("SELECT COUNT(*) FROM round_history WHERE wasCorrect = 1")
    fun totalCorrect(): Flow<Int>

    @Query("SELECT COUNT(*) FROM round_history WHERE wasCorrect = 0")
    fun totalWrong(): Flow<Int>

    @Query("SELECT difficulty, COUNT(*) as total, SUM(CASE WHEN wasCorrect THEN 1 ELSE 0 END) as correct FROM round_history GROUP BY difficulty")
    fun accuracyByDifficulty(): Flow<List<DifficultyAccuracy>>

    @Query("DELETE FROM round_history")
    suspend fun clearAll()
}
