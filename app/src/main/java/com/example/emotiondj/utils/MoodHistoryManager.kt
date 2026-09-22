package com.example.emotiondj.utils

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class MoodRecord(val mood: String, val timestamp: Long, val playlist: String)

val Context.dataStore by preferencesDataStore(name = "mood_history")

class MoodHistoryManager(private val context: Context) {
    private val gson = Gson()
    private val moodHistoryKey = stringPreferencesKey("mood_history")
    private val MAX_HISTORY_SIZE = 100

    init {
        CoroutineScope(Dispatchers.IO).launch { trimHistoryIfNeeded() }
    }

    private suspend fun trimHistoryIfNeeded() {
        val current = getMoodList()
        if (current.size > MAX_HISTORY_SIZE) {
            val trimmed = current.takeLast(MAX_HISTORY_SIZE)
            val json = gson.toJson(trimmed)
            context.dataStore.edit { prefs -> prefs[moodHistoryKey] = json }
        }
    }

    suspend fun addMood(mood: String, playlist: String) {
        val currentList = getMoodList()
        val newRecord = MoodRecord(mood, System.currentTimeMillis(), playlist)
        val updatedList = (currentList + newRecord).takeLast(MAX_HISTORY_SIZE)
        val json = gson.toJson(updatedList)
        context.dataStore.edit { prefs -> prefs[moodHistoryKey] = json }
    }

    fun getAllMoods(): Flow<List<MoodRecord>> = context.dataStore.data.map { prefs ->
        val json = prefs[moodHistoryKey] ?: "[]"
        val type = object : TypeToken<List<MoodRecord>>() {}.type
        gson.fromJson(json, type)
    }

    suspend fun clearHistory() {
        context.dataStore.edit { prefs -> prefs.remove(moodHistoryKey) }
    }

    private suspend fun getMoodList(): List<MoodRecord> = getAllMoods().first()
}