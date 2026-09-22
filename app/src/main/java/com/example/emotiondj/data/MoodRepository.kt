package com.example.emotiondj.data.repository

import android.content.Context
import com.example.emotiondj.data.local.MoodEntry
import com.example.emotiondj.data.local.MoodHistoryDatabase
import kotlinx.coroutines.flow.Flow

class MoodRepository(context: Context) {
    private val dao = MoodHistoryDatabase.getDatabase(context).moodHistoryDao()

    suspend fun saveMood(mood: String, playlistName: String) {
        val entry = MoodEntry(
            mood = mood,
            timestamp = java.util.Date(),
            playlistName = playlistName
        )
        dao.insert(entry)
    }

    fun getAllMoods(): Flow<List<MoodEntry>> = dao.getAllMoods()

    suspend fun clearHistory() = dao.deleteAll()
}