package com.mindscale.games.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "round_history")
data class RoundHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val mode: String,
    val difficulty: String,
    val wasCorrect: Boolean,
    val outcomeType: String
)
