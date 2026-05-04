package com.kaede.app.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kaede.app.ai.GenerationResult
import com.kaede.app.ai.MlcLlm
import com.kaede.app.ai.Mood
import com.kaede.app.ai.ModelLoadState
import com.kaede.app.ai.PersonalityManager
import com.kaede.app.ai.PromptBuilder
import com.kaede.app.data.db.KaedeDatabase
import com.kaede.app.data.memory.MemoryManager
import com.kaede.app.data.memory.MemoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Chat ViewModel - Manages chat UI state and MLC LLM interaction
 */
class ChatViewModel(application: Application) : AndroidViewModel(application) {
    
    companion object {
        private const val TAG = "ChatViewModel"
    }
    
    private val database = KaedeDatabase.getDatabase(application)
    private val memoryRepository = MemoryRepository(
        memoryDao = database.memoryDao(),
        preferencesDao = database.userPreferencesDao()
    )
    private val mlcLlm = MlcLlm()
    private val personalityManager = PersonalityManager()
    private val promptBuilder = PromptBuilder()
    private val memoryManager = MemoryManager()
    
    // Chat messages
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()
    
    // Typing state
    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()
    
    // Current streaming response
    private val _currentResponse = MutableStateFlow("")
    val currentResponse: StateFlow<String> = _currentResponse.asStateFlow()
    
    // Mood state
    private val _currentMood = MutableStateFlow(Mood.NORMAL)
    val currentMood: StateFlow<Mood> = _currentMood.asStateFlow()
    
    // Model loading state
    private val _modelState = MutableStateFlow<ModelLoadState>(ModelLoadState.Idle)
    val modelState: StateFlow<ModelLoadState> = _modelState.asStateFlow()
    
    // Performance stats
    private val _performanceStats = MutableStateFlow("")
    val performanceStats: StateFlow<String> = _performanceStats.asStateFlow()
    
    // Token usage stats
    private val _tokenStats = MutableStateFlow("Tokens: 0 | 0.0 tok/s")
    val tokenStats: StateFlow<String> = _tokenStats.asStateFlow()
    
    init {
        initializeMlcLlm()
    }
    
    /**
     * Initialize MLC LLM
     */
    private fun initializeMlcLlm() {
        viewModelScope.launch {
            mlcLlm.init()
        }
        
        viewModelScope.launch {
            mlcLlm.loadModelFromAssets(
                context = getApplication(),
                modelAssetName = "gemma-2b-it-q4f16_1-MLC"
            )
            
            mlcLlm.loadProgress.collect { progress ->
                if (progress >= 1.0f) {
                    _modelState.value = ModelLoadState.Success(
                        com.kaede.app.ai.ModelInfo(
                            name = "gemma-2b-it-q4f16_1-MLC",
                            path = "cache",
                            sizeBytes = 0,
                            version = 3,
                            tensorCount = 0,
                            metadataCount = 0,
                            contextSize = 2048,
                            parameters = "MLC-LLM Gemma-2B"
                        )
                    )
                    addSystemMessage("✨ Kaede is ready! (MLC-LLM)")
                } else if (progress > 0f) {
                    _modelState.value = ModelLoadState.Loading(
                        progress = progress,
                        status = "Loading MLC model... ${(progress * 100).toInt()}%"
                    )
                }
            }
        }
    }
    
    /**
     * Send user message
     */
    fun sendMessage(userMessage: String) {
        viewModelScope.launch {
            if (_modelState.value !is ModelLoadState.Success) {
                addSystemMessage("Kaede is still loading... Please wait.")
                return@launch
            }
            
            // Add user message
            val userChatMessage = ChatMessage(
                content = userMessage,
                isUser = true
            )
            _messages.value = _messages.value + userChatMessage
            
            // Add to memory
            memoryManager.addToBuffer("User: $userMessage")
            
            // Update mood
            personalityManager.updateMood(userMessage)
            _currentMood.value = personalityManager.getCurrentMood()
            
            // Generate response
            generateResponse(userMessage)
        }
    }
    
    /**
     * Generate AI response with token tracking
     */
    private fun generateResponse(userMessage: String) {
        viewModelScope.launch {
            _isTyping.value = true
            _currentResponse.value = ""
            
            try {
                // Build prompt
                val prompt = promptBuilder.buildPrompt(
                    mood = personalityManager.getCurrentMood(),
                    memories = memoryRepository.getContextMemories(5),
                    conversationHistory = memoryManager.getConversationHistory(),
                    userMessage = userMessage
                )
                
                // Generate with MLC LLM and get token stats
                val result = mlcLlm.generate(
                    prompt = prompt,
                    onToken = { token ->
                        _currentResponse.value += token
                    }
                )
                
                // Update token stats
                _tokenStats.value = "Tokens: ${result.tokenCount} | ${String.format("%.2f", result.tokensPerSecond)} tok/s | ${result.generationTimeMs}ms"
                _performanceStats.value = "⚡ ${result.tokenCount} tokens @ ${String.format("%.1f", result.tokensPerSecond)} tok/s"
                
                // Add response to chat
                val fullResponse = _currentResponse.value
                val kaedeMessage = ChatMessage(
                    content = fullResponse,
                    isUser = false
                )
                _messages.value = _messages.value + kaedeMessage
                
                // Save to memory
                memoryManager.addToBuffer("Kaede: $fullResponse")
                
                val importance = promptBuilder.calculateImportance(userMessage)
                if (importance >= 5) {
                    memoryRepository.saveMemory(
                        content = "User said: $userMessage",
                        importanceScore = importance,
                        type = com.kaede.app.data.db.MemoryType.CONVERSATION,
                        associatedMood = personalityManager.getCurrentMood().name
                    )
                }
                
                Log.d(TAG, "Generation complete: ${result.tokenCount} tokens @ ${String.format("%.2f", result.tokensPerSecond)} tok/s")
                
            } catch (e: Exception) {
                Log.e(TAG, "Generation failed", e)
                val errorMessage = ChatMessage(
                    content = "Kaede had a thinking moment... Try again? 💜",
                    isUser = false
                )
                _messages.value = _messages.value + errorMessage
            } finally {
                _isTyping.value = false
                _currentResponse.value = ""
            }
        }
    }
    
    /**
     * Add system message
     */
    private fun addSystemMessage(content: String) {
        val systemMessage = ChatMessage(
            content = content,
            isUser = false
        )
        _messages.value = _messages.value + systemMessage
    }
    
    /**
     * Clear chat history
     */
    fun clearChat() {
        viewModelScope.launch {
            _messages.value = emptyList()
            memoryManager.clearBuffer()
            addSystemMessage("Memory cleared! Kaede remembers nothing~ 💜")
        }
    }
    
    /**
     * Retry model loading
     */
    fun retryModelLoad() {
        viewModelScope.launch {
            _modelState.value = ModelLoadState.Idle
            initializeMlcLlm()
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        mlcLlm.release()
    }
}

/**
 * Chat Message data class
 */
data class ChatMessage(
    val content: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
