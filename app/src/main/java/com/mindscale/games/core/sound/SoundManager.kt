package com.mindscale.games.core.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.mindscale.games.data.local.UserPrefsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SoundManager(
    private val context: Context,
    private val userPrefsRepository: UserPrefsRepository
) {
    enum class Sfx {
        TAP,
        CORRECT,
        WRONG,
        COMBO_UP,
        TIMER_TICK,
        TIMEOUT,
        ROUND_COMPLETE
    }

    private val scope = CoroutineScope(Dispatchers.IO)
    private val soundPool: SoundPool

    private val soundMap = mutableMapOf<Sfx, Int>()

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(audioAttributes)
            .build()

        loadSounds()
    }

    private fun loadSounds() {
        Sfx.values().forEach { sfx ->
            val resName = sfx.name.lowercase()
            val resId = context.resources.getIdentifier(resName, "raw", context.packageName)
            if (resId != 0) {
                try {
                    soundMap[sfx] = soundPool.load(context, resId, 1)
                } catch (_: Exception) {}
            }
        }
    }

    fun play(sfx: Sfx) {
        scope.launch {
            val isEnabled = userPrefsRepository.soundEnabled.first()
            if (!isEnabled) return@launch

            val soundId = soundMap[sfx] ?: return@launch
            soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f)
        }
    }

    fun release() {
        soundPool.release()
    }
}
