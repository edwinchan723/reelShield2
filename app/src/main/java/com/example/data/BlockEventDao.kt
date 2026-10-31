package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockEventDao {
    @Insert
    suspend fun insert(event: BlockEvent): Long

    @Query("SELECT * FROM block_events ORDER BY timestamp DESC LIMIT 50")
    fun getRecentEvents(): Flow<List<BlockEvent>>

    @Query("SELECT COUNT(*) FROM block_events WHERE eventType != 'CHAT_REEL_PERMITTED'")
    fun getTotalBlocksCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM block_events WHERE eventType != 'CHAT_REEL_PERMITTED' AND timestamp >= :sinceTimestamp")
    fun getBlocksSince(sinceTimestamp: Long): Flow<Int>

    @Query("SELECT SUM(estimatedSecondsSaved) FROM block_events WHERE eventType != 'CHAT_REEL_PERMITTED'")
    fun getTotalSecondsSaved(): Flow<Int?>

    @Query("DELETE FROM block_events")
    suspend fun clearAll()
}
