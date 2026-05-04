package com.kaede.app.ui.chat

/**
 * Chat UI State - Represents the current state of the chat interface
 */
sealed class ChatUiState {
    
    /**
     * Initial state - waiting for model to load
     */
    object Initializing : ChatUiState()
    
    /**
     * Model is loading
     * 
     * @param progress Loading progress (0.0 to 1.0)
     * @param status Human-readable status message
     */
    data class Loading(
        val progress: Float = 0f,
        val status: String = "Loading..."
    ) : ChatUiState()
    
    /**
     * Chat is ready for conversation
     */
    object Ready : ChatUiState()
    
    /**
     * Error state
     * 
     * @param message Error message to display
     * @param recoverable Whether the error can be recovered
     */
    data class Error(
        val message: String,
        val recoverable: Boolean = true
    ) : ChatUiState()
    
    /**
     * Check if chat is interactive
     */
    fun canInteract(): Boolean = this is Ready
    
    /**
     * Check if loading is in progress
     */
    fun isLoading(): Boolean = this is Loading || this is Initializing
}

/**
 * Input State - Represents the state of the input bar
 */
sealed class InputState {
    
    /**
     * Input is enabled and ready
     */
    object Enabled : InputState()
    
    /**
     * Input is disabled (AI is thinking)
     */
    object Disabled : InputState()
    
    /**
     * Input is disabled with reason
     */
    data class DisabledWithReason(
        val reason: String
    ) : InputState()
    
    /**
     * Check if input is enabled
     */
    fun isEnabled(): Boolean = this is Enabled
}
