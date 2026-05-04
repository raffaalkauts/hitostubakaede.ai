package com.kaede.app.ai

/**
 * Model Load State - Represents the current state of model loading
 */
sealed class ModelLoadState {
    
    /**
     * Initial state - model loading not started
     */
    object Idle : ModelLoadState()
    
    /**
     * Model loading in progress
     */
    data class Loading(
        val progress: Float = 0f,
        val status: String = "Loading..."
    ) : ModelLoadState()
    
    /**
     * Model loaded successfully
     */
    data class Success(
        val modelInfo: ModelInfo
    ) : ModelLoadState()
    
    /**
     * Model loading failed
     */
    data class Error(
        val error: String,
        val recoverable: Boolean = true
    ) : ModelLoadState()
    
    /**
     * Get display text for current state
     */
    fun getDisplayText(): String {
        return when (this) {
            is Idle -> "Model not loaded"
            is Loading -> status
            is Success -> "Model ready: ${modelInfo.parameters}"
            is Error -> error
        }
    }
    
    /**
     * Check if model is ready for inference
     */
    fun isReady(): Boolean = this is Success
}

/**
 * Model Information
 */
data class ModelInfo(
    val name: String,
    val path: String,
    val sizeBytes: Long,
    val version: Int,
    val tensorCount: Long,
    val metadataCount: Long,
    val contextSize: Int,
    val parameters: String
) {
    /**
     * Get formatted size string
     */
    fun getFormattedSize(): String {
        return when {
            sizeBytes < 1024 -> "$sizeBytes B"
            sizeBytes < 1024 * 1024 -> "${sizeBytes / 1024} KB"
            sizeBytes < 1024 * 1024 * 1024 -> "${sizeBytes / (1024 * 1024)} MB"
            else -> String.format("%.2f GB", sizeBytes / (1024.0 * 1024.0 * 1024.0))
        }
    }
    
    companion object {
        /**
         * Estimate parameter count from tensor count
         */
        fun estimateParameters(tensorCount: Long): String {
            return when {
                tensorCount < 100 -> "< 100M"
                tensorCount < 500 -> "~ 500M"
                tensorCount < 1000 -> "~ 1B"
                tensorCount < 2000 -> "~ 1.5B"
                tensorCount < 5000 -> "~ 3B"
                tensorCount < 10000 -> "~ 7B"
                else -> "> 7B"
            }
        }
    }
}
