package com.kaede.app.data.memory

import android.util.Log
import com.kaede.app.data.db.MemoryDao
import com.kaede.app.data.db.MemoryEntity
import com.kaede.app.data.db.MemoryType
import com.kaede.app.data.db.UserPreferencesDao
import com.kaede.app.data.db.UserPreferencesEntity
import com.kaede.app.data.db.PreferenceKeys
import kotlinx.coroutines.flow.Flow

/**
 * Memory Repository - Mediates between data sources
 * 
 * Provides high-level API for memory operations,
 * coordinating between MemoryDao and business logic
 */
class MemoryRepository(
    private val memoryDao: MemoryDao,
    private val preferencesDao: UserPreferencesDao
) {
    
    companion object {
        private const val TAG = "MemoryRepository"
        
        // Minimum score for memory to be considered "important"
        private const val MIN_IMPORTANT_SCORE = 5
        
        // Maximum memories to return for context
        private const val MAX_CONTEXT_MEMORIES = 5
    }
    
    // ==================== MEMORY OPERATIONS ====================
    
    /**
     * Get all memories as Flow (reactive stream)
     */
    val allMemories: Flow<List<MemoryEntity>> = memoryDao.getAllMemories()
    
    /**
     * Get important memories for prompt context
     */
    suspend fun getImportantMemories(limit: Int = MAX_CONTEXT_MEMORIES): List<MemoryEntity> {
        return memoryDao.getImportantMemories(
            minScore = MIN_IMPORTANT_SCORE,
            limit = limit
        )
    }
    
    /**
     * Get memories for LLM context (weighted by importance and recency)
     */
    suspend fun getContextMemories(limit: Int = MAX_CONTEXT_MEMORIES): List<MemoryEntity> {
        return memoryDao.getContextMemories(
            currentTime = System.currentTimeMillis(),
            limit = limit
        )
    }
    
    /**
     * Search memories by query
     */
    suspend fun searchMemories(query: String): List<MemoryEntity> {
        return memoryDao.searchMemories(query)
    }
    
    /**
     * Get memories by type
     */
    suspend fun getMemoriesByType(type: MemoryType): List<MemoryEntity> {
        return memoryDao.getMemoriesByType(type)
    }
    
    /**
     * Get recent memories (last 24 hours)
     */
    suspend fun getRecentMemories(): List<MemoryEntity> {
        val oneDayAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
        return memoryDao.getRecentMemories(oneDayAgo)
    }
    
    /**
     * Get permanent memories
     */
    suspend fun getPermanentMemories(): List<MemoryEntity> {
        return memoryDao.getPermanentMemories()
    }
    
    /**
     * Save new memory
     * 
     * @param content Memory content
     * @param importanceScore Importance score (0-10)
     * @param type Memory type
     * @param associatedMood Mood when memory was created
     * @return ID of saved memory
     */
    suspend fun saveMemory(
        content: String,
        importanceScore: Int = 0,
        type: MemoryType = MemoryType.CONVERSATION,
        associatedMood: String = "NORMAL"
    ): Long {
        Log.d(TAG, "Saving memory: $content (importance: $importanceScore, type: $type)")
        
        val memory = MemoryEntity(
            content = content,
            importanceScore = importanceScore.coerceIn(0, 10),
            memoryType = type,
            associatedMood = associatedMood
        )
        
        return memoryDao.insertMemory(memory)
    }
    
    /**
     * Save multiple memories
     */
    suspend fun saveMemories(memories: List<MemoryEntity>) {
        Log.d(TAG, "Saving ${memories.size} memories")
        memoryDao.insertMemories(memories)
    }
    
    /**
     * Update existing memory
     */
    suspend fun updateMemory(memory: MemoryEntity) {
        memoryDao.updateMemory(memory)
    }
    
    /**
     * Delete memory
     */
    suspend fun deleteMemory(memory: MemoryEntity) {
        memoryDao.deleteMemory(memory)
    }
    
    /**
     * Delete memory by ID
     */
    suspend fun deleteMemoryById(id: Long) {
        memoryDao.deleteMemoryById(id)
    }
    
    /**
     * Mark memory as permanent
     */
    suspend fun markMemoryAsPermanent(id: Long) {
        memoryDao.markAsPermanent(id)
    }
    
    /**
     * Increment access count for memory
     */
    suspend fun incrementAccessCount(id: Long) {
        memoryDao.incrementAccessCount(id)
    }
    
    /**
     * Clear all non-permanent memories
     */
    suspend fun clearAllMemories() {
        Log.w(TAG, "Clearing all non-permanent memories")
        memoryDao.deleteAllNonPermanent()
    }
    
    /**
     * Get total memory count
     */
    suspend fun getMemoryCount(): Int {
        return memoryDao.getMemoryCount()
    }
    
    // ==================== PREFERENCE OPERATIONS ====================
    
    /**
     * Get user preference value
     */
    suspend fun getPreference(key: String): String? {
        return preferencesDao.getPreferenceValue(key)
    }
    
    /**
     * Get user preference with default value
     */
    suspend fun getPreferenceOrDefault(key: String, default: String): String {
        return preferencesDao.getPreferenceValue(key) ?: default
    }
    
    /**
     * Set user preference
     */
    suspend fun setPreference(key: String, value: String) {
        Log.d(TAG, "Setting preference: $key = $value")
        val entity = UserPreferencesEntity(
            key = key,
            value = value
        )
        preferencesDao.insertPreference(entity)
    }
    
    /**
     * Get user name
     */
    suspend fun getUserName(): String? {
        return getPreference(PreferenceKeys.USER_NAME)
    }
    
    /**
     * Set user name
     */
    suspend fun setUserName(name: String) {
        setPreference(PreferenceKeys.USER_NAME, name)
    }
    
    /**
     * Get conversation count
     */
    suspend fun getConversationCount(): Int {
        val count = getPreference(PreferenceKeys.CONVERSATION_COUNT) ?: "0"
        return count.toIntOrNull() ?: 0
    }
    
    /**
     * Increment conversation count
     */
    suspend fun incrementConversationCount() {
        val count = getConversationCount()
        setPreference(PreferenceKeys.CONVERSATION_COUNT, (count + 1).toString())
    }
    
    /**
     * Update last active timestamp
     */
    suspend fun updateLastActive() {
        setPreference(PreferenceKeys.LAST_ACTIVE, System.currentTimeMillis().toString())
    }
    
    // ==================== MEMORY MANAGEMENT ====================
    
    /**
     * Clean up old low-importance memories
     * Called periodically to manage storage
     */
    suspend fun cleanupOldMemories() {
        Log.d(TAG, "Cleaning up old memories")
        
        val count = getMemoryCount()
        
        // If more than 1000 memories, delete oldest 100
        if (count > 1000) {
            memoryDao.deleteLowImportanceMemories(minScore = 2, count = 100)
            Log.d(TAG, "Deleted 100 low-importance memories")
        }
        
        // Delete memories older than 30 days (except permanent)
        val thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)
        memoryDao.deleteOldMemories(thirtyDaysAgo)
        Log.d(TAG, "Deleted memories older than 30 days")
    }
    
    /**
     * Export all memories to JSON (for backup)
     */
    suspend fun exportMemoriesToJson(): String {
        val memories = memoryDao.getAllMemoriesNonFlow()
        // Simple JSON export (can be enhanced with proper JSON library)
        return buildString {
            append("[")
            var first = true
            for (memory in memories) {
                if (!first) append(",")
                first = false
                append("""{"id":${memory.id},"content":"${escapeJson(memory.content)}","importance":${memory.importanceScore},"type":"${memory.memoryType}"}""")
            }
            append("]")
        }
    }
    
    /**
     * Escape special characters for JSON
     */
    private fun escapeJson(text: String): String {
        return text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }
}
