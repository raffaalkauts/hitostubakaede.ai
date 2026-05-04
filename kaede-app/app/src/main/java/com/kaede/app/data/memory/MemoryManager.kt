package com.kaede.app.data.memory

import android.util.Log
import com.kaede.app.data.db.MemoryType
import com.kaede.app.data.db.MemoryEntity
import com.kaede.app.ai.Mood

/**
 * Memory Manager - Handles short-term and long-term memory coordination
 * 
 * Manages the conversation buffer (short-term) and coordinates
 * with MemoryRepository for long-term storage
 */
class MemoryManager {
    
    companion object {
        private const val TAG = "MemoryManager"
        
        // Maximum messages in short-term buffer
        private const val MAX_BUFFER_SIZE = 10
        
        // Importance threshold for automatic long-term storage
        private const val AUTO_SAVE_THRESHOLD = 7
    }
    
    // Short-term memory buffer (last N messages)
    private val conversationBuffer = mutableListOf<ConversationTurn>()
    
    // Session statistics
    private var sessionMessageCount = 0
    private var sessionStartTime = System.currentTimeMillis()
    
    /**
     * Add message to conversation buffer
     * 
     * @param message Message content
     * @param isUser Whether this is a user message
     * @param mood Current mood when message was sent
     */
    fun addToBuffer(message: String, isUser: Boolean = true, mood: Mood = Mood.NORMAL) {
        val turn = ConversationTurn(
            content = message,
            isUser = isUser,
            mood = mood,
            timestamp = System.currentTimeMillis()
        )
        
        if (conversationBuffer.size >= MAX_BUFFER_SIZE) {
            val removed = conversationBuffer.removeAt(0)
            Log.d(TAG, "Removed old message from buffer: ${removed.content.take(30)}...")
        }
        
        conversationBuffer.add(turn)
        sessionMessageCount++
        
        Log.d(TAG, "Added to buffer (${conversationBuffer.size}/$MAX_BUFFER_SIZE): ${message.take(30)}...")
    }
    
    /**
     * Get conversation history formatted for LLM prompt
     */
    fun getConversationHistory(): List<String> {
        return conversationBuffer.map { turn ->
            if (turn.isUser) {
                "User: ${turn.content}"
            } else {
                "Kaede: ${turn.content}"
            }
        }
    }
    
    /**
     * Get raw conversation turns
     */
    fun getConversationTurns(): List<ConversationTurn> {
        return conversationBuffer.toList()
    }
    
    /**
     * Get last N turns from buffer
     */
    fun getLastTurns(count: Int): List<ConversationTurn> {
        return conversationBuffer.takeLast(count)
    }
    
    /**
     * Get last user message
     */
    fun getLastUserMessage(): String? {
        return conversationBuffer.lastOrNull { it.isUser }?.content
    }
    
    /**
     * Get last Kaede response
     */
    fun getLastKaedeResponse(): String? {
        return conversationBuffer.lastOrNull { !it.isUser }?.content
    }
    
    /**
     * Check if buffer contains specific topic
     */
    fun containsTopic(topic: String): Boolean {
        return conversationBuffer.any { 
            it.content.contains(topic, ignoreCase = true) 
        }
    }
    
    /**
     * Get messages mentioning specific topic
     */
    fun getMessagesAboutTopic(topic: String): List<ConversationTurn> {
        return conversationBuffer.filter {
            it.content.contains(topic, ignoreCase = true)
        }
    }
    
    /**
     * Clear conversation buffer
     */
    fun clearBuffer() {
        Log.d(TAG, "Clearing conversation buffer")
        conversationBuffer.clear()
        sessionMessageCount = 0
        sessionStartTime = System.currentTimeMillis()
    }
    
    /**
     * Get session statistics
     */
    fun getSessionStats(): SessionStats {
        return SessionStats(
            messageCount = sessionMessageCount,
            bufferCount = conversationBuffer.size,
            sessionDurationMs = System.currentTimeMillis() - sessionStartTime,
            startTime = sessionStartTime
        )
    }
    
    /**
     * Determine if a message should be saved to long-term memory
     * 
     * @param message Message content
     * @param importanceScore Pre-calculated importance score
     * @return Whether to save to long-term memory
     */
    fun shouldSaveToLongTerm(message: String, importanceScore: Int): Boolean {
        // Always save high-importance messages
        if (importanceScore >= AUTO_SAVE_THRESHOLD) {
            return true
        }
        
        // Save if message contains personal information keywords
        val personalKeywords = listOf(
            "my name is", "call me", "i am", "i'm", "i live", "i work",
            "i study", "i like", "i love", "i hate", "my favorite"
        )
        
        val lowerMessage = message.lowercase()
        return personalKeywords.any { keyword ->
            lowerMessage.contains(keyword)
        }
    }
    
    /**
     * Create MemoryEntity from conversation turn
     */
    fun createMemoryFromTurn(
        turn: ConversationTurn,
        importanceScore: Int,
        type: MemoryType = MemoryType.CONVERSATION
    ): MemoryEntity {
        return MemoryEntity(
            content = turn.content,
            importanceScore = importanceScore,
            memoryType = type,
            associatedMood = turn.mood.name,
            timestamp = turn.timestamp
        )
    }
    
    /**
     * Reset session (new conversation session)
     */
    fun resetSession() {
        Log.d(TAG, "Resetting session")
        clearBuffer()
        sessionStartTime = System.currentTimeMillis()
    }
}

/**
 * Represents a single conversation turn
 */
data class ConversationTurn(
    val content: String,
    val isUser: Boolean,
    val mood: Mood = Mood.NORMAL,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Session statistics data class
 */
data class SessionStats(
    val messageCount: Int,
    val bufferCount: Int,
    val sessionDurationMs: Long,
    val startTime: Long
) {
    /**
     * Get formatted session duration
     */
    fun getFormattedDuration(): String {
        val minutes = sessionDurationMs / (60 * 1000)
        val seconds = (sessionDurationMs % (60 * 1000)) / 1000
        return "${minutes}m ${seconds}s"
    }
}
