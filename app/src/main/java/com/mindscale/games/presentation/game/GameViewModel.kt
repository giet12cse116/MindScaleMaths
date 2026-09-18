package com.mindscale.games.presentation.game

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mindscale.games.core.ads.AdManager
import com.mindscale.games.core.analytics.AnalyticsManager
import com.mindscale.games.core.haptics.HapticManager
import com.mindscale.games.core.sound.SoundManager
import com.mindscale.games.core.timer.RoundTimer
import com.mindscale.games.data.local.UserPrefsRepository
import com.mindscale.games.data.repository.StatsRepository
import com.mindscale.games.domain.generator.timerSecondsFor
import com.mindscale.games.domain.model.Comparison
import com.mindscale.games.domain.model.Difficulty
import com.mindscale.games.domain.model.GameMode
import com.mindscale.games.domain.model.RoundOutcome
import com.mindscale.games.domain.usecase.EvaluateAnswerUseCase
import com.mindscale.games.domain.usecase.GenerateRoundUseCase
import com.mindscale.games.domain.usecase.ScoreUseCase
import com.mindscale.games.presentation.results.GameSessionSummary
import com.mindscale.games.presentation.results.ResultsSessionHolder
import com.mindscale.games.presentation.theme.CardState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GameViewModel(
    private val generateRound: GenerateRoundUseCase,
    private val evaluateAnswer: EvaluateAnswerUseCase,
    private val scoreUseCase: ScoreUseCase,
    private val userPrefsRepository: UserPrefsRepository,
    initialMode: GameMode,
    initialDifficulty: Difficulty,
    private val soundManager: SoundManager? = null,
    private val hapticManager: HapticManager? = null,
    private val statsRepository: StatsRepository? = null,
    private val analyticsManager: AnalyticsManager? = null
) : ViewModel() {

    private val roundTimer = RoundTimer(viewModelScope)

    private val _navEvent = MutableSharedFlow<NavEvent>()
    val navEvent: SharedFlow<NavEvent> = _navEvent.asSharedFlow()

    private var peakStreakThisRun = 0
    private var previousBestStreak = 0
    fun onHintClicked(activity: Activity?, adManager: AdManager?) {
        val currentState = _uiState.value
        if (currentState.isAnswered || currentState.hintUsed) return

        if (currentState.session.hintsRemaining > 0) {
            onHint()
        } else {
            if (activity != null && adManager != null) {
                pauseTimer()
                var rewardEarned = false
                adManager.showRewardedAd(
                    activity = activity,
                    onRewardEarned = {
                        rewardEarned = true
                        onRewardedHintEarned()
                    },
                    onAdDismissed = {
                        resumeTimer()
                    }
                )
            }
        }
    }

    private val initialSession = GameSessionState(
        mode = initialMode,
        difficulty = initialDifficulty,
        livesRemaining = if (initialMode == GameMode.ARCADE) 3 else 0,
        secondsLeft = if (initialMode == GameMode.ARCADE) timerSecondsFor(initialDifficulty) else null
    )

    private val initialRound = generateRound(initialDifficulty, initialMode)

    private val _uiState = MutableStateFlow(
        GameUiState(
            session = initialSession,
            round = initialRound
        )
    )
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        analyticsManager?.logGameStart(initialMode.name, initialDifficulty.name)

        viewModelScope.launch {
            previousBestStreak = userPrefsRepository.bestStreak.first()
        }

        if (initialMode == GameMode.ARCADE) {
            viewModelScope.launch {
                roundTimer.secondsLeft.collect { seconds ->
                    if (seconds in 1..4) {
                        soundManager?.play(SoundManager.Sfx.TIMER_TICK)
                    }
                    _uiState.update { currentState ->
                        currentState.copy(
                            session = currentState.session.copy(secondsLeft = seconds)
                        )
                    }
                }
            }
            startRoundTimer(initialDifficulty)
        }
    }

    private fun startRoundTimer(difficulty: Difficulty) {
        val totalSecs = timerSecondsFor(difficulty)
        roundTimer.start(totalSecs) {
            onTimeExpire()
        }
    }

    private fun onTimeExpire() {
        val currentState = _uiState.value
        if (currentState.isAnswered) return

        soundManager?.play(SoundManager.Sfx.TIMEOUT)
        hapticManager?.perform(HapticManager.Impact.MEDIUM)

        val session = currentState.session
        val outcome = RoundOutcome.TimedOut(currentState.round.correctAnswer)

        viewModelScope.launch {
            statsRepository?.recordRound(session.mode, session.difficulty, outcome)
        }

        val scoreState = ScoreUseCase.ScoreState(
            streak = session.streak,
            comboMultiplier = session.comboMultiplier,
            comboProgress = session.comboProgress
        )
        val newScore = scoreUseCase.onWrongOrSolved(scoreState)

        val newSession = session.copy(
            wrong = session.wrong + 1,
            streak = newScore.streak,
            comboMultiplier = newScore.comboMultiplier,
            comboProgress = newScore.comboProgress
        )

        _uiState.value = currentState.copy(
            session = newSession,
            leftState = CardState.WRONG,
            rightState = CardState.WRONG,
            outcome = outcome,
            isAnswered = true
        )

        analyticsManager?.logRoundCompleted(session.mode.name, session.difficulty.name, false, newScore.streak)
        loseLife()
    }

    fun onCompare(choice: Comparison) {
        val currentState = _uiState.value
        if (currentState.isAnswered) return

        soundManager?.play(SoundManager.Sfx.TAP)
        hapticManager?.perform(HapticManager.Impact.LIGHT)

        if (currentState.session.mode == GameMode.ARCADE) {
            roundTimer.pause()
        }

        val round = currentState.round
        val session = currentState.session
        val outcome = evaluateAnswer(round, choice, session.streak)

        viewModelScope.launch {
            statsRepository?.recordRound(session.mode, session.difficulty, outcome)
        }

        if (outcome is RoundOutcome.Correct) {
            soundManager?.play(SoundManager.Sfx.CORRECT)
            hapticManager?.perform(HapticManager.Impact.MEDIUM)

            val scoreState = ScoreUseCase.ScoreState(
                streak = session.streak,
                comboMultiplier = session.comboMultiplier,
                comboProgress = session.comboProgress
            )
            val newScore = scoreUseCase.onCorrect(scoreState)

            if (newScore.comboMultiplier > session.comboMultiplier) {
                soundManager?.play(SoundManager.Sfx.COMBO_UP)
                hapticManager?.perform(HapticManager.Impact.LIGHT)
            }

            if (newScore.streak > peakStreakThisRun) {
                peakStreakThisRun = newScore.streak
            }

            val newSession = session.copy(
                correct = session.correct + 1,
                streak = newScore.streak,
                comboMultiplier = newScore.comboMultiplier,
                comboProgress = newScore.comboProgress
            )

            viewModelScope.launch {
                userPrefsRepository.updateBestStreakIfHigher(newScore.streak)
            }

            _uiState.value = currentState.copy(
                session = newSession,
                leftState = CardState.CORRECT,
                rightState = CardState.CORRECT,
                outcome = outcome,
                isAnswered = true
            )

            analyticsManager?.logRoundCompleted(session.mode.name, session.difficulty.name, true, newScore.streak)
        } else if (outcome is RoundOutcome.Wrong) {
            soundManager?.play(SoundManager.Sfx.WRONG)
            hapticManager?.perform(HapticManager.Impact.MEDIUM)

            val scoreState = ScoreUseCase.ScoreState(
                streak = session.streak,
                comboMultiplier = session.comboMultiplier,
                comboProgress = session.comboProgress
            )
            val newScore = scoreUseCase.onWrongOrSolved(scoreState)

            val newSession = session.copy(
                wrong = session.wrong + 1,
                streak = newScore.streak,
                comboMultiplier = newScore.comboMultiplier,
                comboProgress = newScore.comboProgress
            )

            _uiState.value = currentState.copy(
                session = newSession,
                leftState = CardState.WRONG,
                rightState = CardState.WRONG,
                outcome = outcome,
                isAnswered = true
            )

            analyticsManager?.logRoundCompleted(session.mode.name, session.difficulty.name, false, newScore.streak)

            if (session.mode == GameMode.ARCADE) {
                loseLife()
            }
        }
    }

    fun onHint() {
        val currentState = _uiState.value
        if (currentState.isAnswered || currentState.hintUsed || currentState.session.hintsRemaining <= 0) return

        soundManager?.play(SoundManager.Sfx.TAP)
        hapticManager?.perform(HapticManager.Impact.LIGHT)

        val newSession = currentState.session.copy(
            hintsRemaining = (currentState.session.hintsRemaining - 1).coerceAtLeast(0)
        )

        _uiState.value = currentState.copy(
            session = newSession,
            revealedLeftValue = currentState.round.left.value,
            leftState = CardState.HINT,
            outcome = RoundOutcome.HintUsed,
            hintUsed = true
        )
    }

    fun onRewardedHintEarned() {
        _uiState.update { currentState ->
            val newHints = currentState.session.hintsRemaining + 1
            currentState.copy(
                session = currentState.session.copy(hintsRemaining = newHints)
            )
        }
        soundManager?.play(SoundManager.Sfx.CORRECT)
        hapticManager?.perform(HapticManager.Impact.MEDIUM)

        if (!_uiState.value.isAnswered && !_uiState.value.hintUsed) {
            onHint()
        }
    }

    fun onNext() {
        val currentState = _uiState.value
        soundManager?.play(SoundManager.Sfx.TAP)
        hapticManager?.perform(HapticManager.Impact.LIGHT)

        val session = currentState.session
        val newRound = generateRound(session.difficulty, session.mode)

        _uiState.value = currentState.copy(
            round = newRound,
            leftState = CardState.IDLE,
            rightState = CardState.IDLE,
            revealedLeftValue = null,
            revealedRightValue = null,
            outcome = null,
            isAnswered = false,
            hintUsed = false
        )

        if (session.mode == GameMode.ARCADE) {
            startRoundTimer(session.difficulty)
        }
    }

    fun onDifficultyChange(newDifficulty: Difficulty) {
        val currentState = _uiState.value
        soundManager?.play(SoundManager.Sfx.TAP)
        hapticManager?.perform(HapticManager.Impact.LIGHT)

        val newSession = currentState.session.copy(difficulty = newDifficulty)
        val newRound = generateRound(newDifficulty, newSession.mode)

        _uiState.value = currentState.copy(
            session = newSession,
            round = newRound,
            leftState = CardState.IDLE,
            rightState = CardState.IDLE,
            revealedLeftValue = null,
            revealedRightValue = null,
            outcome = null,
            isAnswered = false,
            hintUsed = false
        )

        analyticsManager?.logGameStart(newSession.mode.name, newDifficulty.name)

        if (newSession.mode == GameMode.ARCADE) {
            startRoundTimer(newDifficulty)
        }
    }

    fun pauseTimer() {
        if (_uiState.value.session.mode == GameMode.ARCADE) {
            roundTimer.pause()
        }
    }

    fun resumeTimer() {
        if (_uiState.value.session.mode == GameMode.ARCADE && !_uiState.value.isAnswered) {
            roundTimer.resume { onTimeExpire() }
        }
    }

    fun cancelTimer() {
        roundTimer.cancel()
    }

    fun restartSession() {
        val currentState = _uiState.value
        peakStreakThisRun = 0

        val resetSession = currentState.session.copy(
            correct = 0,
            wrong = 0,
            streak = 0,
            comboMultiplier = 1,
            comboProgress = 0f,
            livesRemaining = 3,
            hintsRemaining = 3
        )
        val newRound = generateRound(resetSession.difficulty, resetSession.mode)

        _uiState.value = currentState.copy(
            session = resetSession,
            round = newRound,
            leftState = CardState.IDLE,
            rightState = CardState.IDLE,
            revealedLeftValue = null,
            revealedRightValue = null,
            outcome = null,
            isAnswered = false,
            hintUsed = false
        )

        analyticsManager?.logGameStart(resetSession.mode.name, resetSession.difficulty.name)

        if (resetSession.mode == GameMode.ARCADE) {
            startRoundTimer(resetSession.difficulty)
        }
    }

    fun onRewardedHeartEarned() {
        _uiState.update { currentState ->
            currentState.copy(
                session = currentState.session.copy(livesRemaining = 1),
                showOutOfLivesDialog = false
            )
        }
        soundManager?.play(SoundManager.Sfx.CORRECT)
        hapticManager?.perform(HapticManager.Impact.MEDIUM)
        resumeTimer()
    }

    fun giveUpSession() {
        _uiState.update { it.copy(showOutOfLivesDialog = false) }
        endSession()
    }

    private fun loseLife() {
        hapticManager?.perform(HapticManager.Impact.HEAVY)

        val currentLives = _uiState.value.session.livesRemaining
        val newLives = (currentLives - 1).coerceAtLeast(0)

        _uiState.update { currentState ->
            currentState.copy(
                session = currentState.session.copy(livesRemaining = newLives)
            )
        }

        if (newLives == 0) {
            roundTimer.pause()
            _uiState.update { it.copy(showOutOfLivesDialog = true) }
        }
    }

    private fun endSession() {
        roundTimer.cancel()
        soundManager?.play(SoundManager.Sfx.ROUND_COMPLETE)
        hapticManager?.perform(HapticManager.Impact.HEAVY)

        val session = _uiState.value.session
        val isNewBest = peakStreakThisRun > previousBestStreak && peakStreakThisRun > 0

        analyticsManager?.logGameEnd(session.mode.name, session.difficulty.name, session.correct, session.wrong, peakStreakThisRun)

        val summary = GameSessionSummary(
            mode = session.mode,
            difficulty = session.difficulty,
            correct = session.correct,
            wrong = session.wrong,
            bestStreakThisRun = peakStreakThisRun,
            isNewBest = isNewBest
        )

        ResultsSessionHolder.lastSummary = summary

        viewModelScope.launch {
            if (peakStreakThisRun > 0) {
                userPrefsRepository.updateBestStreakIfHigher(peakStreakThisRun)
            }
            _navEvent.emit(NavEvent.ToResults(summary))
        }
    }
}

class GameViewModelFactory(
    private val generateRound: GenerateRoundUseCase,
    private val evaluateAnswer: EvaluateAnswerUseCase,
    private val scoreUseCase: ScoreUseCase,
    private val userPrefsRepository: UserPrefsRepository,
    private val initialMode: GameMode,
    private val initialDifficulty: Difficulty,
    private val soundManager: SoundManager? = null,
    private val hapticManager: HapticManager? = null,
    private val statsRepository: StatsRepository? = null,
    private val analyticsManager: AnalyticsManager? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GameViewModel(
            generateRound = generateRound,
            evaluateAnswer = evaluateAnswer,
            scoreUseCase = scoreUseCase,
            userPrefsRepository = userPrefsRepository,
            initialMode = initialMode,
            initialDifficulty = initialDifficulty,
            soundManager = soundManager,
            hapticManager = hapticManager,
            statsRepository = statsRepository,
            analyticsManager = analyticsManager
        ) as T
    }
}
