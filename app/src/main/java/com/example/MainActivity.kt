package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SetupGuideScreen
import com.example.ui.screens.SimulatorScreen
import com.example.ui.theme.ReelShieldTheme
import com.example.ui.viewmodel.ReelShieldViewModel

enum class AppDestination {
    HOME,
    SIMULATOR,
    SETUP_GUIDE,
    SETTINGS
}

class MainActivity : ComponentActivity() {

    private val viewModel: ReelShieldViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ReelShieldTheme {
                val lifecycleOwner = LocalLifecycleOwner.current
                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) {
                            viewModel.refreshServiceStatus()
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }

                ReelShieldApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ReelShieldApp(viewModel: ReelShieldViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var currentDestination by remember { mutableStateOf(AppDestination.HOME) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        AnimatedContent(
            targetState = currentDestination,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "screen_transition"
        ) { destination ->
            when (destination) {
                AppDestination.HOME -> {
                    HomeScreen(
                        uiState = uiState,
                        onToggleProtection = { viewModel.toggleProtection(it) },
                        onNavigateToSimulator = { currentDestination = AppDestination.SIMULATOR },
                        onNavigateToSetup = { currentDestination = AppDestination.SETUP_GUIDE },
                        onNavigateToSettings = { currentDestination = AppDestination.SETTINGS }
                    )
                }
                AppDestination.SIMULATOR -> {
                    SimulatorScreen(
                        viewModel = viewModel,
                        onNavigateBackToApp = { currentDestination = AppDestination.HOME }
                    )
                }
                AppDestination.SETUP_GUIDE -> {
                    SetupGuideScreen(
                        isServiceActive = uiState.isServiceEnabledInSystem || uiState.isServiceRunning,
                        onRefreshStatus = { viewModel.refreshServiceStatus() },
                        onBack = { currentDestination = AppDestination.HOME }
                    )
                }
                AppDestination.SETTINGS -> {
                    SettingsScreen(
                        settings = uiState.settings,
                        onSetAllowReelsInChat = { viewModel.setAllowReelsInChat(it) },
                        onSetStrictKickback = { viewModel.setStrictKickback(it) },
                        onSetHapticFeedback = { viewModel.setHapticFeedback(it) },
                        onSetBlockExploreReels = { viewModel.setBlockExploreReels(it) },
                        onSetShowToastAlert = { viewModel.setShowToastAlert(it) },
                        onClearHistory = { viewModel.clearHistory() },
                        onBack = { currentDestination = AppDestination.HOME }
                    )
                }
            }
        }
    }
}
