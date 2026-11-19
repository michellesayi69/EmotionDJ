package com.example.emotiondj.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.emotiondj.audio.MusicPlayer
import com.example.emotiondj.audio.PlaybackState
import com.example.emotiondj.utils.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application
    private val historyManager = MoodHistoryManager(application)
    private val artistPrefManager = ArtistPreferenceManager(application)
    private val player = MusicPlayer(application, viewModelScope)

    /** Playback state for the UI (MusicBar). */
    val playback: StateFlow<PlaybackState> = player.state

    private val _currentMood = MutableStateFlow("Neutral")
    val currentMood = _currentMood.asStateFlow()

    private val _currentConfidence = MutableStateFlow(0.5f)
    val currentConfidence = _currentConfidence.asStateFlow()

    // Artist prompt flow kept for optional manual use (see promptForArtistForCurrentMood).
    private val _needsArtist = MutableStateFlow<Pair<String, Boolean>?>(null)
    val needsArtist = _needsArtist.asStateFlow()

    fun updateMood(newMood: String, confidence: Float) {
        viewModelScope.launch {
            if (_needsArtist.value != null) return@launch

            val oldMood = _currentMood.value
            if (oldMood == newMood) return@launch

            SoundPlayer.playMoodChangeSound(app)
            HapticHelper.vibrateShort(app)
            NotificationHelper.updateNotification(app, newMood)
            TTSHelper.speakMoodChange(newMood, app)

            _currentMood.value = newMood
            _currentConfidence.value = confidence

            try {
                historyManager.addMood(newMood, newMood)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to save mood", e)
            }

            // Local, mood-driven playback (primary music path).
            player.playMood(newMood)
        }
    }

    // --- Manual Spotify hand-off (not auto-triggered) ---

    fun openSpotifyForCurrentMood() {
        viewModelScope.launch {
            val mood = _currentMood.value
            val artist = artistPrefManager.getArtist(mood).first().orEmpty()
            if (artist.isBlank()) {
                promptForArtistForCurrentMood()
                return@launch
            }
            openSpotifySearch(mood, artist)
            player.pause()
        }
    }

    /** Manual trigger for the artist-setup TTS prompt (e.g. from a settings button). */
    fun promptForArtistForCurrentMood() {
        viewModelScope.launch {
            val mood = _currentMood.value
            if (!artistPrefManager.hasArtist(mood)) promptForArtist(mood)
        }
    }

    private suspend fun promptForArtist(mood: String) {
        _needsArtist.emit(Pair(mood, true))
        TTSHelper.speakWithCallback(
            app,
            "Who is your favorite artist to listen to when you are $mood?",
        ) {}
    }

    suspend fun saveArtistAndOpenPlaylist(mood: String, artist: String) {
        artistPrefManager.saveArtist(mood, artist)
        openSpotifySearch(mood, artist)
        _needsArtist.emit(null)
    }

    fun clearArtistPrompt() {
        viewModelScope.launch { _needsArtist.emit(null) }
    }

    private fun openSpotifySearch(mood: String, artist: String) {
        val query = "$artist $mood songs".replace(" ", "%20")
        val url = "https://open.spotify.com/search/$query/tracks"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            app.startActivity(intent)
        } catch (e: Exception) {
            Log.e("MainViewModel", "Failed to open Spotify", e)
        }
    }

    // --- Playback controls surfaced to the UI ---

    fun togglePlayback() = player.togglePlayPause()
    fun nextTrack() = player.skipNext()
    fun previousTrack() = player.skipPrevious()
    fun seekTo(ms: Long) = player.seekTo(ms)

    fun clearHistory() {
        viewModelScope.launch { historyManager.clearHistory() }
    }

    fun getAllMoods() = historyManager.getAllMoods()

    override fun onCleared() {
        player.release()
        super.onCleared()
    }
}
