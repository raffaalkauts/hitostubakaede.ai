package com.kaede.app.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Memory Entity - Long-term memory storage for Kaede
 * 
 * Stores important conversation events, user preferences,
 * and emotional moments for persistent character memory
 */
@Entity(
    tableName = "memories",
    indices = [
        Index(value = ["timestamp"], name = "index_memories_timestamp"),
        Index(value = ["importance_score"], name = "index_memories_importance"),
        Index(value = ["memory_type"], name = "index_memories_type"),
        Index(value = ["content", "memory_type"], name = "index_memories_search")
    ]
)
data class MemoryEntity(
    /**
     * Unique identifier (auto-generated)
     */
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /**
     * Memory content (the actual information stored)
     */
    @ColumnInfo(name = "content", typeAffinity = ColumnInfo.TEXT)
    val content: String,
    
    /**
     * Importance score (0-10)
     * Higher scores are retained longer and prioritized in prompts
     */
    @ColumnInfo(name = "importance_score", defaultValue = "0")
    val importanceScore: Int = 0,
    
    /**
     * Timestamp when memory was created (epoch milliseconds)
     */
    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis(),
    
    /**
     * Type of memory for categorization
     */
    @ColumnInfo(name = "memory_type", defaultValue = "CONVERSATION")
    val memoryType: MemoryType = MemoryType.CONVERSATION,
    
    /**
     * Associated mood when memory was created
     */
    @ColumnInfo(name = "associated_mood", defaultValue = "NORMAL")
    val associatedMood: String = "NORMAL",
    
    /**
     * Number of times this memory has been accessed
     */
    @ColumnInfo(name = "access_count", defaultValue = "0")
    val accessCount: Int = 0,
    
    /**
     * Whether this memory should be kept permanently
     */
    @ColumnInfo(name = "is_permanent", defaultValue = "0")
    val isPermanent: Boolean = false
)

/**
 * Memory types for categorization
 */
enum class MemoryType {
    /**
     * Regular conversation memory
     */
    CONVERSATION,
    
    /**
     * User preference or personal information
     */
    USER_INFO,
    
    /**
     * Emotional or significant event
     */
    EMOTIONAL,
    
    /**
     * Recurring topic or theme
     */
    RECURRING,
    
    /**
     * Fact learned about the user
     */
    FACT,
    
    /**
     * Shared experience or inside joke
     */
    SHARED
}

/**
 * Extension function to convert string to MemoryType
 */
fun String.toMemoryType(): MemoryType {
    return try {
        MemoryType.valueOf(this)
    } catch (e: IllegalArgumentException) {
        MemoryType.CONVERSATION
    }
}

/**
 * Extension function to convert MemoryType to string
 */
fun MemoryType.toString(): String {
    return this.name
}
