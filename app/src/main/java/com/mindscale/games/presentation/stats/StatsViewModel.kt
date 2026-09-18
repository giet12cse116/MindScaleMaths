package com.mindscale.games.presentation.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mindscale.games.data.local.UserPrefsRepository
import com.mindscale.games.data.repository.StatsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class StatsViewModel(
    private val statsRepository: StatsRepository,
    private val userPrefsRepository: UserPrefsRepository
) : ViewModel() {

    val uiState: StateFlow<StatsUiState> = combine(
        statsRepository.totalCorrect(),
        statsRepository.totalWrong(),
        statsRepository.accuracyByDifficulty(),
        userPrefsRepository.bestStreak
    ) { correct, wrong, accuracyList, bestStreak ->
        val total = correct + wrong
        StatsUiState(
            totalCorrect = correct,
            totalWrong = wrong,
            bestStreak = bestStreak,
            accuracyList = accuracyList,
            isEmpty = total == 0
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatsUiState()
    )
}

class StatsViewModelFactory(
    private val statsRepository: StatsRepository,
    private val userPrefsRepository: UserPrefsRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StatsViewModel(statsRepository, userPrefsRepository) as T
    }
}
