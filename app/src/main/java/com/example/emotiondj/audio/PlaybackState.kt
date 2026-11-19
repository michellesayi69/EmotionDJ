package com.example.emotiondj.audio

data class PlaybackState(
    val track: Track? = null,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val queueSize: Int = 0,
    val indexInQueue: Int = -1,
) {
    val hasQueue: Boolean get() = queueSize > 0
    val progress: Float
        get() = if (durationMs > 0) (positionMs.toFloat() / durationMs).coerceIn(0f, 1f) else 0f
}
