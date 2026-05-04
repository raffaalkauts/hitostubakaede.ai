package com.kaede.app.ai

/**
 * Generation result with token usage statistics
 */
data class GenerationResult(
    val text: String,
    val tokenCount: Int,
    val generationTimeMs: Long,
    val tokensPerSecond: Double
)
