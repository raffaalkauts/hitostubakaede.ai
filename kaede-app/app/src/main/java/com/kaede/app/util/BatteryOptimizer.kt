package com.kaede.app.util

import android.content.Context
import android.os.BatteryManager
import android.os.PowerManager
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Battery Optimizer - Manages power consumption for AI inference
 * 
 * Features:
 * - Battery level monitoring
 * - Power saving mode detection
 * - Performance throttling based on battery
 * - Wake lock management for long generations
 */
class BatteryOptimizer(private val context: Context) {
    
    companion object {
        private const val TAG = "BatteryOptimizer"
        
        // Battery thresholds
        private const val BATTERY_LOW_THRESHOLD = 20
        private const val BATTERY_CRITICAL_THRESHOLD = 10
        
        // Performance levels
        private const val PERFORMANCE_FULL = 1.0f
        private const val PERFORMANCE_REDUCED = 0.7f
        private const val PERFORMANCE_MINIMAL = 0.5f
    }
    
    // Battery state
    private val _batteryState = MutableStateFlow(BatteryState())
    val batteryState: StateFlow<BatteryState> = _batteryState.asStateFlow()
    
    // Wake lock for long generations
    private var wakeLock: PowerManager.WakeLock? = null
    
    // Battery manager
    private val batteryManager: BatteryManager by lazy {
        context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    }
    
    // Power manager
    private val powerManager: PowerManager by lazy {
        context.getSystemService(Context.POWER_SERVICE) as PowerManager
    }
    
    /**
     * Initialize battery monitoring
     */
    fun initialize() {
        updateBatteryState()
        Log.d(TAG, "Battery optimizer initialized")
    }
    
    /**
     * Update battery state
     */
    fun updateBatteryState() {
        val batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        val isCharging = batteryManager.isCharging
        
        val state = BatteryState(
            level = batteryLevel,
            isCharging = isCharging,
            health = "Good",
            performanceLevel = getPerformanceLevel(batteryLevel, isCharging),
            shouldReducePerformance = shouldReducePerformance(batteryLevel, isCharging),
            isCritical = batteryLevel <= BATTERY_CRITICAL_THRESHOLD
        )
        
        _batteryState.value = state
        
        Log.d(TAG, "Battery: $batteryLevel%, Charging: $isCharging, Performance: ${state.performanceLevel}")
    }
    
    /**
     * Get recommended performance level based on battery
     */
    private fun getPerformanceLevel(batteryLevel: Int, isCharging: Boolean): Float {
        return when {
            isCharging -> PERFORMANCE_FULL
            batteryLevel > BATTERY_LOW_THRESHOLD -> PERFORMANCE_FULL
            batteryLevel > BATTERY_CRITICAL_THRESHOLD -> PERFORMANCE_REDUCED
            else -> PERFORMANCE_MINIMAL
        }
    }
    
    /**
     * Check if performance should be reduced
     */
    private fun shouldReducePerformance(batteryLevel: Int, isCharging: Boolean): Boolean {
        return !isCharging && batteryLevel <= BATTERY_LOW_THRESHOLD
    }
    
    /**
     * Get battery health string (REMOVED - not used in stub)
     */
    // Stub implementation doesn't need battery health
    
    /**
     * Acquire wake lock for long generation
     * Prevents CPU from sleeping during inference
     */
    fun acquireWakeLock(timeoutMs: Long = 60_000L) {
        releaseWakeLock()
        
        try {
            wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "Kaede::GenerationLock"
            ).apply {
                setReferenceCounted(false)
                acquire(timeoutMs)
            }
            Log.d(TAG, "Wake lock acquired for ${timeoutMs}ms")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to acquire wake lock", e)
        }
    }
    
    /**
     * Release wake lock
     */
    fun releaseWakeLock() {
        try {
            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                    Log.d(TAG, "Wake lock released")
                }
            }
            wakeLock = null
        } catch (e: Exception) {
            Log.e(TAG, "Failed to release wake lock", e)
        }
    }
    
    /**
     * Get recommended max tokens based on battery
     */
    fun getMaxTokens(): Int {
        return when {
            _batteryState.value.isCharging -> 256
            _batteryState.value.level > 50 -> 128
            _batteryState.value.level > BATTERY_LOW_THRESHOLD -> 96
            else -> 64
        }
    }
    
    /**
     * Get recommended thread count based on battery
     */
    fun getThreadCount(): Int {
        return when {
            _batteryState.value.isCharging -> 8
            _batteryState.value.level > 50 -> 4
            _batteryState.value.level > BATTERY_LOW_THRESHOLD -> 2
            else -> 1
        }
    }
    
    /**
     * Check if device is in power saving mode
     */
    fun isPowerSavingMode(): Boolean {
        return powerManager.isPowerSaveMode
    }
    
    /**
     * Cleanup resources
     */
    fun cleanup() {
        releaseWakeLock()
    }
}

/**
 * Battery state data class
 */
data class BatteryState(
    val level: Int = 0,
    val isCharging: Boolean = false,
    val health: String = "Unknown",
    val performanceLevel: Float = 1.0f,
    val shouldReducePerformance: Boolean = false,
    val isCritical: Boolean = false
) {
    /**
     * Get battery level with color indicator
     */
    fun getBatteryColor(): String {
        return when {
            isCritical -> "red"
            shouldReducePerformance -> "orange"
            isCharging -> "green"
            else -> "normal"
        }
    }
    
    /**
     * Get formatted battery status
     */
    fun getStatusText(): String {
        return buildString {
            append("$level%")
            if (isCharging) append(" ⚡")
            if (shouldReducePerformance) append(" (Power Save)")
        }
    }
}
