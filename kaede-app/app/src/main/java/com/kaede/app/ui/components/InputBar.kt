package com.kaede.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.kaede.app.ui.theme.KaedePrimary
import com.kaede.app.ui.theme.KaedeSurface
import com.kaede.app.ui.theme.KaedeSurfaceVariant

/**
 * Modern Input Bar with Shadow & Gradient
 */
@Composable
fun InputBar(
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var message by remember { mutableStateOf("") }
    
    Surface(
        modifier = modifier,
        color = KaedeSurface,
        shadowElevation = 12.dp,
        tonalElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(KaedeSurface, KaedeSurfaceVariant.copy(alpha = 0.3f))
                    )
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Modern Text Input
                ModernTextInput(
                    value = message,
                    onValueChange = { message = it },
                    modifier = Modifier.weight(1f),
                    enabled = enabled
                )
                
                // Modern Send Button
                ModernSendButton(
                    onClick = {
                        if (message.isNotBlank() && enabled) {
                            onSendMessage(message.trim())
                            message = ""
                        }
                    },
                    enabled = message.isNotBlank() && enabled
                )
            }
        }
    }
}

/**
 * Modern Text Input with Rounded Corners
 */
@Composable
private fun ModernTextInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .background(
                color = KaedeSurface,
                shape = RoundedCornerShape(28.dp)
            )
            .border(
                width = 1.5.dp,
                color = if (enabled) KaedePrimary.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(28.dp)
            )
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(28.dp),
                clip = false
            )
            .padding(horizontal = 20.dp, vertical = 14.dp),
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize
        ),
        cursorBrush = SolidColor(KaedePrimary),
        maxLines = 4,
        enabled = enabled,
        decorationBox = { innerTextField ->
            if (value.isEmpty()) {
                Text(
                    text = "Message Kaede...",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            innerTextField()
        }
    )
}

/**
 * Modern Send Button with Gradient & Shadow
 */
@Composable
private fun ModernSendButton(
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    val buttonColor = if (enabled) {
        KaedePrimary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    
    val iconColor = if (enabled) {
        KaedeSurface
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    }
    
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .shadow(
                elevation = if (enabled) 6.dp else 0.dp,
                shape = CircleShape,
                clip = false
            )
            .background(
                color = buttonColor,
                shape = CircleShape
            )
            .clickable(
                enabled = enabled,
                onClick = onClick
            )
    ) {
        Icon(
            imageVector = Icons.Default.Send,
            contentDescription = "Send",
            tint = iconColor,
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
        )
    }
}
