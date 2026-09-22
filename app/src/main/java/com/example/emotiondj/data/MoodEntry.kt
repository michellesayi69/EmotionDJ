package com.example.emotiondj.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "mood_history")
data class MoodEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mood: String,
    val timestamp: Date,
    val playlistName: String   // e.g., "Happy"
)