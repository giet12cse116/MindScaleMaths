package com.mindscale.games.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mindscale.games.data.local.UserPrefsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

enum class SplashDestination {
    Walkthrough,
    Home
}

class SplashViewModel(
    private val userPrefsRepository: UserPrefsRepository
) : ViewModel() {

    private val _destination = MutableStateFlow<SplashDestination?>(null)
    val destination: StateFlow<SplashDestination?> = _destination.asStateFlow()

    init {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            val hasCompleted = userPrefsRepository.hasCompletedOnboarding.first()
            val elapsedTime = System.currentTimeMillis() - startTime
            val remainingDelay = 1200L - elapsedTime
            if (remainingDelay > 0) {
                delay(remainingDelay)
            }
            _destination.value = if (hasCompleted) {
                SplashDestination.Home
            } else {
                SplashDestination.Walkthrough
            }
        }
    }
}

class SplashViewModelFactory(
    private val repository: UserPrefsRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SplashViewModel(repository) as T
    }
}
