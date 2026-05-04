package com.kaede.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kaede.app.ai.Mood

/**
 * Mood Indicator - Shows Kaede's current emotional state
 */
@Composable
fun MoodIndicator(
    mood: Mood,
    modifier: Modifier = Modifier
) {
    val moodColor = getMoodColor(mood)
    val moodText = getMoodText(mood)
    
    Row(
        modifier = modifier
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Mood dot
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(moodColor)
                .border(
                    width = 2.dp,
                    color = moodColor.copy(alpha = 0.5f),
                    shape = CircleShape
                )
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Mood text
        Text(
            text = moodText,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Get color for each mood state
 */
@Composable
private fun getMoodColor(mood: Mood): Color {
    return when (mood) {
        Mood.HAPPY -> Color(0xFFFFD700) // Gold
        Mood.PLAYFUL -> Color(0xFFFF69B4) // Hot pink
        Mood.JEALOUS -> Color(0xFF32CD32) // Lime green
        Mood.SHY -> Color(0xFFFFB6C1) // Light pink
        Mood.NORMAL -> MaterialTheme.colorScheme.primary
    }
}

/**
 * Get display text for each mood state
 */
private fun getMoodText(mood: Mood): String {
    return when (mood) {
        Mood.HAPPY -> "Kaede is happy ✨"
        Mood.PLAYFUL -> "Kaede is playful 😊"
        Mood.JEALOUS -> "Kaede is curious 🤔"
        Mood.SHY -> "Kaede is shy 🌸"
        Mood.NORMAL -> "Kaede is here 💜"
    }
}
