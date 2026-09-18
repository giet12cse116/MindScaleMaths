package com.mindscale.games.core.haptics

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.mindscale.games.data.local.UserPrefsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HapticManager(
    context: Context,
    private val userPrefsRepository: UserPrefsRepository
) {
    enum class Impact {
        LIGHT,   // ~10ms
        MEDIUM,  // ~20ms
        HEAVY    // ~35ms
    }

    private val scope = CoroutineScope(Dispatchers.IO)
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    fun perform(impact: Impact) {
        val vib = vibrator ?: return
        if (!vib.hasVibrator()) return

        scope.launch {
            val isEnabled = userPrefsRepository.hapticsEnabled.first()
            if (!isEnabled) return@launch

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val (duration, amplitude) = when (impact) {
                        Impact.LIGHT -> Pair(10L, 80)
                        Impact.MEDIUM -> Pair(20L, 160)
                        Impact.HEAVY -> Pair(35L, 255)
                    }
                    vib.vibrate(VibrationEffect.createOneShot(duration, amplitude))
                } else {
                    val duration = when (impact) {
                        Impact.LIGHT -> 10L
                        Impact.MEDIUM -> 20L
                        Impact.HEAVY -> 35L
                    }
                    @Suppress("DEPRECATION")
                    vib.vibrate(duration)
                }
            } catch (_: Exception) {}
        }
    }
}
