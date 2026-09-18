package com.mindscale.games.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [RoundHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class RoundHistoryDatabase : RoomDatabase() {
    abstract fun roundHistoryDao(): RoundHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: RoundHistoryDatabase? = null

        fun getInstance(context: Context): RoundHistoryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoundHistoryDatabase::class.java,
                    "mindscale_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
