package com.example.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import com.example.data.BlockRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InstagramReelsBlockerService : AccessibilityService() {

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private lateinit var repository: BlockRepository
    private val mainHandler = Handler(Looper.getMainLooper())
    private var lastActionTimestamp = 0L

    override fun onCreate() {
        super.onCreate()
        repository = BlockRepository.getInstance(applicationContext)
        _isServiceRunning.value = true
        Log.d(TAG, "ReelShield AccessibilityService created")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        _isServiceRunning.value = true
        Log.d(TAG, "ReelShield AccessibilityService connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val packageName = event.packageName?.toString() ?: return

        // Only monitor Instagram apps
        if (!isInstagramPackage(packageName)) {
            return
        }

        val prefs = repository.getPreferences()
        if (!prefs.isProtectionEnabled()) {
            return
        }

        val currentTime = System.currentTimeMillis()
        // Debounce rapid continuous events (within 400ms)
        if (currentTime - lastActionTimestamp < 400L) {
            return
        }

        try {
            val rootNode = rootInActiveWindow
            val isChatContext = isDirectChatContext(rootNode, event)

            // Prompt rule: "allow watching reels inside ig chat, but not allowing seeing the reels tabs"
            if (isChatContext) {
                // User is in Direct Messages / Chat. Watching reels shared inside chat is explicitly ALLOWED!
                Log.d(TAG, "Inside Direct Chat context - Reels viewing permitted.")
                return
            }

            // Detect if the user is clicking or landed on the Reels tab / Reels feed
            val isReelsTabDetected = isReelsTabOrFeed(rootNode, event)

            if (isReelsTabDetected) {
                lastActionTimestamp = currentTime
                handleReelsTabBlocked("Reels tab accessed outside of chat")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error evaluating accessibility event", e)
        }
    }

    private fun isInstagramPackage(pkg: String): Boolean {
        return pkg.equals("com.instagram.android", ignoreCase = true) ||
                pkg.equals("com.instagram.lite", ignoreCase = true) ||
                pkg.equals("com.instagram.android.preload", ignoreCase = true)
    }

    /**
     * Checks if the active screen is Instagram Direct Messages / Chat thread.
     */
    private fun isDirectChatContext(rootNode: AccessibilityNodeInfo?, event: AccessibilityEvent): Boolean {
        val className = event.className?.toString() ?: ""
        if (className.contains("DirectThreadActivity", ignoreCase = true) ||
            className.contains("DirectInboxActivity", ignoreCase = true) ||
            className.contains("DirectReplyActivity", ignoreCase = true) ||
            className.contains("DirectMessageActivity", ignoreCase = true) ||
            className.contains("DirectShareSheetFragment", ignoreCase = true)
        ) {
            return true
        }

        if (rootNode == null) return false

        // Search for known Instagram Direct chat UI node identifiers and markers
        return hasNodeMatching(rootNode) { node ->
            val viewId = node.viewIdResourceName?.lowercase() ?: ""
            val desc = node.contentDescription?.toString()?.lowercase() ?: ""
            val text = node.text?.toString()?.lowercase() ?: ""

            viewId.contains("direct_thread") ||
                    viewId.contains("direct_text_message") ||
                    viewId.contains("row_thread_composer") ||
                    viewId.contains("message_composer") ||
                    viewId.contains("direct_inbox") ||
                    viewId.contains("thread_title") ||
                    viewId.contains("direct_share") ||
                    desc.contains("direct message") ||
                    desc.contains("chat details") ||
                    text.contains("send message") ||
                    text.contains("message...")
        }
    }

    /**
     * Checks if the user clicked or entered the Reels tab or Reels full screen viewer.
     */
    private fun isReelsTabOrFeed(rootNode: AccessibilityNodeInfo?, event: AccessibilityEvent): Boolean {
        val eventText = event.text?.joinToString(" ")?.lowercase() ?: ""
        val eventDesc = event.contentDescription?.toString()?.lowercase() ?: ""
        val className = event.className?.toString() ?: ""

        // Check if event source directly represents a Reels Tab click or selection
        val source = event.source
        if (source != null) {
            val sourceDesc = source.contentDescription?.toString()?.lowercase() ?: ""
            val sourceId = source.viewIdResourceName?.lowercase() ?: ""
            if (isReelsIdentifier(sourceDesc, sourceId)) {
                return true
            }
        }

        // Check if event description or text refers to the Reels tab
        if (isReelsIdentifier(eventDesc, "")) {
            return true
        }

        // Check if the current activity/fragment is the full-screen Clips/Reels viewer
        if (className.contains("ClipsViewerActivity", ignoreCase = true) ||
            className.contains("ReelsViewerFragment", ignoreCase = true) ||
            className.contains("ClipsViewerFragment", ignoreCase = true)
        ) {
            return true
        }

        // Inspect root node to see if the Reels tab item in the bottom navigation bar is selected
        if (rootNode != null) {
            val reelsTabSelected = hasNodeMatching(rootNode) { node ->
                val desc = node.contentDescription?.toString()?.lowercase() ?: ""
                val id = node.viewIdResourceName?.lowercase() ?: ""
                val isSelected = node.isSelected
                val isReels = isReelsIdentifier(desc, id)
                isReels && isSelected
            }
            if (reelsTabSelected) {
                return true
            }

            // Check if root view has clips_viewer without being in chat
            val hasClipsViewer = hasNodeMatching(rootNode) { node ->
                val id = node.viewIdResourceName?.lowercase() ?: ""
                id.contains("clips_viewer") || id.contains("clips_video_container")
            }
            if (hasClipsViewer) {
                return true
            }
        }

        return false
    }

    private fun isReelsIdentifier(desc: String, id: String): Boolean {
        val matchesDesc = desc.equals("reels", ignoreCase = true) ||
                desc.contains("reels tab", ignoreCase = true) ||
                desc.contains("reels, tab", ignoreCase = true) ||
                desc.startsWith("reels")
        val matchesId = id.contains("tab_reels") ||
                id.contains("clips_tab") ||
                id.contains("reels_tab") ||
                id.contains("clips_viewer")
        return matchesDesc || matchesId
    }

    private fun hasNodeMatching(
        node: AccessibilityNodeInfo,
        depth: Int = 0,
        predicate: (AccessibilityNodeInfo) -> Boolean
    ): Boolean {
        if (depth > 25) return false // Prevent excessive recursion depth
        if (predicate(node)) return true

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val found = hasNodeMatching(child, depth + 1, predicate)
            if (found) return true
        }
        return false
    }

    private fun handleReelsTabBlocked(reason: String) {
        val prefs = repository.getPreferences()

        // 1. Kick the user back immediately to escape the Reels trap!
        if (prefs.isStrictKickback()) {
            val backSuccess = performGlobalAction(GLOBAL_ACTION_BACK)
            if (!backSuccess) {
                // Fallback: try navigating home
                performGlobalAction(GLOBAL_ACTION_HOME)
            }
        }

        // 2. Trigger Haptic Vibration if enabled
        if (prefs.isHapticFeedback()) {
            triggerVibration()
        }

        // 3. Show Toast Alert if enabled
        if (prefs.isShowToastAlert()) {
            mainHandler.post {
                Toast.makeText(
                    applicationContext,
                    "ReelShield: Reels tab is blocked! 🛡️ (Chat reels allowed)",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // 4. Record event in database
        repository.recordBlockAsync(
            serviceScope,
            eventType = "REELS_TAB_BLOCKED",
            source = "Instagram App",
            details = reason,
            savedSeconds = 900 // 15 mins estimated saved doomscroll session
        )

        _lastInterventionTime.value = System.currentTimeMillis()
        _interventionsCount.value += 1
        Log.i(TAG, "Reels tab blocked successfully: $reason")
    }

    private fun triggerVibration() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(120)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error vibrating", e)
        }
    }

    override fun onInterrupt() {
        Log.w(TAG, "ReelShield AccessibilityService interrupted")
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        _isServiceRunning.value = false
        Log.d(TAG, "ReelShield AccessibilityService destroyed")
    }

    companion object {
        private const val TAG = "ReelShieldService"

        private val _isServiceRunning = MutableStateFlow(false)
        val isServiceRunning: StateFlow<Boolean> = _isServiceRunning.asStateFlow()

        private val _lastInterventionTime = MutableStateFlow(0L)
        val lastInterventionTime: StateFlow<Long> = _lastInterventionTime.asStateFlow()

        private val _interventionsCount = MutableStateFlow(0)
        val interventionsCount: StateFlow<Int> = _interventionsCount.asStateFlow()

        fun isAccessibilityServiceEnabled(context: Context): Boolean {
            val serviceName = "${context.packageName}/${InstagramReelsBlockerService::class.java.canonicalName}"
            val accessibilityEnabled = try {
                Settings.Secure.getInt(
                    context.contentResolver,
                    Settings.Secure.ACCESSIBILITY_ENABLED
                )
            } catch (e: Settings.SettingNotFoundException) {
                0
            }

            if (accessibilityEnabled == 1) {
                val settingValue = Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
                ) ?: return false

                return settingValue.split(":").any {
                    it.equals(serviceName, ignoreCase = true) ||
                            it.contains(InstagramReelsBlockerService::class.java.simpleName, ignoreCase = true)
                }
            }
            return false
        }
    }
}
