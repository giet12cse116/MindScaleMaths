package com.mindscale.games.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mindscale.games.data.local.UserPrefsRepository
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val userPrefsRepository: UserPrefsRepository
) : ViewModel() {

    fun completeOnboarding(onComplete: () -> Unit) {
        viewModelScope.launch {
            userPrefsRepository.setOnboardingComplete()
            onComplete()
        }
    }
}

class OnboardingViewModelFactory(
    private val repository: UserPrefsRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OnboardingViewModel(repository) as T
    }
}
