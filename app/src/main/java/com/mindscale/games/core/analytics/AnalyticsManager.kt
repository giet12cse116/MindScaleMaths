package com.mindscale.games.core.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class AnalyticsManager(context: Context) {

    private val firebaseAnalytics: FirebaseAnalytics by lazy {
        FirebaseAnalytics.getInstance(context.applicationContext)
    }

    fun logScreenView(screenName: String) {
        try {
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
                putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
            }
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun logGameStart(mode: String, difficulty: String) {
        try {
            val bundle = Bundle().apply {
                putString("game_mode", mode)
                putString("difficulty", difficulty)
            }
            firebaseAnalytics.logEvent("game_start", bundle)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun logRoundCompleted(mode: String, difficulty: String, isCorrect: Boolean, streak: Int) {
        try {
            val bundle = Bundle().apply {
                putString("game_mode", mode)
                putString("difficulty", difficulty)
                putBoolean("is_correct", isCorrect)
                putInt("streak", streak)
            }
            firebaseAnalytics.logEvent("round_completed", bundle)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun logGameEnd(mode: String, difficulty: String, correct: Int, wrong: Int, bestStreak: Int) {
        try {
            val bundle = Bundle().apply {
                putString("game_mode", mode)
                putString("difficulty", difficulty)
                putInt("correct_count", correct)
                putInt("wrong_count", wrong)
                putInt("best_streak", bestStreak)
            }
            firebaseAnalytics.logEvent("game_ended", bundle)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
