package com.example.emotiondj.utils

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.artistDataStore by preferencesDataStore(name = "artist_preferences")

class ArtistPreferenceManager(private val context: Context) {
    private val dataStore = context.artistDataStore

    suspend fun saveArtist(mood: String, artist: String) {
        val key = stringPreferencesKey(mood.uppercase())
        dataStore.edit { preferences ->
            preferences[key] = artist
        }
    }

    fun getArtist(mood: String): Flow<String?> {
        val key = stringPreferencesKey(mood.uppercase())
        return dataStore.data.map { preferences ->
            preferences[key]
        }
    }

    suspend fun hasArtist(mood: String): Boolean {
        val key = stringPreferencesKey(mood.uppercase())
        val preferences = dataStore.data.first()
        return preferences.contains(key)
    }
}