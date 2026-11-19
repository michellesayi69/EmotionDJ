package com.example.emotiondj.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MusicPlayer(
    private val context: Context,
    private val scope: CoroutineScope,
) {
    private val audioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()

    private var mediaPlayer: MediaPlayer? = null
    private var queue: List<Track> = emptyList()
    private var index: Int = -1
    private var progressJob: Job? = null
    private var resumeOnFocusGain: Boolean = false
    private var focusRequest: AudioFocusRequest? = null

    private val _state = MutableStateFlow(PlaybackState())
    val state: StateFlow<PlaybackState> = _state.asStateFlow()

    private val focusListener = AudioManager.OnAudioFocusChangeListener { change ->
        when (change) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                resumeOnFocusGain = false
                pause()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                resumeOnFocusGain = _state.value.isPlaying
                pause()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> setVolume(0.2f)
            AudioManager.AUDIOFOCUS_GAIN -> {
                setVolume(1f)
                if (resumeOnFocusGain) { resumeOnFocusGain = false; resume() }
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun requestFocus(): Boolean {
        val granted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val req = focusRequest ?: AudioFocusRequest
                .Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(audioAttributes)
                .setOnAudioFocusChangeListener(focusListener)
                .build()
                .also { focusRequest = it }
            audioManager.requestAudioFocus(req)
        } else {
            audioManager.requestAudioFocus(
                focusListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN,
            )
        }
        return granted == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    @Suppress("DEPRECATION")
    private fun abandonFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            focusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
        } else {
            audioManager.abandonAudioFocus(focusListener)
        }
    }

    fun playMood(mood: String, autoPlay: Boolean = true) {
        val tracks = MoodCatalog.tracksFor(mood)
        if (tracks.isEmpty()) return
        if (tracks == queue && mediaPlayer != null) {
            if (autoPlay && !_state.value.isPlaying) resume()
            return
        }
        queue = tracks
        _state.update { it.copy(queueSize = tracks.size) }
        loadTrack(0, autoPlay)
    }

    fun togglePlayPause() {
        if (_state.value.isPlaying) pause() else resume()
    }

    fun resume() {
        val mp = mediaPlayer ?: return
        if (!requestFocus()) return
        runCatching { mp.start() }
        _state.update { it.copy(isPlaying = true, durationMs = safeDuration()) }
        startTicker()
    }

    fun pause() {
        runCatching { mediaPlayer?.takeIf { it.isPlaying }?.pause() }
        _state.update { it.copy(isPlaying = false) }
        progressJob?.cancel()
        progressJob = null
        abandonFocus()
    }

    fun skipNext() {
        if (queue.isEmpty()) return
        val next = if (index < queue.lastIndex) index + 1 else 0
        loadTrack(next, autoPlay = _state.value.isPlaying)
    }

    fun skipPrevious() {
        if (queue.isEmpty()) return
        if (_state.value.positionMs > 3_000L) { seekTo(0); return }
        val prev = if (index > 0) index - 1 else queue.lastIndex
        loadTrack(prev, autoPlay = _state.value.isPlaying)
    }

    fun seekTo(ms: Long) {
        val mp = mediaPlayer ?: return
        val clamped = ms.coerceIn(0L, safeDuration().coerceAtLeast(0L))
        runCatching { mp.seekTo(clamped.toInt()) }
        _state.update { it.copy(positionMs = clamped) }
    }

    fun release() {
        progressJob?.cancel()
        abandonFocus()
        mediaPlayer?.release()
        mediaPlayer = null
        _state.value = PlaybackState()
    }

    private fun loadTrack(i: Int, autoPlay: Boolean) {
        if (queue.isEmpty()) return
        index = i.coerceIn(0, queue.lastIndex)
        val track = queue[index]

        mediaPlayer?.release()
        mediaPlayer = null
        progressJob?.cancel()

        val mp = MediaPlayer()
        try {
            context.resources.openRawResourceFd(track.resId).use { afd ->
                mp.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            }
        } catch (e: Exception) {
            mp.release()
            _state.update { it.copy(isPlaying = false) }
            return
        }

        mp.setAudioAttributes(audioAttributes)
        mp.setOnPreparedListener { if (autoPlay) resume() }
        mp.setOnCompletionListener {
            if (index < queue.lastIndex) {
                loadTrack(index + 1, autoPlay = true)
            } else {
                _state.update { it.copy(isPlaying = false, positionMs = 0L) }
                progressJob?.cancel()
            }
        }
        mp.setOnErrorListener { _, _, _ ->
            _state.update { it.copy(isPlaying = false) }
            true
        }
        mp.prepareAsync()
        mediaPlayer = mp

        _state.update {
            it.copy(
                track = track,
                indexInQueue = index,
                positionMs = 0L,
                durationMs = 0L,
                isPlaying = false,
            )
        }
    }

    private fun safeDuration(): Long =
        runCatching { mediaPlayer?.duration?.toLong() ?: 0L }.getOrDefault(0L)

    private fun setVolume(v: Float) {
        runCatching { mediaPlayer?.setVolume(v, v) }
    }

    private fun startTicker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                val mp = mediaPlayer
                if (mp != null && _state.value.isPlaying) {
                    val pos = runCatching { mp.currentPosition.toLong() }.getOrDefault(0L)
                    val dur = runCatching { mp.duration.toLong() }.getOrDefault(0L)
                    _state.update { it.copy(positionMs = pos, durationMs = dur) }
                }
                delay(500)
            }
        }
    }
}
