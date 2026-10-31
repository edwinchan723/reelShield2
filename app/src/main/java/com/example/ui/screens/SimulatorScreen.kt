package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ReelInstagramGradientEnd
import com.example.ui.theme.ReelInstagramGradientMid
import com.example.ui.theme.ReelInstagramGradientStart
import com.example.ui.theme.ShieldDarkBg
import com.example.ui.theme.ShieldPrimary
import com.example.ui.theme.ShieldSecondary
import com.example.ui.theme.StatusActiveGreen
import com.example.ui.theme.StatusBlockedRed
import com.example.ui.viewmodel.ReelShieldViewModel
import com.example.ui.viewmodel.SimulatorScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimulatorScreen(
    viewModel: ReelShieldViewModel,
    onNavigateBackToApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.uiState.value
    val simState = uiState.simulatorState

    BackHandler {
        if (simState.currentScreen == SimulatorScreen.FEED) {
            onNavigateBackToApp()
        } else {
            viewModel.backFromSimulatorScreen()
        }
    }

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        Scaffold(
            topBar = {
                SimulatorTopBar(
                    currentScreen = simState.currentScreen,
                    unreadChatCount = simState.unreadChatCount,
                    onBackClick = {
                        if (simState.currentScreen == SimulatorScreen.FEED) {
                            onNavigateBackToApp()
                        } else {
                            viewModel.backFromSimulatorScreen()
                        }
                    },
                    onOpenInbox = { viewModel.openSimulatorInbox() },
                    onExitSimulator = onNavigateBackToApp
                )
            },
            bottomBar = {
                // Bottom bar shown on Feed and Explore
                if (simState.currentScreen == SimulatorScreen.FEED ||
                    simState.currentScreen == SimulatorScreen.EXPLORE
                ) {
                    SimulatorBottomBar(
                        currentScreen = simState.currentScreen,
                        onTabClick = { viewModel.onSimulatorTabSelected(it) }
                    )
                }
            },
            containerColor = Color.Black
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (simState.currentScreen) {
                    SimulatorScreen.FEED -> SimulatorFeedView(
                        onOpenChatSarah = { viewModel.openSimulatorChatSarah() }
                    )
                    SimulatorScreen.EXPLORE -> SimulatorExploreView()
                    SimulatorScreen.DIRECT_INBOX -> SimulatorInboxView(
                        onOpenChatSarah = { viewModel.openSimulatorChatSarah() }
                    )
                    SimulatorScreen.DIRECT_CHAT_SARAH -> SimulatorChatView(
                        onPlayChatReel = { viewModel.playSimulatorChatReel() }
                    )
                    SimulatorScreen.CHAT_REEL_PLAYER -> SimulatorChatReelPlayerView(
                        onBack = { viewModel.backFromSimulatorScreen() }
                    )
                    SimulatorScreen.REELS_FEED_BLOCKED -> {
                        // Normally intercepted by overlay, fallback display
                        SimulatorFeedView(onOpenChatSarah = { viewModel.openSimulatorChatSarah() })
                    }
                }

                // Interactive Instructions Banner at top of sandbox
                if (simState.currentScreen == SimulatorScreen.FEED) {
                    SandboxInstructionsBanner(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(12.dp),
                        onOpenInbox = { viewModel.openSimulatorInbox() },
                        onTapReels = { viewModel.onSimulatorTabSelected("REELS") }
                    )
                }
            }
        }

        // Animated ReelShield Interception Overlay
        AnimatedVisibility(
            visible = simState.isBlockOverlayVisible,
            enter = fadeIn() + scaleIn(initialScale = 0.85f),
            exit = fadeOut() + scaleOut(targetScale = 0.9f)
        ) {
            ShieldBlockInterceptionOverlay(
                title = simState.blockTitle,
                subtitle = simState.blockSubtitle
            )
        }
    }
}

