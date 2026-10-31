package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BlockEvent
import com.example.ui.theme.ReelInstagramGradientEnd
import com.example.ui.theme.ReelInstagramGradientMid
import com.example.ui.theme.ReelInstagramGradientStart
import com.example.ui.theme.ShieldCardBg
import com.example.ui.theme.ShieldCardBorder
import com.example.ui.theme.ShieldCardSurface
import com.example.ui.theme.ShieldDarkBg
import com.example.ui.theme.ShieldPrimary
import com.example.ui.theme.ShieldSecondary
import com.example.ui.theme.ShieldTertiary
import com.example.ui.theme.StatusActiveGreen
import com.example.ui.theme.StatusBlockedRed
import com.example.ui.theme.StatusWarningOrange
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.viewmodel.ReelShieldUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: ReelShieldUiState,
    onToggleProtection: (Boolean) -> Unit,
    onNavigateToSimulator: () -> Unit,
    onNavigateToSetup: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isProtected = uiState.settings.isProtectionEnabled
    val isServiceActive = uiState.isServiceEnabledInSystem || uiState.isServiceRunning

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ShieldDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(ShieldPrimary, ShieldSecondary)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "ReelShield Icon",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "ReelShield",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Instagram Focus Guard",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                IconButton(
                    onClick = onNavigateToSettings,
                    modifier = Modifier.testTag("settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = TextSecondary
                    )
                }
            }
        }

        // Hero Status Card
        item {
            HeroStatusCard(
                isProtectionEnabled = isProtected,
                isServiceActive = isServiceActive,
                onToggleProtection = onToggleProtection,
                onNavigateToSetup = onNavigateToSetup
            )
        }

        // Active Rules Banner (Crucial user request highlight)
        item {
            RuleStatusCard()
        }

        // Interactive Sandbox / Simulator Callout
        item {
            SimulatorPromoCard(onNavigateToSimulator = onNavigateToSimulator)
        }

        // Metrics Grid
        item {
            Text(
                text = "Your Focus Impact",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Today's Blocks",
                    value = "${uiState.todayBlocks}",
                    unit = "tabs thwarted",
                    icon = Icons.Default.Security,
                    color = ShieldPrimary
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Time Saved",
                    value = "${uiState.totalMinutesSaved}",
                    unit = "minutes saved",
                    icon = Icons.Default.HourglassBottom,
                    color = ShieldTertiary
                )
            }
        }

        // Recent Activity Header & Feed
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Live Protection Activity",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "${uiState.totalBlocks} total blocks",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary
                )
            }
        }

        if (uiState.recentEvents.isEmpty()) {
            item {
                EmptyActivityCard(onNavigateToSimulator = onNavigateToSimulator)
            }
        } else {
            items(uiState.recentEvents.take(15)) { event ->
                ActivityEventItem(event = event)
            }
        }
    }
}

@Composable
fun HeroStatusCard(
    isProtectionEnabled: Boolean,
    isServiceActive: Boolean,
    onToggleProtection: (Boolean) -> Unit,
    onNavigateToSetup: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isProtectionEnabled) 1.08f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val cardBorderColor by animateColorAsState(
        targetValue = if (isProtectionEnabled && isServiceActive) StatusActiveGreen.copy(alpha = 0.5f)
        else if (isProtectionEnabled) StatusWarningOrange.copy(alpha = 0.5f)
        else ShieldCardBorder,
        label = "borderColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.5.dp, cardBorderColor, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = ShieldCardBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Pulsing Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(
                        if (isProtectionEnabled && isServiceActive) {
                            StatusActiveGreen.copy(alpha = 0.15f)
                        } else if (isProtectionEnabled) {
                            StatusWarningOrange.copy(alpha = 0.15f)
                        } else {
                            Color.Gray.copy(alpha = 0.15f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isProtectionEnabled) Icons.Default.Shield else Icons.Default.Warning,
                    contentDescription = "Shield Status",
                    tint = if (isProtectionEnabled && isServiceActive) StatusActiveGreen
                    else if (isProtectionEnabled) StatusWarningOrange
                    else Color.Gray,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (isProtectionEnabled && isServiceActive) "ReelShield is Protecting"
                else if (isProtectionEnabled) "Service Setup Needed"
                else "Protection Paused",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = if (isProtectionEnabled && isServiceActive) {
                    "Instagram Reels tabs are blocked. Direct chat reels remain allowed."
                } else if (isProtectionEnabled) {
                    "Enable Accessibility Service in Android Settings to activate blocking."
                } else {
                    "Turn on protection to block endless Reels scrolling."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            // Setup or Toggle Button
            if (isProtectionEnabled && !isServiceActive) {
                Button(
                    onClick = onNavigateToSetup,
                    colors = ButtonDefaults.buttonColors(containerColor = StatusWarningOrange),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("enable_service_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enable in Android Settings", color = Color.Black, fontWeight = FontWeight.SemiBold)
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ShieldCardSurface)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Blocker Master Switch",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Switch(
                        checked = isProtectionEnabled,
                        onCheckedChange = onToggleProtection,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = ShieldPrimary,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = ShieldCardBg
                        ),
                        modifier = Modifier.testTag("protection_master_switch")
                    )
                }
            }
        }
    }
}

