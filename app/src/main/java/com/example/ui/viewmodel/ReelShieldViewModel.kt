package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BlockEvent
import com.example.data.BlockRepository
import com.example.data.ShieldSettings
import com.example.service.InstagramReelsBlockerService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SimulatorScreen {
    FEED,
    EXPLORE,
    DIRECT_INBOX,
    DIRECT_CHAT_SARAH,
    CHAT_REEL_PLAYER,
    REELS_FEED_BLOCKED
}

data class SimulatorState(
    val currentScreen: SimulatorScreen = SimulatorScreen.FEED,
    val isBlockOverlayVisible: Boolean = false,
    val blockTitle: String = "Reels Tab Blocked!",
    val blockSubtitle: String = "ReelShield kicked you back to protect your focus.",
    val unreadChatCount: Int = 1,
    val chatReelWatchedCount: Int = 0
)

data class ReelShieldUiState(
    val isServiceEnabledInSystem: Boolean = false,
    val isServiceRunning: Boolean = false,
    val settings: ShieldSettings = ShieldSettings(),
    val todayBlocks: Int = 0,
    val totalBlocks: Int = 0,
    val totalMinutesSaved: Int = 0,
    val streakDays: Int = 3,
    val recentEvents: List<BlockEvent> = emptyList(),
    val simulatorState: SimulatorState = SimulatorState()
)

class ReelShieldViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BlockRepository.getInstance(application)
    private val context = application.applicationContext

    private val _isServiceEnabled = MutableStateFlow(
        InstagramReelsBlockerService.isAccessibilityServiceEnabled(context)
    )
    private val _simulatorState = MutableStateFlow(SimulatorState())

    private data class ServiceStatus(val enabled: Boolean, val running: Boolean)
    private data class BlockStats(val today: Int, val total: Int, val minutesSaved: Int)

    private val serviceStatusFlow = combine(
        _isServiceEnabled,
        InstagramReelsBlockerService.isServiceRunning
    ) { enabled, running ->
        ServiceStatus(enabled, running)
    }

    private val statsFlow = combine(
        repository.getTodayBlocksCount(),
        repository.totalBlocksCount,
        repository.totalSecondsSaved
    ) { today, total, seconds ->
        BlockStats(today, total, (seconds ?: 0) / 60)
    }

    val uiState: StateFlow<ReelShieldUiState> = combine(
        serviceStatusFlow,
        statsFlow,
        repository.settingsFlow,
        repository.recentEvents,
        _simulatorState
    ) { serviceStatus, stats, settings, events, simState ->
        ReelShieldUiState(
            isServiceEnabledInSystem = serviceStatus.enabled,
            isServiceRunning = serviceStatus.running,
            settings = settings,
            todayBlocks = stats.today,
            totalBlocks = stats.total,
            totalMinutesSaved = stats.minutesSaved,
            streakDays = if (stats.total > 0) 3 else 1,
            recentEvents = events,
            simulatorState = simState
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ReelShieldUiState()
    )

    fun refreshServiceStatus() {
        _isServiceEnabled.value = InstagramReelsBlockerService.isAccessibilityServiceEnabled(context)
    }

    fun toggleProtection(enabled: Boolean) {
        repository.getPreferences().updateProtectionEnabled(enabled)
    }

    fun setAllowReelsInChat(allowed: Boolean) {
        repository.getPreferences().updateAllowReelsInChat(allowed)
    }

    fun setStrictKickback(strict: Boolean) {
        repository.getPreferences().updateStrictKickback(strict)
    }

    fun setHapticFeedback(haptic: Boolean) {
        repository.getPreferences().updateHapticFeedback(haptic)
    }

    fun setBlockExploreReels(blockExplore: Boolean) {
        repository.getPreferences().updateBlockExploreReels(blockExplore)
    }

    fun setShowToastAlert(show: Boolean) {
        repository.getPreferences().updateShowToastAlert(show)
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    // --- Simulator Actions (Interactive Testing) ---

    fun onSimulatorTabSelected(tab: String) {
        when (tab) {
            "HOME" -> {
                _simulatorState.value = _simulatorState.value.copy(
                    currentScreen = SimulatorScreen.FEED,
                    isBlockOverlayVisible = false
                )
            }
            "EXPLORE" -> {
                _simulatorState.value = _simulatorState.value.copy(
                    currentScreen = SimulatorScreen.EXPLORE,
                    isBlockOverlayVisible = false
                )
            }
            "REELS" -> {
                val settings = repository.getPreferences()
                if (settings.isProtectionEnabled()) {
                    triggerSimulatedReelsBlock()
                } else {
                    _simulatorState.value = _simulatorState.value.copy(
                        currentScreen = SimulatorScreen.REELS_FEED_BLOCKED,
                        isBlockOverlayVisible = false
                    )
                }
            }
            "PROFILE" -> {
                // Keep on feed
            }
        }
    }

    fun triggerSimulatedReelsBlock() {
        // Haptic feedback
        if (repository.getPreferences().isHapticFeedback()) {
            triggerVibration()
        }

        // Record block in DB
        viewModelScope.launch {
            repository.recordBlock(
                eventType = "REELS_TAB_BLOCKED",
                source = "IG Simulator",
                details = "User tapped Reels tab - automatically blocked & redirected!",
                savedSeconds = 900 // 15 mins saved
            )
        }

        // Show block overlay then auto-kickback to Feed!
        _simulatorState.value = _simulatorState.value.copy(
            isBlockOverlayVisible = true,
            blockTitle = "Reels Tab Blocked! 🛡️",
            blockSubtitle = "ReelShield kicked you out of the reels loop. Focus preserved (+15m saved)."
        )

        viewModelScope.launch {
            delay(1600)
            _simulatorState.value = _simulatorState.value.copy(
                isBlockOverlayVisible = false,
                currentScreen = SimulatorScreen.FEED
            )
        }
    }

    fun openSimulatorInbox() {
        _simulatorState.value = _simulatorState.value.copy(
            currentScreen = SimulatorScreen.DIRECT_INBOX,
            isBlockOverlayVisible = false
        )
    }

    fun openSimulatorChatSarah() {
        _simulatorState.value = _simulatorState.value.copy(
            currentScreen = SimulatorScreen.DIRECT_CHAT_SARAH,
            unreadChatCount = 0,
            isBlockOverlayVisible = false
        )
    }

    fun playSimulatorChatReel() {
        val currentCount = _simulatorState.value.chatReelWatchedCount
        _simulatorState.value = _simulatorState.value.copy(
            currentScreen = SimulatorScreen.CHAT_REEL_PLAYER,
            chatReelWatchedCount = currentCount + 1,
            isBlockOverlayVisible = false
        )

        // Record permitted event for logging
        viewModelScope.launch {
            repository.recordBlock(
                eventType = "CHAT_REEL_PERMITTED",
                source = "Direct Chat",
                details = "Reel opened inside IG Direct chat (Explicitly allowed)",
                savedSeconds = 0
            )
        }
    }

    fun backFromSimulatorScreen() {
        when (_simulatorState.value.currentScreen) {
            SimulatorScreen.CHAT_REEL_PLAYER -> {
                _simulatorState.value = _simulatorState.value.copy(
                    currentScreen = SimulatorScreen.DIRECT_CHAT_SARAH
                )
            }
            SimulatorScreen.DIRECT_CHAT_SARAH -> {
                _simulatorState.value = _simulatorState.value.copy(
                    currentScreen = SimulatorScreen.DIRECT_INBOX
                )
            }
            SimulatorScreen.DIRECT_INBOX -> {
                _simulatorState.value = _simulatorState.value.copy(
                    currentScreen = SimulatorScreen.FEED
                )
            }
            SimulatorScreen.EXPLORE -> {
                _simulatorState.value = _simulatorState.value.copy(
                    currentScreen = SimulatorScreen.FEED
                )
            }
            SimulatorScreen.REELS_FEED_BLOCKED -> {
                _simulatorState.value = _simulatorState.value.copy(
                    currentScreen = SimulatorScreen.FEED
                )
            }
            SimulatorScreen.FEED -> {
                // Already at root
            }
        }
    }

    private fun triggerVibration() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(100)
            }
        } catch (_: Exception) {}
    }
}
