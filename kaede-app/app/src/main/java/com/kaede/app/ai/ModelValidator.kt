package com.kaede.app.ai

import android.util.Log
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Model Validator - Validates GGUF model files
 * 
 * GGUF magic number: 0x46554747 ("GGUF" in little-endian)
 */
object ModelValidator {
    
    private const val TAG = "ModelValidator"
    
    // GGUF magic number (little-endian)
    private const val GGUF_MAGIC = 0x46554747 // "GGUF"
    
    // Minimum valid GGUF file size (header + metadata)
    private const val MIN_GGUF_SIZE = 512 * 1024L // 512 KB
    
    /**
     * Validate GGUF model file
     * 
     * @param file Model file to validate
     * @return ValidationResult with success status and error message
     */
    fun validate(file: File): ValidationResult {
        Log.d(TAG, "Validating model file: ${file.absolutePath}")
        
        // Check file exists
        if (!file.exists()) {
            return ValidationResult(
                success = false,
                error = "Model file not found: ${file.absolutePath}"
            )
        }
        
        // Check file size
        val fileSize = file.length()
        if (fileSize < MIN_GGUF_SIZE) {
            return ValidationResult(
                success = false,
                error = "Model file too small (${formatSize(fileSize)}). Minimum ${formatSize(MIN_GGUF_SIZE)} required."
            )
        }
        
        // Check file extension
        if (!file.name.endsWith(".gguf", ignoreCase = true)) {
            return ValidationResult(
                success = false,
                error = "Invalid file extension. Expected .gguf file."
            )
        }
        
        // Validate GGUF header
        return try {
            validateGgufHeader(file)
        } catch (e: Exception) {
            Log.e(TAG, "Error validating GGUF header", e)
            ValidationResult(
                success = false,
                error = "Failed to read model file: ${e.message}"
            )
        }
    }
    
    /**
     * Validate GGUF header structure
     */
    private fun validateGgufHeader(file: File): ValidationResult {
        RandomAccessFile(file, "r").use { raf ->
            // Read first 16 bytes for header validation
            val header = ByteArray(16)
            raf.read(header)
            
            val buffer = ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN)
            
            // Read magic number (4 bytes)
            val magic = buffer.int
            
            if (magic != GGUF_MAGIC) {
                Log.e(TAG, "Invalid GGUF magic number: 0x${magic.toString(16)}")
                return ValidationResult(
                    success = false,
                    error = "Invalid GGUF format. Magic number mismatch."
                )
            }
            
            // Read version (4 bytes)
            val version = buffer.int
            Log.d(TAG, "GGUF version: $version")
            
            if (version < 1 || version > 3) {
                Log.w(TAG, "Unknown GGUF version: $version (expected 1-3)")
            }
            
            // Read tensor count (8 bytes in v3, 4 bytes in v1-v2)
            val tensorCount = if (version >= 3) {
                buffer.long
            } else {
                buffer.int.toLong()
            }
            
            // Read metadata KV count (8 bytes in v3, 4 bytes in v1-v2)
            val metadataKvCount = if (version >= 3) {
                buffer.long
            } else {
                buffer.int.toLong()
            }
            
            Log.d(TAG, "Model validation successful")
            Log.d(TAG, "  Tensors: $tensorCount")
            Log.d(TAG, "  Metadata entries: $metadataKvCount")
            
            return ValidationResult(
                success = true,
                fileSize = file.length(),
                version = version,
                tensorCount = tensorCount,
                metadataCount = metadataKvCount
            )
        }
    }
    
    /**
     * Format file size for display
     */
    private fun formatSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
            else -> "${bytes / (1024 * 1024 * 1024)} GB"
        }
    }
}

/**
 * Validation result data class
 */
data class ValidationResult(
    val success: Boolean,
    val error: String? = null,
    val fileSize: Long = 0,
    val version: Int = 0,
    val tensorCount: Long = 0,
    val metadataCount: Long = 0
) {
    /**
     * Get formatted model info for display
     */
    fun getModelInfo(): String {
        if (!success) {
            return "Validation failed: $error"
        }
        
        return buildString {
            append("✓ Valid GGUF model\n")
            append("Version: $version\n")
            append("Size: ${formatSize(fileSize)}\n")
            append("Tensors: $tensorCount\n")
            append("Metadata: $metadataCount entries")
        }
    }
    
    private fun formatSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
            else -> "${bytes / (1024 * 1024 * 1024)} GB"
        }
    }
}
