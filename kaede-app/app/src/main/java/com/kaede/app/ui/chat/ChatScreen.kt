package com.kaede.app.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kaede.app.ai.ModelLoadState
import com.kaede.app.ui.components.InputBar
import com.kaede.app.ui.components.MessageBubble
import com.kaede.app.ui.components.MoodIndicator
import com.kaede.app.ui.components.TypingIndicator
import com.kaede.app.ui.theme.KaedePrimary
import com.kaede.app.ui.theme.KaedePrimaryLight
import com.kaede.app.ui.theme.KaedeSurface
import com.kaede.app.ui.theme.KaedeSurfaceVariant
import com.kaede.app.ui.theme.StatusError
import com.kaede.app.ui.theme.StatusInfo
import com.kaede.app.ui.theme.StatusSuccess
import com.kaede.app.ui.theme.StatusWarning
import com.kaede.app.viewmodel.ChatViewModel
import com.kaede.app.viewmodel.ChatMessage
import java.text.SimpleDateFormat
import java.util.*

/**
 * Chat Screen - Modern & Clean UI
 */
@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel? = null
) {
    val actualViewModel: ChatViewModel = viewModel ?: viewModel()
    
    val messages by actualViewModel.messages.collectAsState()
    val isTyping by actualViewModel.isTyping.collectAsState()
    val currentMood by actualViewModel.currentMood.collectAsState()
    val modelState by actualViewModel.modelState.collectAsState()
    val performanceStats by actualViewModel.performanceStats.collectAsState()
    val tokenStats by actualViewModel.tokenStats.collectAsState()
    
    val listState = rememberLazyListState()
    
    // Auto-scroll to bottom
    LaunchedEffect(messages.size, isTyping) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }
    
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Modern Header with Gradient
            ModernHeader(
                modelState = modelState,
                performanceStats = performanceStats,
                tokenStats = tokenStats,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Main content
            when (modelState) {
                is ModelLoadState.Idle,
                is ModelLoadState.Loading -> {
                    ModernLoadingScreen(
                        progress = (modelState as? ModelLoadState.Loading)?.progress ?: 0f,
                        status = (modelState as? ModelLoadState.Loading)?.status ?: "Initializing...",
                        modifier = Modifier.weight(1f)
                    )
                }
                is ModelLoadState.Error -> {
                    ModernErrorScreen(
                        error = (modelState as ModelLoadState.Error).error,
                        recoverable = (modelState as ModelLoadState.Error).recoverable,
                        onRetry = { actualViewModel.retryModelLoad() },
                        modifier = Modifier.weight(1f)
                    )
                }
                is ModelLoadState.Success -> {
                    ModernChatContent(
                        messages = messages,
                        isTyping = isTyping,
                        currentMood = currentMood,
                        listState = listState,
                        onSendMessage = { userMessage ->
                            actualViewModel.sendMessage(userMessage)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Modern Input Bar
            ModernInputBar(
                onSendMessage = { userMessage ->
                    actualViewModel.sendMessage(userMessage)
                },
                enabled = modelState is ModelLoadState.Success && !isTyping,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Modern Header with Gradient & Shadow
 */
@Composable
private fun ModernHeader(
    modelState: ModelLoadState,
    performanceStats: String,
    tokenStats: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = KaedeSurface,
        shadowElevation = 8.dp,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(KaedePrimaryLight, KaedeSurface)
                    )
                )
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // App Name & Status
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Status Indicator with Glow
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                color = when (modelState) {
                                    is ModelLoadState.Success -> StatusSuccess
                                    is ModelLoadState.Error -> StatusError
                                    is ModelLoadState.Loading -> StatusWarning
                                    is ModelLoadState.Idle -> StatusInfo
                                },
                                shape = RoundedCornerShape(50)
                            )
                            .shadow(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(50),
                                clip = false
                            )
                    )
                    
                    Text(
                        text = when (modelState) {
                            is ModelLoadState.Idle -> "Initializing"
                            is ModelLoadState.Loading -> "Loading Model"
                            is ModelLoadState.Success -> "Kaede Ready"
                            is ModelLoadState.Error -> "Error"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                // Model Info Badge
                if (modelState is ModelLoadState.Success) {
                    Surface(
                        color = KaedePrimaryLight.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Gemma-3 1B",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = KaedePrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
            
            // Performance Stats
            if (performanceStats.isNotEmpty() && modelState is ModelLoadState.Success) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = KaedePrimaryLight.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = performanceStats,
                            style = MaterialTheme.typography.bodySmall,
                            color = KaedePrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (tokenStats.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = tokenStats,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Modern Chat Content
 */
@Composable
private fun ModernChatContent(
    messages: List<ChatMessage>,
    isTyping: Boolean,
    currentMood: com.kaede.app.ai.Mood,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Mood Indicator - Modern Style
        ModernMoodIndicator(mood = currentMood)
        
        // Message List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(
                items = messages,
                key = { message -> message.timestamp }
            ) { message ->
                val isLastKaedeMessage = !message.isUser &&
                    isTyping &&
                    message == messages.lastOrNull { !it.isUser }
                
                ModernMessageBubble(
                    message = message,
                    isStreaming = isLastKaedeMessage
                )
            }
            
            // Typing Indicator
            if (isTyping) {
                item {
                    ModernTypingIndicator()
                }
            }
        }
    }
}

/**
 * Modern Message Bubble
 */
@Composable
private fun ModernMessageBubble(
    message: ChatMessage,
    isStreaming: Boolean = false
) {
    MessageBubble(
        message = message.content,
        isUser = message.isUser,
        timestamp = formatTimestamp(message.timestamp),
        isStreaming = isStreaming
    )
}

/**
 * Modern Typing Indicator
 */
@Composable
private fun ModernTypingIndicator() {
    TypingIndicator()
}

/**
 * Modern Mood Indicator
 */
@Composable
private fun ModernMoodIndicator(mood: com.kaede.app.ai.Mood) {
    MoodIndicator(mood = mood)
}

/**
 * Modern Input Bar
 */
@Composable
private fun ModernInputBar(
    onSendMessage: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    InputBar(
        onSendMessage = onSendMessage,
        enabled = enabled,
        modifier = modifier
    )
}

/**
 * Modern Loading Screen
 */
@Composable
private fun ModernLoadingScreen(
    progress: Float,
    status: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Animated Logo
            Surface(
                modifier = Modifier.size(100.dp),
                shape = RoundedCornerShape(50),
                color = KaedePrimaryLight.copy(alpha = 0.2f),
                shadowElevation = 12.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "K",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = KaedePrimary
                    )
                }
            }
            
            // Loading Text
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Loading Kaede...",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Text(
                    text = status,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Modern Progress Bar
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(50)),
                    color = KaedePrimary,
                    trackColor = KaedePrimaryLight.copy(alpha = 0.3f)
                )
                
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * Modern Error Screen
 */
@Composable
private fun ModernErrorScreen(
    error: String,
    recoverable: Boolean,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Error Icon
            Surface(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(50),
                color = StatusError.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "⚠️",
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }
            
            // Error Text
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (recoverable) "Oops!" else "Critical Error",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    softWrap = true
                )
            }
            
            // Retry Button
            if (recoverable) {
                Button(
                    onClick = onRetry,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KaedePrimary
                    )
                ) {
                    Text(
                        text = "Try Again",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Text(
                    text = "Kaede will run in demo mode until model loads",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Format timestamp
 */
private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
