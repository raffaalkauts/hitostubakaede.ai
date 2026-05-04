package com.kaede.app

import android.app.Application
import android.util.Log
import com.kaede.app.ai.MlcLlm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Kaede Application - Application lifecycle management
 */
class KaedeApplication : Application() {
    
    companion object {
        private const val TAG = "KaedeApplication"
        
        // Application scope for background tasks
        val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
    
    override fun onCreate() {
        super.onCreate()
        
        Log.d(TAG, "Kaede Application starting...")
        Log.d(TAG, "MLC-LLM Engine initialized")
        
        Log.d(TAG, "Kaede Application started")
    }
    
    override fun onTerminate() {
        super.onTerminate()
        
        Log.d(TAG, "Kaede Application terminating...")
    }
    
    override fun onLowMemory() {
        super.onLowMemory()
        
        Log.w(TAG, "Low memory warning")
    }
}
