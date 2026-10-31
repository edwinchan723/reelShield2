package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "block_events")
data class BlockEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val eventType: String, // "REELS_TAB_BLOCKED", "REELS_EXPLORE_BLOCKED", "CHAT_REEL_PERMITTED"
    val source: String, // "Instagram App" or "Simulator"
    val estimatedSecondsSaved: Int = 900, // 15 mins by default for a blocked doomscroll session
    val details: String = ""
)
