package com.mindscale.games.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mindscale.games.data.local.UserPrefsRepository
import com.mindscale.games.data.repository.StatsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true
)

class SettingsViewModel(
    private val userPrefsRepository: UserPrefsRepository,
    private val statsRepository: StatsRepository? = null
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        userPrefsRepository.soundEnabled,
        userPrefsRepository.hapticsEnabled
    ) { sound, haptics ->
        SettingsUiState(
            soundEnabled = sound,
            hapticsEnabled = haptics
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun toggleSound(enabled: Boolean) {
        viewModelScope.launch {
            userPrefsRepository.setSoundEnabled(enabled)
        }
    }

    fun toggleHaptics(enabled: Boolean) {
        viewModelScope.launch {
            userPrefsRepository.setHapticsEnabled(enabled)
        }
    }

    fun resetGameProgress() {
        viewModelScope.launch {
            userPrefsRepository.resetStats()
            statsRepository?.clearAll()
        }
    }

    fun resetStats() {
        resetGameProgress()
    }
}

class SettingsViewModelFactory(
    private val userPrefsRepository: UserPrefsRepository,
    private val statsRepository: StatsRepository? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(userPrefsRepository, statsRepository) as T
    }
}