@Composable
fun SimulatorTopBar(
    currentScreen: SimulatorScreen,
    unreadChatCount: Int,
    onBackClick: () -> Unit,
    onOpenInbox: () -> Unit,
    onExitSimulator: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF121212))
    ) {
        // App header bar indicating sandbox mode
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ShieldDarkBg)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(StatusActiveGreen)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "ReelShield Sandbox Simulator",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Text(
                text = "Tap to Exit ✕",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = ShieldPrimary,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onExitSimulator() }
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                    .testTag("exit_sandbox_button")
            )
        }

        // Instagram navigation header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (currentScreen == SimulatorScreen.FEED || currentScreen == SimulatorScreen.EXPLORE) {
                Text(
                    text = "Instagram",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    color = Color.White
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "Notifications",
                            tint = Color.White
                        )
                    }
                    Box {
                        IconButton(
                            onClick = onOpenInbox,
                            modifier = Modifier.testTag("ig_direct_inbox_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Direct Messages",
                                tint = Color.White
                            )
                        }
                        if (unreadChatCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 6.dp, end = 6.dp)
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(StatusBlockedRed),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$unreadChatCount",
                                    fontSize = 10.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("ig_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = when (currentScreen) {
                            SimulatorScreen.DIRECT_INBOX -> "Direct Messages"
                            SimulatorScreen.DIRECT_CHAT_SARAH -> "Sarah Jenkins"
                            SimulatorScreen.CHAT_REEL_PLAYER -> "Shared Reel Video"
                            else -> "Instagram"
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun SimulatorBottomBar(
    currentScreen: SimulatorScreen,
    onTabClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color(0xFF121212))
            .border(0.5.dp, Color(0xFF262626)),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Home
        IconButton(
            onClick = { onTabClick("HOME") },
            modifier = Modifier.testTag("ig_tab_home")
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Home",
                tint = if (currentScreen == SimulatorScreen.FEED) Color.White else Color.Gray
            )
        }

        // Search
        IconButton(
            onClick = { onTabClick("EXPLORE") },
            modifier = Modifier.testTag("ig_tab_search")
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = if (currentScreen == SimulatorScreen.EXPLORE) Color.White else Color.Gray
            )
        }

        // Create
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Default.AddBox,
                contentDescription = "Create",
                tint = Color.Gray
            )
        }

        // Reels Tab (Target for blocking!)
        Box(
            modifier = Modifier
                .clickable { onTabClick("REELS") }
                .padding(8.dp)
                .testTag("ig_tab_reels"),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box {
                    Icon(
                        imageVector = Icons.Default.Movie,
                        contentDescription = "Reels",
                        tint = Color(0xFFFF5252),
                        modifier = Modifier.size(26.dp)
                    )
                    // Shield guard badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(StatusBlockedRed)
                    )
                }
                Text(
                    text = "Reels",
                    fontSize = 9.sp,
                    color = Color(0xFFFF5252),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Profile
        IconButton(onClick = { onTabClick("PROFILE") }) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun SandboxInstructionsBanner(
    modifier: Modifier = Modifier,
    onOpenInbox: () -> Unit,
    onTapReels: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, ShieldPrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xEE1A1B28))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = ShieldPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Test the exact two rules requested:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onTapReels,
                    colors = ButtonDefaults.buttonColors(containerColor = StatusBlockedRed.copy(alpha = 0.8f)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("1. Tap Reels Tab (Block)", fontSize = 11.sp, maxLines = 1)
                }
                Button(
                    onClick = onOpenInbox,
                    colors = ButtonDefaults.buttonColors(containerColor = StatusActiveGreen.copy(alpha = 0.8f)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("2. Open DM Reel (Allow)", fontSize = 11.sp, maxLines = 1, color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun SimulatorFeedView(onOpenChatSarah: () -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentPadding = PaddingValues(top = 90.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Stories Bar
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                items(5) { index ->
                    val names = listOf("Your Story", "Sarah", "Alex", "DesignHub", "NationalGeographic")
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            if (index == 1) onOpenChatSarah()
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            ReelInstagramGradientStart,
                                            ReelInstagramGradientMid,
                                            ReelInstagramGradientEnd
                                        )
                                    )
                                )
                                .padding(2.5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(Color(0xFF222222)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = names[index].take(2).uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = names[index],
                            fontSize = 11.sp,
                            color = Color.White,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Feed Post 1
        item {
            SimulatorPostCard(
                username = "nationalgeographic",
                location = "Banff National Park, Canada",
                caption = "Majestic sunset reflection across Lake Louise. Nature at its purest stillness. 🏔️✨ #nature #photography",
                likes = "48,219 likes",
                imageBgColor = Color(0xFF1E3A5F)
            )
        }

        // Feed Post 2
        item {
            SimulatorPostCard(
                username = "tech_insider",
                location = "Silicon Valley",
                caption = "Smart habits: Digital minimalism helps software engineers reclaim up to 2.5 hours every day! 💡🧘",
                likes = "12,940 likes",
                imageBgColor = Color(0xFF2C1E5F)
            )
        }
    }
}

@Composable
fun SimulatorPostCard(
    username: String,
    location: String,
    caption: String,
    likes: String,
    imageBgColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
    ) {
        // Author header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.DarkGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(username.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(username, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(location, color = Color.Gray, fontSize = 11.sp)
                }
            }
            Icon(imageVector = Icons.Default.MoreVert, contentDescription = null, tint = Color.White)
        }

        // Post Image placeholder with artistic gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(imageBgColor, Color(0xFF0F172A))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📸 Standard Feed Photo", color = Color.White, fontWeight = FontWeight.SemiBold)
                Text("(Allowed & visible in standard home feed)", color = Color.LightGray, fontSize = 12.sp)
            }
        }

        // Actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Icon(imageVector = Icons.Default.FavoriteBorder, contentDescription = null, tint = Color.White)
                Icon(imageVector = Icons.Default.ChatBubbleOutline, contentDescription = null, tint = Color.White)
                Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color.White)
            }
            Icon(imageVector = Icons.Default.BookmarkBorder, contentDescription = null, tint = Color.White)
        }

        // Likes & Caption
        Column(modifier = Modifier.padding(horizontal = 12.dp)) {
            Text(likes, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$username $caption",
                color = Color.White,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("View all 184 comments", color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
fun SimulatorExploreView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF262626))
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Search Instagram...", color = Color.Gray, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Explore Grid (Photo Discovery)",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Grid mockup
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(3) { rowIdx ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    repeat(3) { colIdx ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(110.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    Color(
                                        red = (40 + rowIdx * 25) % 255,
                                        green = (60 + colIdx * 30) % 255,
                                        blue = (100 + (rowIdx + colIdx) * 20) % 255
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Photo",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SimulatorInboxView(onOpenChatSarah: () -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 12.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Messages",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        // Sarah's Chat item containing the shared reel!
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onOpenChatSarah() }
                    .border(1.dp, StatusActiveGreen.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .testTag("chat_item_sarah"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(ShieldPrimary, ShieldSecondary)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("SJ", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Sarah Jenkins", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("2m ago", color = Color.Gray, fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🎬 Sent a Reel: \"Check this cat!\"",
                                color = StatusActiveGreen,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(StatusActiveGreen)
                    )
                }
            }
        }

        // Other chats
        item {
            SimulatorChatItem(
                name = "Alex Wong",
                initials = "AW",
                message = "Sounds great, see you at 6!",
                time = "1h ago"
            )
        }

        item {
            SimulatorChatItem(
                name = "Design Squad",
                initials = "DS",
                message = "Elena: Loved the new UI concept",
                time = "3h ago"
            )
        }
    }
}

@Composable
fun SimulatorChatItem(name: String, initials: String, message: String, time: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF141414))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color(0xFF333333)),
            contentAlignment = Alignment.Center
        ) {
            Text(initials, color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(time, color = Color.Gray, fontSize = 11.sp)
            }
            Text(message, color = Color.Gray, fontSize = 13.sp, maxLines = 1)
        }
    }
}

