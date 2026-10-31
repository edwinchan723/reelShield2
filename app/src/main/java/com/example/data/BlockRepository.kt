package com.example.data

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class BlockRepository(
    private val blockEventDao: BlockEventDao,
    private val preferencesManager: PreferencesManager
) {
    val settingsFlow: StateFlow<ShieldSettings> = preferencesManager.settingsFlow
    val recentEvents: Flow<List<BlockEvent>> = blockEventDao.getRecentEvents()
    val totalBlocksCount: Flow<Int> = blockEventDao.getTotalBlocksCount()
    val totalSecondsSaved: Flow<Int?> = blockEventDao.getTotalSecondsSaved()

    fun getTodayBlocksCount(): Flow<Int> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return blockEventDao.getBlocksSince(calendar.timeInMillis)
    }

    suspend fun recordBlock(
        eventType: String,
        source: String,
        details: String,
        savedSeconds: Int = 900
    ): Long {
        val event = BlockEvent(
            eventType = eventType,
            source = source,
            details = details,
            estimatedSecondsSaved = savedSeconds
        )
        return blockEventDao.insert(event)
    }

    fun recordBlockAsync(
        scope: CoroutineScope,
        eventType: String,
        source: String,
        details: String,
        savedSeconds: Int = 900
    ) {
        scope.launch(Dispatchers.IO) {
            recordBlock(eventType, source, details, savedSeconds)
        }
    }

    suspend fun clearHistory() {
        blockEventDao.clearAll()
    }

    fun getPreferences(): PreferencesManager = preferencesManager

    companion object {
        @Volatile
        private var INSTANCE: BlockRepository? = null

        fun getInstance(context: Context): BlockRepository {
            return INSTANCE ?: synchronized(this) {
                val db = AppDatabase.getInstance(context)
                val prefs = PreferencesManager.getInstance(context)
                val instance = BlockRepository(db.blockEventDao(), prefs)
                INSTANCE = instance
                instance
            }
        }
    }
}
