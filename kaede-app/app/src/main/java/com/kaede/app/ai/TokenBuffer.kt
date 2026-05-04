package com.kaede.app.ai

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.delay

/**
 * Token Buffer - Smooths out token streaming for better visual experience
 * 
 * Buffers and releases tokens at a consistent rate to avoid
 * choppy text display during AI generation
 */
class TokenBuffer {
    
    companion object {
        // Target tokens per second for smooth display
        private const val TARGET_TPS = 30
        
        // Minimum buffer size before starting display
        private const val MIN_BUFFER_SIZE = 3
        
        // Maximum buffer size
        private const val MAX_BUFFER_SIZE = 10
    }
    
    private val buffer = StringBuilder()
    private var isComplete = false
    
    /**
     * Add token to buffer
     */
    fun append(token: String) {
        synchronized(buffer) {
            buffer.append(token)
        }
    }
    
    /**
     * Mark streaming as complete
     */
    fun complete() {
        isComplete = true
    }
    
    /**
     * Get current buffered text
     */
    fun getText(): String {
        synchronized(buffer) {
            return buffer.toString()
        }
    }
    
    /**
     * Clear buffer
     */
    fun clear() {
        synchronized(buffer) {
            buffer.clear()
        }
        isComplete = false
    }
    
    /**
     * Check if buffer is empty
     */
    fun isEmpty(): Boolean {
        synchronized(buffer) {
            return buffer.isEmpty()
        }
    }
    
    /**
     * Check if streaming is complete
     */
    fun isComplete(): Boolean {
        return isComplete
    }
}

/**
 * Create a smoothed flow from token flow
 * 
 * @param targetDelayMs Target delay between token emissions
 * @return Flow of smoothed text updates
 */
fun Flow<String>.smoothBuffer(
    targetDelayMs: Long = 50
): Flow<String> = channelFlow {
    val buffer = StringBuilder()
    var lastEmitTime = System.currentTimeMillis()
    
    collect { token ->
        buffer.append(token)
        val now = System.currentTimeMillis()
        
        // Emit if enough time has passed or buffer is large
        if (now - lastEmitTime >= targetDelayMs || buffer.length >= 20) {
            send(buffer.toString())
            buffer.clear()
            lastEmitTime = now
        }
    }
    
    // Send remaining buffer
    if (buffer.isNotEmpty()) {
        send(buffer.toString())
    }
}

/**
 * Debounce token flow to avoid too frequent updates
 * 
 * @param delayMs Minimum delay between emissions
 * @return Flow of debounced text
 */
fun Flow<String>.debounceTokens(
    delayMs: Long = 30
): Flow<String> = channelFlow {
    var lastText = ""
    var lastEmitTime = 0L
    
    collect { token ->
        lastText += token
        val now = System.currentTimeMillis()
        
        if (now - lastEmitTime >= delayMs) {
            send(lastText)
            lastEmitTime = now
        }
    }
    
    // Send final text
    if (lastText.isNotEmpty()) {
        send(lastText)
    }
}
