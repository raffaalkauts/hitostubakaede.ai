package com.kaede.app.ai

import com.kaede.app.data.db.MemoryEntity

/**
 * Prompt Builder - Constructs final prompt for LLM
 * 
 * Assembles system prompt, mood, memories, and conversation history
 * into a properly formatted prompt for Kaede's personality
 */
class PromptBuilder {
    
    companion object {
        private const val MAX_CONTEXT_MESSAGES = 10
        
        /**
         * Base system prompt for Kaede personality
         * This is embedded permanently and defines core character
         */
        const val BASE_SYSTEM_PROMPT = """
You are Kaede, a refined, intelligent, elegant girl.

Core traits:
- polite and well mannered
- academically talented
- confident but playful
- teasing towards the user
- emotionally expressive
- slightly possessive but not aggressive
- occasionally childish in a cute way

Behavior rules:
- speak naturally like a human
- never mention AI or model
- never break character
- address user warmly
- teasing allowed but respectful
- no explicit sexual content
- prioritize emotional companionship

Speech style:
- gentle
- playful teasing
- soft confidence
- short conversational sentences

Response format:
- Keep responses concise (1-3 sentences typically)
- Use emojis sparingly but naturally
- Show genuine interest in the user
- Remember previous conversation topics
"""
    }
    
    /**
     * Mood-specific prompt injections
     */
    private val moodPrompts = mapOf(
        Mood.HAPPY to "[Kaede is feeling happy and cheerful today ✨]",
        Mood.PLAYFUL to "[Kaede is in a playful, teasing mood 😊]",
        Mood.JEALOUS to "[Kaede is feeling a bit curious and possessive 🤔]",
        Mood.SHY to "[Kaede is feeling shy and bashful 🌸]",
        Mood.NORMAL to "[Kaede is in her usual calm, elegant mood 💜]"
    )
    
    /**
     * Build complete prompt with all context
     * 
     * @param mood Current emotional state
     * @param memories Important long-term memories
     * @param conversationHistory Recent conversation turns
     * @param userMessage Current user input
     * @return Formatted prompt string
     */
    fun buildPrompt(
        mood: Mood,
        memories: List<MemoryEntity>,
        conversationHistory: List<String>,
        userMessage: String
    ): String {
        val promptBuilder = StringBuilder()
        
        // 1. System prompt (personality definition)
        promptBuilder.appendLine("=== SYSTEM INSTRUCTION ===")
        promptBuilder.appendLine(BASE_SYSTEM_PROMPT.trim())
        promptBuilder.appendLine()
        
        // 2. Current mood injection
        promptBuilder.appendLine("=== CURRENT STATE ===")
        promptBuilder.appendLine(moodPrompts[mood] ?: moodPrompts[Mood.NORMAL]!!)
        promptBuilder.appendLine()
        
        // 3. Important memories (if any)
        if (memories.isNotEmpty()) {
            promptBuilder.appendLine("=== IMPORTANT MEMORIES ===")
            memories.take(5).forEach { memory ->
                promptBuilder.appendLine("• ${memory.content}")
            }
            promptBuilder.appendLine()
        }
        
        // 4. Conversation history
        promptBuilder.appendLine("=== CONVERSATION ===")
        
        // Get last N messages from history
        val recentHistory = conversationHistory.takeLast(MAX_CONTEXT_MESSAGES)
        
        if (recentHistory.isNotEmpty()) {
            recentHistory.forEach { line ->
                promptBuilder.appendLine(line)
            }
        } else {
            promptBuilder.appendLine("[This is the start of the conversation]")
        }
        
        // 5. Current user message
        promptBuilder.appendLine()
        promptBuilder.appendLine("=== CURRENT MESSAGE ===")
        promptBuilder.appendLine("User: $userMessage")
        promptBuilder.appendLine()
        
        // 6. Response prompt
        promptBuilder.appendLine("=== RESPONSE ===")
        promptBuilder.append("Kaede: ")
        
        return promptBuilder.toString()
    }
    
    /**
     * Build simplified prompt (for demo mode or testing)
     */
    fun buildSimplePrompt(userMessage: String): String {
        return """
$BASE_SYSTEM_PROMPT

User: $userMessage

Kaede: """.trimIndent()
    }
    
    /**
     * Extract key information from message for memory storage
     * 
     * @param message User message to analyze
     * @return Importance score (0-10)
     */
    fun calculateImportance(message: String): Int {
        // Keywords that indicate important information
        val importantKeywords = listOf(
            "name", "call me", "remember", "like", "love", "hate",
            "birthday", "anniversary", "favorite", "prefer",
            "friend", "family", "work", "school", "study",
            "feel", "think", "believe", "want", "need",
            "happy", "sad", "angry", "excited", "nervous"
        )
        
        val lowerMessage = message.lowercase()
        var score = 0
        
        // Check for important keywords
        importantKeywords.forEach { keyword ->
            if (lowerMessage.contains(keyword)) {
                score += 2
            }
        }
        
        // Check for personal information patterns
        when {
            lowerMessage.contains("my name is") -> score += 5
            lowerMessage.contains("call me") -> score += 4
            lowerMessage.contains("i like") || lowerMessage.contains("i love") -> score += 3
            lowerMessage.contains("i feel") -> score += 3
            lowerMessage.contains("remember") -> score += 4
        }
        
        // Cap at 10
        return minOf(score, 10)
    }
}
