package com.kaede.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kaede.app.ui.theme.KaedeBubbleKaede
import com.kaede.app.ui.theme.KaedeBubbleKaedeText
import com.kaede.app.ui.theme.KaedeBubbleUser
import com.kaede.app.ui.theme.KaedeBubbleUserText

/**
 * WhatsApp-Style Message Bubble
 * User: RIGHT side (green bubble)
 * Kaede: LEFT side (white/light bubble)
 */
@Composable
fun MessageBubble(
    message: String,
    isUser: Boolean,
    modifier: Modifier = Modifier,
    timestamp: String = "",
    isStreaming: Boolean = false
) {
    val bubbleColor = if (isUser) {
        KaedeBubbleUser  // User: Purple/Right
    } else {
        KaedeBubbleKaede  // Kaede: Light/Left
    }
    
    val textColor = if (isUser) {
        KaedeBubbleUserText
    } else {
        KaedeBubbleKaedeText
    }
    
    // WhatsApp-style rounded corners
    val bubbleShape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = if (isUser) 16.dp else 4.dp,  // User: all rounded, Kaede: bottom-left sharp
        bottomEnd = if (isUser) 4.dp else 16.dp     // User: bottom-right sharp, Kaede: all rounded
    )
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start  // User: RIGHT, Kaede: LEFT
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
        ) {
            // Kaede Avatar (LEFT side only)
            if (!isUser) {
                ModernAvatar(modifier = Modifier.size(36.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            Column(
                horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
            ) {
                // Message bubble with shadow
                Box(
                    modifier = Modifier
                        .widthIn(max = 300.dp)
                        .shadow(
                            elevation = 2.dp,
                            shape = bubbleShape,
                            clip = false
                        )
                        .background(
                            color = bubbleColor,
                            shape = bubbleShape
                        )
                        .padding(12.dp)
                ) {
                    Text(
                        text = message,
                        color = textColor,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (isUser) FontWeight.Medium else FontWeight.Normal
                    )
                }
                
                // Timestamp
                if (timestamp.isNotEmpty()) {
                    Spacer(modifier = Modifier.padding(top = 2.dp))
                    Text(
                        text = timestamp,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            // User Avatar (RIGHT side - optional, currently disabled for cleaner look)
            if (isUser) {
                Spacer(modifier = Modifier.width(8.dp))
                // Optional: Add user avatar here if needed
                Spacer(modifier = Modifier.size(36.dp))
            }
        }
    }
}

/**
 * Modern Avatar with Gradient & Shadow
 */
@Composable
fun ModernAvatar(
    modifier: Modifier = Modifier,
    moodColor: Color = KaedeBubbleUser
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = moodColor.copy(alpha = 0.2f),
        shadowElevation = 2.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "K",
                color = moodColor,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

/**
 * Legacy Avatar (for compatibility)
 */
@Composable
fun KaedeAvatar(
    modifier: Modifier = Modifier,
    moodColor: Color = KaedeBubbleUser
) {
    ModernAvatar(modifier = modifier, moodColor = moodColor)
}
