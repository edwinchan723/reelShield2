package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ShieldSettings(
    val isProtectionEnabled: Boolean = true,
    val allowReelsInChat: Boolean = true,
    val strictKickback: Boolean = true,
    val hapticFeedback: Boolean = true,
    val blockExploreReels: Boolean = false,
    val showToastAlert: Boolean = true
)

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("reel_shield_prefs", Context.MODE_PRIVATE)

    private val _settingsFlow = MutableStateFlow(loadSettings())
    val settingsFlow: StateFlow<ShieldSettings> = _settingsFlow.asStateFlow()

    init {
        _settingsFlow.value = loadSettings()
    }

    private fun loadSettings(): ShieldSettings {
        return ShieldSettings(
            isProtectionEnabled = prefs.getBoolean(KEY_PROTECTION_ENABLED, true),
            allowReelsInChat = prefs.getBoolean(KEY_ALLOW_REELS_IN_CHAT, true),
            strictKickback = prefs.getBoolean(KEY_STRICT_KICKBACK, true),
            hapticFeedback = prefs.getBoolean(KEY_HAPTIC_FEEDBACK, true),
            blockExploreReels = prefs.getBoolean(KEY_BLOCK_EXPLORE_REELS, false),
            showToastAlert = prefs.getBoolean(KEY_SHOW_TOAST_ALERT, true)
        )
    }

    fun isProtectionEnabled(): Boolean = prefs.getBoolean(KEY_PROTECTION_ENABLED, true)
    fun allowReelsInChat(): Boolean = prefs.getBoolean(KEY_ALLOW_REELS_IN_CHAT, true)
    fun isStrictKickback(): Boolean = prefs.getBoolean(KEY_STRICT_KICKBACK, true)
    fun isHapticFeedback(): Boolean = prefs.getBoolean(KEY_HAPTIC_FEEDBACK, true)
    fun isBlockExploreReels(): Boolean = prefs.getBoolean(KEY_BLOCK_EXPLORE_REELS, false)
    fun isShowToastAlert(): Boolean = prefs.getBoolean(KEY_SHOW_TOAST_ALERT, true)

    fun updateProtectionEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_PROTECTION_ENABLED, enabled).apply()
        _settingsFlow.value = loadSettings()
    }

    fun updateAllowReelsInChat(allowed: Boolean) {
        prefs.edit().putBoolean(KEY_ALLOW_REELS_IN_CHAT, allowed).apply()
        _settingsFlow.value = loadSettings()
    }

    fun updateStrictKickback(strict: Boolean) {
        prefs.edit().putBoolean(KEY_STRICT_KICKBACK, strict).apply()
        _settingsFlow.value = loadSettings()
    }

    fun updateHapticFeedback(haptic: Boolean) {
        prefs.edit().putBoolean(KEY_HAPTIC_FEEDBACK, haptic).apply()
        _settingsFlow.value = loadSettings()
    }

    fun updateBlockExploreReels(blockExplore: Boolean) {
        prefs.edit().putBoolean(KEY_BLOCK_EXPLORE_REELS, blockExplore).apply()
        _settingsFlow.value = loadSettings()
    }

    fun updateShowToastAlert(show: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_TOAST_ALERT, show).apply()
        _settingsFlow.value = loadSettings()
    }

    companion object {
        private const val KEY_PROTECTION_ENABLED = "key_protection_enabled"
        private const val KEY_ALLOW_REELS_IN_CHAT = "key_allow_reels_in_chat"
        private const val KEY_STRICT_KICKBACK = "key_strict_kickback"
        private const val KEY_HAPTIC_FEEDBACK = "key_haptic_feedback"
        private const val KEY_BLOCK_EXPLORE_REELS = "key_block_explore_reels"
        private const val KEY_SHOW_TOAST_ALERT = "key_show_toast_alert"

        @Volatile
        private var INSTANCE: PreferencesManager? = null

        fun getInstance(context: Context): PreferencesManager {
            return INSTANCE ?: synchronized(this) {
                val instance = PreferencesManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
