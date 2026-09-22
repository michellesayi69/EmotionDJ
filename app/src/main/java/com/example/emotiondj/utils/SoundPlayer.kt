package com.example.emotiondj.utils

import android.content.Context
import android.media.MediaPlayer
import android.provider.Settings

object SoundPlayer {
    private var mediaPlayer: MediaPlayer? = null

    fun playMoodChangeSound(context: Context) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, Settings.System.DEFAULT_NOTIFICATION_URI)
        mediaPlayer?.start()
        mediaPlayer?.setOnCompletionListener { it.release() }
    }
}