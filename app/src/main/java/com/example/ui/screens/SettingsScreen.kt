package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ShieldSettings
import com.example.ui.theme.ShieldCardBg
import com.example.ui.theme.ShieldCardBorder
import com.example.ui.theme.ShieldCardSurface
import com.example.ui.theme.ShieldDarkBg
import com.example.ui.theme.ShieldPrimary
import com.example.ui.theme.StatusActiveGreen
import com.example.ui.theme.StatusBlockedRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: ShieldSettings,
    onSetAllowReelsInChat: (Boolean) -> Unit,
    onSetStrictKickback: (Boolean) -> Unit,
    onSetHapticFeedback: (Boolean) -> Unit,
    onSetBlockExploreReels: (Boolean) -> Unit,
    onSetShowToastAlert: (Boolean) -> Unit,
    onClearHistory: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    BackHandler {
        onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Protection Rules & Settings",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ShieldDarkBg
                )
            )
        },
        containerColor = ShieldDarkBg,
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Core Rules
            item {
                Text(
                    text = "Core Protection Rules",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            // Setting 1: Allow in Chat (User's primary requirement)
            item {
                SettingSwitchCard(
                    title = "Allow Reels in Direct Chat",
                    subtitle = "Permits watching reels shared in Direct Message threads while keeping the main Reels tab blocked.",
                    icon = Icons.Default.Chat,
                    iconColor = StatusActiveGreen,
                    isChecked = settings.allowReelsInChat,
                    onCheckedChange = onSetAllowReelsInChat,
                    testTag = "setting_allow_chat_reels"
                )
            }

            // Setting 2: Strict Kickback
            item {
                SettingSwitchCard(
                    title = "Strict Kickback (Instant Back Action)",
                    subtitle = "Instantly simulates Android Back button when user taps the Reels tab to immediately exit the feed.",
                    icon = Icons.Default.Shield,
                    iconColor = ShieldPrimary,
                    isChecked = settings.strictKickback,
                    onCheckedChange = onSetStrictKickback,
                    testTag = "setting_strict_kickback"
                )
            }

            // Setting 3: Haptic Feedback
            item {
                SettingSwitchCard(
                    title = "Haptic Vibration Alert",
                    subtitle = "Vibrate phone briefly whenever ReelShield blocks an attempt to open the Reels tab.",
                    icon = Icons.Default.Vibration,
                    iconColor = Color(0xFFFFB300),
                    isChecked = settings.hapticFeedback,
                    onCheckedChange = onSetHapticFeedback,
                    testTag = "setting_haptic_feedback"
                )
            }

            // Setting 4: Toast Alert
            item {
                SettingSwitchCard(
                    title = "Show Toast Alert",
                    subtitle = "Display a friendly nudge popup when Reels tab is blocked.",
                    icon = Icons.Default.Notifications,
                    iconColor = ShieldPrimary,
                    isChecked = settings.showToastAlert,
                    onCheckedChange = onSetShowToastAlert,
                    testTag = "setting_toast_alert"
                )
            }

            // Setting 5: Explore Feed Reels
            item {
                SettingSwitchCard(
                    title = "Block Explore Feed Reels Previews",
                    subtitle = "Optional: Also block video clips previews inside the Instagram Explore search grid.",
                    icon = Icons.Default.Explore,
                    iconColor = Color(0xFF64B5F6),
                    isChecked = settings.blockExploreReels,
                    onCheckedChange = onSetBlockExploreReels,
                    testTag = "setting_block_explore"
                )
            }

            // Section 2: Data & Stats
            item {
                Text(
                    text = "Data & History",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, ShieldCardBorder, RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = ShieldCardBg)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Reset Wellbeing Statistics",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Clear all logged blocks and time saved metrics stored on this device.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        OutlinedButton(
                            onClick = { showClearConfirmDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("clear_history_button")
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = StatusBlockedRed, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Clear Activity History", color = StatusBlockedRed)
                        }
                    }
                }
            }

            // Section 3: About
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = ShieldCardSurface)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "ReelShield v1.0",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Empowering you to stay connected via Direct Messages without falling into endless video doomscrolling loops.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        if (showClearConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showClearConfirmDialog = false },
                title = { Text("Reset Activity Log?", color = TextPrimary) },
                text = { Text("This will reset all blocked counts and saved time metrics. This action cannot be undone.", color = TextSecondary) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onClearHistory()
                            showClearConfirmDialog = false
                        }
                    ) {
                        Text("Reset", color = StatusBlockedRed, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearConfirmDialog = false }) {
                        Text("Cancel", color = TextSecondary)
                    }
                },
                containerColor = ShieldCardBg
            )
        }
    }
}

@Composable
fun SettingSwitchCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, ShieldCardBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = ShieldCardBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = ShieldPrimary,
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = ShieldCardSurface
                ),
                modifier = Modifier.testTag(testTag)
            )
        }
    }
}
