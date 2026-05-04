package com.kaede.app.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle

/**
 * Text Formatter - Applies basic formatting to text
 * 
 * Supports:
 * - **bold** text
 * - *italic* text
 * - _underline_ text
 * - `code` blocks
 * - ~~strikethrough~~ text
 */
object TextFormatter {
    
    /**
     * Format text with markdown-like syntax (returns AnnotatedString)
     */
    fun FormattedText(
        text: String,
        style: TextStyle = androidx.compose.ui.text.TextStyle.Default
    ): AnnotatedString {
        return formatText(text)
    }
    
    /**
     * Format text into AnnotatedString with styles
     */
    fun formatText(text: String): AnnotatedString {
        return buildAnnotatedString {
            var currentIndex = 0
            var pos = 0
            
            while (pos < text.length) {
                // Check for bold (**text**)
                if (text.startsWith("**", pos)) {
                    val endPos = text.indexOf("**", pos + 2)
                    if (endPos != -1) {
                        append(text.substring(currentIndex, pos))
                        withStyle(
                            style = SpanStyle(fontWeight = FontWeight.Bold)
                        ) {
                            append(text.substring(pos + 2, endPos))
                        }
                        pos = endPos + 2
                        currentIndex = pos
                        continue
                    }
                }
                
                // Check for italic (*text* or _text_)
                if (text.startsWith("*", pos) && !text.startsWith("**", pos)) {
                    val endPos = text.indexOf("*", pos + 1)
                    if (endPos != -1 && !text.startsWith("*", endPos + 1)) {
                        append(text.substring(currentIndex, pos))
                        withStyle(
                            style = SpanStyle(fontStyle = FontStyle.Italic)
                        ) {
                            append(text.substring(pos + 1, endPos))
                        }
                        pos = endPos + 1
                        currentIndex = pos
                        continue
                    }
                }
                
                // Check for underline (_text_)
                if (text.startsWith("_", pos)) {
                    val endPos = text.indexOf("_", pos + 1)
                    if (endPos != -1) {
                        append(text.substring(currentIndex, pos))
                        withStyle(
                            style = SpanStyle(textDecoration = TextDecoration.Underline)
                        ) {
                            append(text.substring(pos + 1, endPos))
                        }
                        pos = endPos + 1
                        currentIndex = pos
                        continue
                    }
                }
                
                // Check for code (`text`)
                if (text.startsWith("`", pos)) {
                    val endPos = text.indexOf("`", pos + 1)
                    if (endPos != -1) {
                        append(text.substring(currentIndex, pos))
                        withStyle(
                            style = SpanStyle(
                                fontFamily = FontFamily.Monospace
                            )
                        ) {
                            append(text.substring(pos + 1, endPos))
                        }
                        pos = endPos + 1
                        currentIndex = pos
                        continue
                    }
                }
                
                // Check for strikethrough (~~text~~)
                if (text.startsWith("~~", pos)) {
                    val endPos = text.indexOf("~~", pos + 2)
                    if (endPos != -1) {
                        append(text.substring(currentIndex, pos))
                        withStyle(
                            style = SpanStyle(textDecoration = TextDecoration.LineThrough)
                        ) {
                            append(text.substring(pos + 2, endPos))
                        }
                        pos = endPos + 2
                        currentIndex = pos
                        continue
                    }
                }
                
                pos++
            }
            
            // Append remaining text
            if (currentIndex < text.length) {
                append(text.substring(currentIndex))
            }
        }
    }
}
