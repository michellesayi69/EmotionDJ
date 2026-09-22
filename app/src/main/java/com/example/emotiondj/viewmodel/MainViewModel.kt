package com.example.emotiondj.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.emotiondj.utils.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val historyManager = MoodHistoryManager(application)
    private val artistPrefManager = ArtistPreferenceManager(application)

    private val _currentMood = MutableStateFlow("Neutral")
    val currentMood = _currentMood.asStateFlow()

    private val _currentConfidence = MutableStateFlow(0.5f)
    val currentConfidence = _currentConfidence.asStateFlow()

    private val _needsArtist = MutableStateFlow<Pair<String, Boolean>?>(null)
    val needsArtist = _needsArtist.asStateFlow()

    fun updateMood(newMood: String, confidence: Float) {
        viewModelScope.launch {
            if (_needsArtist.value != null) return@launch

            val oldMood = _currentMood.value
            if (oldMood != newMood) {
                SoundPlayer.playMoodChangeSound(getApplication())
                HapticHelper.vibrateShort(getApplication())
                NotificationHelper.updateNotification(getApplication(), newMood)
                TTSHelper.speakMoodChange(newMood, getApplication())

                _currentMood.value = newMood
                _currentConfidence.value = confidence

                try {
                    historyManager.addMood(newMood, newMood)
                } catch (e: Exception) {
                    Log.e("MainViewModel", "Failed to save mood", e)
                }

                val hasArtist = artistPrefManager.hasArtist(newMood)
                if (hasArtist) {
                    val artist = artistPrefManager.getArtist(newMood).first()
                    if (!artist.isNullOrBlank()) {
                        openSpotifySearch(newMood, artist)
                    } else {
                        promptForArtist(newMood)
                    }
                } else {
                    promptForArtist(newMood)
                }
            }
        }
    }

    private suspend fun promptForArtist(mood: String) {
        _needsArtist.emit(Pair(mood, true))
        TTSHelper.speakWithCallback(getApplication(), "Who is your favorite artist to listen to when you are $mood?") {}
    }

    suspend fun saveArtistAndOpenPlaylist(mood: String, artist: String) {
        artistPrefManager.saveArtist(mood, artist)
        openSpotifySearch(mood, artist)
        _needsArtist.emit(null)
    }

    fun clearArtistPrompt() {
        viewModelScope.launch {
            _needsArtist.emit(null)
        }
    }

    private fun openSpotifySearch(mood: String, artist: String) {
        val query = "$artist $mood songs".replace(" ", "%20")
        val url = "https://open.spotify.com/search/$query/tracks"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            getApplication<Application>().startActivity(intent)
        } catch (e: Exception) {
            Log.e("MainViewModel", "Failed to open Spotify", e)
        }
    }

    fun clearHistory() {
        viewModelScope.launch { historyManager.clearHistory() }
    }

    fun getAllMoods() = historyManager.getAllMoods()
}