package com.kaede.app.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * User Preferences Entity - Stores user settings and profile
 */
@Entity(
    tableName = "user_preferences",
    indices = [
        Index(value = ["key"], unique = true, name = "index_preferences_key")
    ]
)
data class UserPreferencesEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "key")
    val key: String,
    
    @ColumnInfo(name = "value")
    val value: String,
    
    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "is_encrypted", defaultValue = "0")
    val isEncrypted: Boolean = false
)

/**
 * Known preference keys
 */
object PreferenceKeys {
    const val USER_NAME = "user_name"
    const val USER_NICKNAME = "user_nickname"
    const val GREETING_PREFERENCE = "greeting_preference"
    const val LANGUAGE = "language"
    const val THEME = "theme"
    const val NOTIFICATION_ENABLED = "notification_enabled"
    const val LAST_ACTIVE = "last_active"
    const val CONVERSATION_COUNT = "conversation_count"
    const val FAVORITE_TOPIC = "favorite_topic"
}
