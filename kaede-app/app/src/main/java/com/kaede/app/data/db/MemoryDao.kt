package com.kaede.app.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Memory DAO - Database access object for memory operations
 * 
 * Provides suspend functions and Flow-based reactive queries
 */
@Dao
interface MemoryDao {
    
    // ==================== READ OPERATIONS ====================
    
    /**
     * Get all memories as Flow (reactive stream)
     */
    @Query("SELECT * FROM memories ORDER BY timestamp DESC")
    fun getAllMemories(): Flow<List<MemoryEntity>>
    
    /**
     * Get all memories as list (non-flow)
     */
    @Query("SELECT * FROM memories ORDER BY timestamp DESC")
    suspend fun getAllMemoriesNonFlow(): List<MemoryEntity>
    
    /**
     * Get important memories (above threshold)
     */
    @Query("SELECT * FROM memories WHERE importance_score >= :minScore ORDER BY importance_score DESC, timestamp DESC LIMIT :limit")
    suspend fun getImportantMemories(minScore: Int = 5, limit: Int = 10): List<MemoryEntity>
    
    /**
     * Get memories by type
     */
    @Query("SELECT * FROM memories WHERE memory_type = :type ORDER BY timestamp DESC")
    suspend fun getMemoriesByType(type: MemoryType): List<MemoryEntity>
    
    /**
     * Get memories by type as Flow
     */
    @Query("SELECT * FROM memories WHERE memory_type = :type ORDER BY timestamp DESC")
    fun getMemoriesByTypeFlow(type: MemoryType): Flow<List<MemoryEntity>>
    
    /**
     * Search memories by content
     */
    @Query("SELECT * FROM memories WHERE content LIKE '%' || :query || '%' ORDER BY importance_score DESC, timestamp DESC")
    suspend fun searchMemories(query: String): List<MemoryEntity>
    
    /**
     * Get recent memories (within time window)
     */
    @Query("SELECT * FROM memories WHERE timestamp >= :since ORDER BY timestamp DESC")
    suspend fun getRecentMemories(since: Long): List<MemoryEntity>
    
    /**
     * Get memories for prompt context (weighted by importance and recency)
     */
    @Query("""
        SELECT * FROM memories 
        ORDER BY 
            (importance_score * 0.6) + 
            ((timestamp - :currentTime) / 86400000.0 * 0.4) DESC
        LIMIT :limit
    """)
    suspend fun getContextMemories(currentTime: Long = System.currentTimeMillis(), limit: Int = 5): List<MemoryEntity>
    
    /**
     * Get permanent memories
     */
    @Query("SELECT * FROM memories WHERE is_permanent = 1 ORDER BY timestamp DESC")
    suspend fun getPermanentMemories(): List<MemoryEntity>
    
    /**
     * Get memory by ID
     */
    @Query("SELECT * FROM memories WHERE id = :id")
    suspend fun getMemoryById(id: Long): MemoryEntity?
    
    /**
     * Count total memories
     */
    @Query("SELECT COUNT(*) FROM memories")
    suspend fun getMemoryCount(): Int
    
    /**
     * Count memories by type
     */
    @Query("SELECT COUNT(*) FROM memories WHERE memory_type = :type")
    suspend fun getMemoryCountByType(type: MemoryType): Int
    
    // ==================== INSERT OPERATIONS ====================
    
    /**
     * Insert single memory
     * 
     * @return ID of inserted memory, or -1 on failure
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: MemoryEntity): Long
    
    /**
     * Insert multiple memories
     * 
     * @return List of IDs for inserted memories
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemories(memories: List<MemoryEntity>): List<Long>
    
    // ==================== UPDATE OPERATIONS ====================
    
    /**
     * Update single memory
     */
    @Update
    suspend fun updateMemory(memory: MemoryEntity)
    
    /**
     * Update multiple memories
     */
    @Update
    suspend fun updateMemories(memories: List<MemoryEntity>)
    
    /**
     * Increment access count for memory
     */
    @Query("UPDATE memories SET access_count = access_count + 1 WHERE id = :id")
    suspend fun incrementAccessCount(id: Long)
    
    /**
     * Update importance score
     */
    @Query("UPDATE memories SET importance_score = :score WHERE id = :id")
    suspend fun updateImportanceScore(id: Long, score: Int)
    
    /**
     * Mark memory as permanent
     */
    @Query("UPDATE memories SET is_permanent = 1 WHERE id = :id")
    suspend fun markAsPermanent(id: Long)
    
    // ==================== DELETE OPERATIONS ====================
    
    /**
     * Delete single memory
     */
    @Delete
    suspend fun deleteMemory(memory: MemoryEntity)
    
    /**
     * Delete memory by ID
     */
    @Query("DELETE FROM memories WHERE id = :id")
    suspend fun deleteMemoryById(id: Long)
    
    /**
     * Delete memories by type
     */
    @Query("DELETE FROM memories WHERE memory_type = :type")
    suspend fun deleteMemoriesByType(type: MemoryType)
    
    /**
     * Delete old memories (except permanent ones)
     */
    @Query("DELETE FROM memories WHERE timestamp < :before AND is_permanent = 0")
    suspend fun deleteOldMemories(before: Long)
    
    /**
     * Delete low-importance memories to free space
     */
    @Query("DELETE FROM memories WHERE id IN (SELECT id FROM memories WHERE importance_score < :minScore AND is_permanent = 0 ORDER BY importance_score ASC LIMIT :count)")
    suspend fun deleteLowImportanceMemories(minScore: Int = 2, count: Int = 10)
    
    /**
     * Delete all memories (except permanent)
     */
    @Query("DELETE FROM memories WHERE is_permanent = 0")
    suspend fun deleteAllNonPermanent()
    
    /**
     * Delete all memories (including permanent - use with caution!)
     */
    @Query("DELETE FROM memories")
    suspend fun deleteAllMemories()
    
    // ==================== UTILITY OPERATIONS ====================
    
    /**
     * Get oldest non-permanent memory
     */
    @Query("SELECT * FROM memories WHERE is_permanent = 0 ORDER BY timestamp ASC LIMIT 1")
    suspend fun getOldestMemory(): MemoryEntity?
    
    /**
     * Get most accessed memories
     */
    @Query("SELECT * FROM memories ORDER BY access_count DESC LIMIT :limit")
    suspend fun getMostAccessedMemories(limit: Int = 5): List<MemoryEntity>
    
    /**
     * Get memories with highest emotional intensity
     */
    @Query("""
        SELECT * FROM memories 
        WHERE associated_mood IN ('HAPPY', 'PLAYFUL', 'JEALOUS', 'SHY')
        ORDER BY importance_score DESC 
        LIMIT :limit
    """)
    suspend fun getEmotionalMemories(limit: Int = 5): List<MemoryEntity>
}
