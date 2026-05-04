package com.kaede.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Kaede Database - Main Room database for all persistent storage
 * 
 * Version 1: Initial schema with memories and user_preferences tables
 */
@Database(
    entities = [
        MemoryEntity::class,
        UserPreferencesEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class KaedeDatabase : RoomDatabase() {
    
    /**
     * Get Memory DAO for database operations
     */
    abstract fun memoryDao(): MemoryDao
    
    /**
     * Get User Preferences DAO
     */
    abstract fun userPreferencesDao(): UserPreferencesDao
    
    companion object {
        private const val DATABASE_NAME = "kaede_database"
        
        @Volatile
        private var INSTANCE: KaedeDatabase? = null
        
        /**
         * Get singleton database instance
         * 
         * @param context Android application context
         * @return KaedeDatabase instance
         */
        fun getDatabase(context: Context): KaedeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KaedeDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
        
        /**
         * Close database and clear instance
         * Call only when shutting down application
         */
        fun closeDatabase() {
            INSTANCE?.close()
            INSTANCE = null
        }
    }
}

/**
 * Type converters for Room database
 */
class Converters {
    
    @androidx.room.TypeConverter
    fun fromMemoryType(value: MemoryType): String {
        return value.toString()
    }
    
    @androidx.room.TypeConverter
    fun toMemoryType(value: String): MemoryType {
        return value.toMemoryType()
    }
}