@Composable
fun SimulatorChatView(onPlayChatReel: () -> Unit) {
    var messageText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Chat Rule Verification Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(StatusActiveGreen.copy(alpha = 0.15f))
                .border(0.5.dp, StatusActiveGreen.copy(alpha = 0.5f))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = StatusActiveGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Direct Chat Active: Reels sent here are PERMITTED by ReelShield rule.",
                    fontSize = 11.sp,
                    color = StatusActiveGreen,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Today 10:42 AM", color = Color.Gray, fontSize = 11.sp)
                }
            }

            // Message from Sarah
            item {
                Row(verticalAlignment = Alignment.Bottom) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(ShieldPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("SJ", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF262626))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Hey! Check out this hilarious recipe reel I found 😂",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Embedded Reel Card in Chat!
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Spacer(modifier = Modifier.width(36.dp))

                    Card(
                        modifier = Modifier
                            .width(240.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onPlayChatReel() }
                            .border(1.5.dp, StatusActiveGreen, RoundedCornerShape(16.dp))
                            .testTag("chat_reel_card"),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E28))
                    ) {
                        Column {
                            // Reel Thumbnail with Play Button
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color(0xFF3F2B96), Color(0xFFE81CFF))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.6f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play Reel",
                                        tint = Color.White,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }

                                // Allowed Badge
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(StatusActiveGreen)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "ALLOWED IN CHAT",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }

                                Text(
                                    text = "0:24",
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(8.dp)
                                        .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }

                            // Reel Details
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "@chef_whiskers • Reels",
                                    fontSize = 11.sp,
                                    color = Color.LightGray
                                )
                                Text(
                                    text = "When the cat decides to bake bread 🐱🍞",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Tap to watch reel in chat ▶",
                                    fontSize = 11.sp,
                                    color = StatusActiveGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Reply from user
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(ShieldSecondary)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Haha that cat is a master chef!",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Chat Input Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF141414))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF262626))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = if (messageText.isEmpty()) "Message Sarah..." else messageText,
                    color = if (messageText.isEmpty()) Color.Gray else Color.White,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = ShieldPrimary
                )
            }
        }
    }
}

