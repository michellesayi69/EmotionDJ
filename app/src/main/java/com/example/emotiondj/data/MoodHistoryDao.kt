package com.example.emotiondj.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodHistoryDao {
    @Insert
    suspend fun insert(moodEntry: MoodEntry)

    @Query("SELECT * FROM mood_history ORDER BY timestamp DESC")
    fun getAllMoods(): Flow<List<MoodEntry>>

    @Query("DELETE FROM mood_history")
    suspend fun deleteAll()
}