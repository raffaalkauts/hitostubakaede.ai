package com.kaede.app.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * User Preferences DAO - Database access object for user settings
 */
@Dao
interface UserPreferencesDao {
    
    // ==================== READ OPERATIONS ====================
    
    /**
     * Get all preferences as Flow
     */
    @Query("SELECT * FROM user_preferences ORDER BY timestamp DESC")
    fun getAllPreferences(): Flow<List<UserPreferencesEntity>>
    
    /**
     * Get preference by key
     */
    @Query("SELECT * FROM user_preferences WHERE key = :key LIMIT 1")
    suspend fun getPreferenceByKey(key: String): UserPreferencesEntity?
    
    /**
     * Get preference value by key
     */
    @Query("SELECT value FROM user_preferences WHERE key = :key LIMIT 1")
    suspend fun getPreferenceValue(key: String): String?
    
    /**
     * Get preference as Flow
     */
    @Query("SELECT * FROM user_preferences WHERE key = :key")
    fun getPreferenceFlow(key: String): Flow<UserPreferencesEntity?>
    
    /**
     * Check if preference exists
     */
    @Query("SELECT EXISTS(SELECT 1 FROM user_preferences WHERE key = :key)")
    suspend fun hasPreference(key: String): Boolean
    
    // ==================== INSERT/UPDATE OPERATIONS ====================
    
    /**
     * Insert or update preference
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreference(preferences: UserPreferencesEntity)
    
    /**
     * Insert or update multiple preferences
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreferences(vararg preferences: UserPreferencesEntity)

    /**
     * Set preference value (convenience method)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setPreference(preferences: UserPreferencesEntity)
    
    /**
     * Update preference
     */
    @Update
    suspend fun updatePreference(preferences: UserPreferencesEntity)
    
    // ==================== DELETE OPERATIONS ====================
    
    /**
     * Delete preference by key
     */
    @Query("DELETE FROM user_preferences WHERE key = :key")
    suspend fun deletePreferenceByKey(key: String)
    
    /**
     * Delete preference entity
     */
    @Delete
    suspend fun deletePreference(preferences: UserPreferencesEntity)
    
    /**
     * Delete all preferences
     */
    @Query("DELETE FROM user_preferences")
    suspend fun deleteAllPreferences()
    
    // ==================== UTILITY OPERATIONS ====================
    
    /**
     * Get all preference keys
     */
    @Query("SELECT key FROM user_preferences")
    suspend fun getAllKeys(): List<String>
    
    /**
     * Count total preferences
     */
    @Query("SELECT COUNT(*) FROM user_preferences")
    suspend fun getPreferenceCount(): Int
}
