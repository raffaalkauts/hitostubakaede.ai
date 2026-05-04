package com.kaede.app.util

import android.app.ActivityManager
import android.content.Context
import android.os.Debug
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.roundToInt

/**
 * Performance Monitor - Tracks app performance metrics
 * 
 * Monitors:
 * - Memory usage (heap, native, total)
 * - Token generation rate
 * - Generation latency
 * - CPU usage (estimated)
 */
class PerformanceMonitor(private val context: Context) {
    
    companion object {
        private const val TAG = "PerformanceMonitor"
        
        // Memory warning thresholds
        private const val MEMORY_WARNING_THRESHOLD_MB = 1500
        private const val MEMORY_CRITICAL_THRESHOLD_MB = 1800
        
        // Token rate calculation window
        private const val TOKEN_RATE_WINDOW_MS = 1000L
    }
    
    // Memory state
    private val _memoryInfo = MutableStateFlow(MemoryInfo())
    val memoryInfo: StateFlow<MemoryInfo> = _memoryInfo.asStateFlow()
    
    // Token generation rate
    private val _tokenRate = MutableStateFlow(0)
    val tokenRate: StateFlow<Int> = _tokenRate.asStateFlow()
    
    // Generation state
    private var generationStartTime = 0L
    private var tokenCount = 0
    private val tokenTimestamps = mutableListOf<Long>()
    
    // Activity manager for memory info
    private val activityManager: ActivityManager by lazy {
        context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    }
    
    /**
     * Get current memory usage
     */
    fun updateMemoryInfo() {
        val runtime = Runtime.getRuntime()
        
        val heapFree = runtime.freeMemory() / (1024 * 1024)
        val heapMax = runtime.maxMemory() / (1024 * 1024)
        val heapUsed = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
        
        // Get native memory (Android-specific)
        val nativeHeapAllocated = Debug.getNativeHeapAllocatedSize() / (1024 * 1024)
        val nativeHeapFree = Debug.getNativeHeapFreeSize() / (1024 * 1024)
        
        val memoryInfo = MemoryInfo(
            heapUsedMb = heapUsed.toInt(),
            heapFreeMb = heapFree.toInt(),
            heapMaxMb = heapMax.toInt(),
            nativeAllocatedMb = nativeHeapAllocated.toInt(),
            nativeFreeMb = nativeHeapFree.toInt(),
            totalUsedMb = (heapUsed + nativeHeapAllocated).toInt(),
            isLowMemory = heapUsed > heapMax * 0.8,
            memoryLevel = getMemoryLevel(heapUsed, heapMax)
        )
        
        _memoryInfo.value = memoryInfo
        
        // Log warnings
        if (memoryInfo.totalUsedMb > MEMORY_CRITICAL_THRESHOLD_MB) {
            Log.w(TAG, "CRITICAL: Memory usage at ${memoryInfo.totalUsedMb}MB")
        } else if (memoryInfo.totalUsedMb > MEMORY_WARNING_THRESHOLD_MB) {
            Log.w(TAG, "WARNING: Memory usage at ${memoryInfo.totalUsedMb}MB")
        }
    }
    
    /**
     * Start token generation timing
     */
    fun startGeneration() {
        generationStartTime = System.currentTimeMillis()
        tokenCount = 0
        tokenTimestamps.clear()
    }
    
    /**
     * Record token generated
     */
    fun recordToken() {
        val now = System.currentTimeMillis()
        tokenCount++
        tokenTimestamps.add(now)
        
        // Calculate tokens per second (over last second)
        val oneSecondAgo = now - TOKEN_RATE_WINDOW_MS
        val recentTokens = tokenTimestamps.count { it >= oneSecondAgo }
        
        _tokenRate.value = recentTokens
        
        // Remove old timestamps to prevent memory leak
        if (tokenTimestamps.size > 1000) {
            tokenTimestamps.removeAll { it < oneSecondAgo }
        }
    }
    
    /**
     * End token generation and return stats
     */
    fun endGeneration(): GenerationStats {
        val endTime = System.currentTimeMillis()
        val duration = endTime - generationStartTime
        
        val stats = GenerationStats(
            tokenCount = tokenCount,
            durationMs = duration,
            tokensPerSecond = if (duration > 0) {
                ((tokenCount * 1000.0) / duration).roundToInt()
            } else {
                0
            },
            avgLatencyMs = if (tokenCount > 0) {
                (duration.toDouble() / tokenCount).roundToInt()
            } else {
                0
            }
        )
        
        Log.d(TAG, "Generation complete: ${stats.tokensPerSecond} tok/s, ${stats.avgLatency}ms/token")
        
        return stats
    }
    
    /**
     * Get memory level (low, medium, high)
     */
    private fun getMemoryLevel(used: Long, max: Long): MemoryLevel {
        val ratio = used.toDouble() / max.toDouble()
        return when {
            ratio < 0.5 -> MemoryLevel.LOW
            ratio < 0.75 -> MemoryLevel.MEDIUM
            ratio < 0.9 -> MemoryLevel.HIGH
            else -> MemoryLevel.CRITICAL
        }
    }
    
    /**
     * Get app memory info from ActivityManager
     */
    fun getAppMemoryInfo(): AppMemoryInfo {
        val memInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)
        
        return AppMemoryInfo(
            availableMemMb = memInfo.availMem / (1024 * 1024),
            totalMemMb = memInfo.totalMem / (1024 * 1024),
            lowMemory = memInfo.lowMemory,
            thresholdMb = memInfo.threshold / (1024 * 1024)
        )
    }
    
    /**
     * Clear all monitoring data
     */
    fun clear() {
        tokenCount = 0
        tokenTimestamps.clear()
        _tokenRate.value = 0
    }
}

/**
 * Memory information data class
 */
data class MemoryInfo(
    val heapUsedMb: Int = 0,
    val heapFreeMb: Int = 0,
    val heapMaxMb: Int = 0,
    val nativeAllocatedMb: Int = 0,
    val nativeFreeMb: Int = 0,
    val totalUsedMb: Int = 0,
    val isLowMemory: Boolean = false,
    val memoryLevel: MemoryLevel = MemoryLevel.LOW
)

/**
 * App memory info from system
 */
data class AppMemoryInfo(
    val availableMemMb: Long,
    val totalMemMb: Long,
    val lowMemory: Boolean,
    val thresholdMb: Long
)

/**
 * Generation statistics
 */
data class GenerationStats(
    val tokenCount: Int,
    val durationMs: Long,
    val tokensPerSecond: Int,
    val avgLatencyMs: Int
) {
    /**
     * Get formatted tokens per second
     */
    fun getFormattedTokPerSec(): String {
        return "$tokensPerSecond tok/s"
    }
    
    /**
     * Get formatted latency
     */
    fun getFormattedLatency(): String {
        return "${avgLatencyMs}ms/token"
    }
    
    /**
     * Get average latency per token
     */
    val avgLatency: Int
        get() = avgLatencyMs
}

/**
 * Memory level enum
 */
enum class MemoryLevel {
    LOW,      // < 50% heap used
    MEDIUM,   // 50-75% heap used
    HIGH,     // 75-90% heap used
    CRITICAL  // > 90% heap used
}
