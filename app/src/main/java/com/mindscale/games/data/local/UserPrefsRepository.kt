package com.mindscale.games.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "mindscale_prefs")

class UserPrefsRepository(private val context: Context) {
    private val KEY_ONBOARDED = booleanPreferencesKey("has_completed_onboarding")
    private val KEY_BEST_STREAK = intPreferencesKey("best_streak")
    private val KEY_SOUND = booleanPreferencesKey("sound_enabled")
    private val KEY_HAPTICS = booleanPreferencesKey("haptics_enabled")

    val hasCompletedOnboarding: Flow<Boolean> =
        context.dataStore.data.map { it[KEY_ONBOARDED] ?: false }

    val bestStreak: Flow<Int> =
        context.dataStore.data.map { it[KEY_BEST_STREAK] ?: 0 }

    val soundEnabled: Flow<Boolean> =
        context.dataStore.data.map { it[KEY_SOUND] ?: true }

    val hapticsEnabled: Flow<Boolean> =
        context.dataStore.data.map { it[KEY_HAPTICS] ?: true }

    suspend fun setOnboardingComplete() {
        context.dataStore.edit { it[KEY_ONBOARDED] = true }
    }

    suspend fun updateBestStreakIfHigher(value: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_BEST_STREAK] ?: 0
            if (value > current) prefs[KEY_BEST_STREAK] = value
        }
    }

    suspend fun setSoundEnabled(value: Boolean) {
        context.dataStore.edit { it[KEY_SOUND] = value }
    }

    suspend fun setHapticsEnabled(value: Boolean) {
        context.dataStore.edit { it[KEY_HAPTICS] = value }
    }

    suspend fun resetStats() {
        context.dataStore.edit { it[KEY_BEST_STREAK] = 0 }
    }
}
