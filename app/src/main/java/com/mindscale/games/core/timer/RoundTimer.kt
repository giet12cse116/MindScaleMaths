package com.mindscale.games.core.timer

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RoundTimer(private val scope: CoroutineScope) {
    private val _secondsLeft = MutableStateFlow(0)
    val secondsLeft: StateFlow<Int> = _secondsLeft.asStateFlow()
    private var job: Job? = null

    fun start(totalSeconds: Int, onExpire: () -> Unit) {
        job?.cancel()
        _secondsLeft.value = totalSeconds
        job = scope.launch {
            while (_secondsLeft.value > 0) {
                delay(1000)
                _secondsLeft.value -= 1
            }
            onExpire()
        }
    }

    fun pause() {
        job?.cancel()
    }

    fun resume(onExpire: () -> Unit) {
        if (_secondsLeft.value > 0) {
            start(_secondsLeft.value, onExpire)
        }
    }

    fun cancel() {
        job?.cancel()
    }
}