@Composable
fun SimulatorChatReelPlayerView(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Simulated video background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF180B3B), Color(0xFF6B1182), Color(0xFF0F0422))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircleFilled,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "🐱 Cute Chef Cat Reel Playing",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "0:14 / 0:24 • Sound: Original Audio",
                    fontSize = 13.sp,
                    color = Color.LightGray
                )
            }
        }

        // Top Status Header (Clear Proof of Permission)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .background(Color.Black.copy(alpha = 0.7f))
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("exit_chat_reel_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Chat",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "From chat with Sarah",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(StatusActiveGreen)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Rule: Allowed in Chat ✅",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "ReelShield identifies this video was opened from Direct Messages. Playing freely without blocking!",
                color = Color(0xFFB0FFD0),
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Return button at bottom
        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(containerColor = ShieldPrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp)
                .fillMaxWidth(0.8f)
        ) {
            Text("Return to Chat Conversation", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ShieldBlockInterceptionOverlay(
    title: String,
    subtitle: String
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shield_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "overlay_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.88f))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(2.dp, StatusBlockedRed, RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF140D1E))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .scale(scale)
                        .clip(CircleShape)
                        .background(StatusBlockedRed.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Blocked",
                        tint = StatusBlockedRed,
                        modifier = Modifier.size(54.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFD1D5DB),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF231633))
                        .border(1.dp, Color(0xFF3F275E), RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "⏱️ ", fontSize = 16.sp)
                        Text(
                            text = "+15 Minutes Saved from Doomscrolling!",
                            color = ShieldPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Auto-redirecting back to Feed...",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}
