package com.kaede.app.ai

import android.util.Log

/**
 * Query Classifier - Detects query complexity for performance optimization
 */
object QueryClassifier {
    
    private const val TAG = "QueryClassifier"
    
    // Complexity keywords
    private val COMPLEX_KEYWORDS = setOf(
        "explain", "how does", "why", "what is", "describe", "analyze",
        "compare", "difference between", "calculate", "solve",
        "code", "program", "algorithm", "function", "debug",
        "summarize", "list all", "every", "complete guide",
        "step by step", "tutorial", "how to make", "create"
    )
    
    private val GREETING_KEYWORDS = setOf(
        "hi", "hello", "hey", "good morning", "good afternoon",
        "good evening", "how are you", "what's up", "yo",
        "halo", "hai", "selamat pagi", "selamat siang", "selamat malam"
    )
    
    private val EMOTIONAL_KEYWORDS = setOf(
        "love", "miss", "happy", "sad", "angry", "excited",
        "tired", "stressed", "worried", "afraid", "hope",
        "dream", "wish", "feel", "emotion", "heart"
    )
    
    /**
     * Classify query and return appropriate complexity tier
     */
    fun classify(query: String): QueryComplexity {
        val lowerQuery = query.lowercase().trim()
        val wordCount = lowerQuery.split(" ").size
        
        // Check for greetings first (most common, minimal processing)
        if (GREETING_KEYWORDS.any { lowerQuery.contains(it) }) {
            Log.d(TAG, "Detected: Greeting → MINIMAL tier")
            return QueryComplexity.MINIMAL
        }
        
        // Check for complex queries
        val hasComplexKeywords = COMPLEX_KEYWORDS.any { lowerQuery.contains(it) }
        
        if (hasComplexKeywords || wordCount > 20) {
            Log.d(TAG, "Detected: Complex query → HIGH tier")
            return QueryComplexity.HIGH
        }
        
        // Check for emotional content (needs careful response)
        if (EMOTIONAL_KEYWORDS.any { lowerQuery.contains(it) }) {
            Log.d(TAG, "Detected: Emotional → NORMAL tier")
            return QueryComplexity.NORMAL
        }
        
        // Default: simple conversation
        Log.d(TAG, "Detected: Simple conversation → NORMAL tier")
        return QueryComplexity.NORMAL
    }
    
    /**
     * Get optimal generation params based on complexity
     */
    fun getOptimalParams(complexity: QueryComplexity): MlcLlm.GenerationParams {
        return when (complexity) {
            QueryComplexity.MINIMAL -> MlcLlm.GenerationParams(
                maxTokens = 64,
                temperature = 0.9f,
                topP = 0.95f,
                repetitionPenalty = 1.0f
            )
            
            QueryComplexity.NORMAL -> MlcLlm.GenerationParams(
                maxTokens = 128,
                temperature = 0.8f,
                topP = 0.9f,
                repetitionPenalty = 1.0f
            )
            
            QueryComplexity.HIGH -> MlcLlm.GenerationParams(
                maxTokens = 256,
                temperature = 0.7f,
                topP = 0.85f,
                repetitionPenalty = 1.1f
            )
        }
    }
}

/**
 * Query complexity levels
 */
enum class QueryComplexity {
    MINIMAL,  // Greetings, simple acknowledgments
    NORMAL,   // Regular conversation, emotional support
    HIGH      // Complex questions, explanations, analysis
}
