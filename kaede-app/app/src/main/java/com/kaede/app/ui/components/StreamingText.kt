package com.kaede.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

/**
 * Simple streaming text with blinking cursor
 */
@Composable
fun SimpleStreamingText(
    text: String,
    isStreaming: Boolean,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    style: TextStyle = LocalTextStyle.current
) {
    // Blinking cursor animation
    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursorBlink"
    )
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = text,
            color = color,
            style = style
        )
        
        // Show blinking cursor when streaming
        if (isStreaming) {
            Spacer(modifier = Modifier.width(2.dp))
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(
                        with(LocalDensity.current) {
                            (style.fontSize.value * 1.2).dp
                        }
                    )
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = cursorAlpha),
                        shape = RoundedCornerShape(1.dp)
                    )
            )
        }
    }
}
