package com.kaede.app.ai

import android.util.Log

/**
 * Mood System - Emotional state management for Kaede
 * 
 * Tracks and updates Kaede's emotional state based on
 * user input and conversation context
 */
enum class Mood {
    HAPPY,      // Positive emotions, praise, compliments
    PLAYFUL,    // Teasing, jokes, lighthearted conversation
    JEALOUS,    // Mentions of others, potential rivals
    SHY,        // Embarrassing topics, compliments about appearance
    NORMAL      // Default neutral state
}

/**
 * Personality Manager - Manages Kaede's emotional state
 * 
 * Analyzes user messages for emotional triggers and
 * updates mood accordingly with decay over time
 */
class PersonalityManager {
    
    companion object {
        private const val TAG = "PersonalityManager"
        
        // Mood decay time in milliseconds (5 minutes)
        private const val MOOD_DECAY_TIME_MS = 5 * 60 * 1000L
        
        // Mood trigger keywords
        private val HAPPY_TRIGGERS = setOf(
            "love", "like", "amazing", "wonderful", "great", "awesome",
            "beautiful", "cute", "sweet", "kind", "smart", "clever",
            "thank", "appreciate", "adore", "miss", "happy", "glad",
            "excited", "joy", "smile", "laugh", "fun", "enjoy"
        )
        
        private val PLAYFUL_TRIGGERS = setOf(
            "tease", "joke", "play", "funny", "silly", "weird",
            "challenge", "bet", "dare", "game", "trick", "prank",
            "haha", "lol", "lmao", "jk", "just kidding"
        )
        
        private val JEALOUS_TRIGGERS = setOf(
            "girl", "boy", "friend", "date", "meeting", "other",
            "someone else", "somebody", "they", "them", "her", "him",
            "together", "hang out", "spend time", "talk to"
        )
        
        private val SHY_TRIGGERS = setOf(
            "blush", "embarrass", "shy", "nervous", "awkward",
            "appearance", "look", "pretty", "handsome", "attractive",
            "body", "face", "eyes", "hair", "clothes", "outfit"
        )
    }
    
    private var currentMood: Mood = Mood.NORMAL
    private var lastMoodChangeTime: Long = System.currentTimeMillis()
    private var moodIntensity: Float = 0f
    
    /**
     * Analyze message for mood triggers and update emotional state
     */
    fun updateMood(userMessage: String) {
        val lowerMessage = userMessage.lowercase()
        
        // Count triggers for each mood
        val happyScore = countTriggers(lowerMessage, HAPPY_TRIGGERS)
        val playfulScore = countTriggers(lowerMessage, PLAYFUL_TRIGGERS)
        val jealousScore = countTriggers(lowerMessage, JEALOUS_TRIGGERS)
        val shyScore = countTriggers(lowerMessage, SHY_TRIGGERS)
        
        // Find highest scoring mood
        val scores = mapOf(
            Mood.HAPPY to happyScore,
            Mood.PLAYFUL to playfulScore,
            Mood.JEALOUS to jealousScore,
            Mood.SHY to shyScore,
            Mood.NORMAL to 0
        )
        
        val maxScore = scores.values.maxOrNull() ?: 0
        
        if (maxScore > 0) {
            // Determine new mood based on highest score
            val newMood = scores.entries.find { it.value == maxScore }?.key ?: Mood.NORMAL
            
            if (newMood != currentMood) {
                Log.d(TAG, "Mood changed: ${currentMood} → $newMood (score: $maxScore)")
                currentMood = newMood
                lastMoodChangeTime = System.currentTimeMillis()
                moodIntensity = minOf(maxScore.toFloat() / 5f, 1f)
            }
        }
    }
    
    /**
     * Get current mood state
     */
    fun getCurrentMood(): Mood {
        // Check for mood decay
        if (currentMood != Mood.NORMAL) {
            val timeSinceChange = System.currentTimeMillis() - lastMoodChangeTime
            
            if (timeSinceChange > MOOD_DECAY_TIME_MS) {
                Log.d(TAG, "Mood decayed back to NORMAL")
                currentMood = Mood.NORMAL
                moodIntensity = 0f
            }
        }
        
        return currentMood
    }
    
    /**
     * Get current mood intensity (0.0 to 1.0)
     */
    fun getMoodIntensity(): Float {
        return moodIntensity
    }
    
    /**
     * Force set mood (for testing or special events)
     */
    fun setMood(mood: Mood, intensity: Float = 0.5f) {
        Log.d(TAG, "Mood forced: $mood (intensity: $intensity)")
        currentMood = mood
        moodIntensity = intensity.coerceIn(0f, 1f)
        lastMoodChangeTime = System.currentTimeMillis()
    }
    
    /**
     * Reset mood to normal
     */
    fun resetMood() {
        Log.d(TAG, "Mood reset to NORMAL")
        currentMood = Mood.NORMAL
        moodIntensity = 0f
    }
    
    /**
     * Count how many trigger words appear in message
     */
    private fun countTriggers(message: String, triggers: Set<String>): Int {
        var count = 0
        triggers.forEach { trigger ->
            if (message.contains(trigger)) {
                count++
            }
        }
        return count
    }
    
    /**
     * Get mood description for debugging
     */
    fun getMoodDescription(): String {
        return "Mood: $currentMood, Intensity: ${String.format("%.2f", moodIntensity)}"
    }
}