@Composable
fun RuleStatusCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, ShieldCardBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = ShieldCardBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Configured Rules",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )

            // Rule 1: Reels Tab
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(ShieldCardSurface)
                    .padding(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(StatusBlockedRed.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🚫", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Reels Navigation Tab",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Blocked • Automatically kicks back to Feed",
                        style = MaterialTheme.typography.bodySmall,
                        color = StatusBlockedRed
                    )
                }
            }

            // Rule 2: Chat Reels
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(ShieldCardSurface)
                    .padding(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(StatusActiveGreen.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("💬", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Reels in Direct Messages",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Allowed • Watch reels sent by friends in chat",
                        style = MaterialTheme.typography.bodySmall,
                        color = StatusActiveGreen
                    )
                }
            }
        }
    }
}

@Composable
fun SimulatorPromoCard(onNavigateToSimulator: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onNavigateToSimulator() }
            .border(
                1.5.dp,
                Brush.horizontalGradient(
                    listOf(ReelInstagramGradientStart, ReelInstagramGradientMid, ReelInstagramGradientEnd)
                ),
                RoundedCornerShape(16.dp)
            ),
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
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(ReelInstagramGradientStart, ReelInstagramGradientMid)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Test Simulator",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Interactive Instagram Sandbox",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Test blocking the Reels tab & watching reels in chat directly in the app!",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onNavigateToSimulator,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ShieldPrimary),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier.testTag("open_sandbox_button")
            ) {
                Text("Try", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    unit: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, ShieldCardBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = ShieldCardBg)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = unit,
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary
            )
        }
    }
}

@Composable
fun ActivityEventItem(event: BlockEvent) {
    val isChatReel = event.eventType == "CHAT_REEL_PERMITTED"
    val timeFormat = SimpleDateFormat("HH:mm • MMM d", Locale.getDefault())
    val formattedTime = timeFormat.format(Date(event.timestamp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ShieldCardBg)
            .border(1.dp, ShieldCardBorder, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    if (isChatReel) StatusActiveGreen.copy(alpha = 0.2f)
                    else StatusBlockedRed.copy(alpha = 0.2f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isChatReel) Icons.Default.Chat else Icons.Default.Shield,
                contentDescription = null,
                tint = if (isChatReel) StatusActiveGreen else StatusBlockedRed,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isChatReel) "Chat Reel Permitted" else "Blocked Reels Tab",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isChatReel) StatusActiveGreen else TextPrimary
                )
                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
            }
            Text(
                text = "${event.source} • ${event.details}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = 1
            )
        }
    }
}

@Composable
fun EmptyActivityCard(onNavigateToSimulator: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = ShieldCardBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "No Activity Logged Yet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = "Launch Instagram or test the interactive sandbox above to see live blocks recorded here.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )
            OutlinedButton(
                onClick = onNavigateToSimulator,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Test in Sandbox")
            }
        }
    }
}
