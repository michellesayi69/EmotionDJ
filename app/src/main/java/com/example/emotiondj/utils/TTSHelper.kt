package com.example.emotiondj.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale

object TTSHelper {
    private var tts: TextToSpeech? = null
    private var isReady = false

    fun init(context: Context, onReady: () -> Unit = {}) {
        if (tts != null) {
            if (isReady) onReady()
            return
        }
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTSHelper", "Language not supported")
                    isReady = false
                } else {
                    isReady = true
                    Log.d("TTSHelper", "TTS ready")
                    onReady()
                }
            } else {
                Log.e("TTSHelper", "Init failed")
                isReady = false
            }
        }
    }

    fun speakMoodChange(mood: String, context: Context) {
        if (!isReady) {
            init(context) { speakMoodChange(mood, context) }
            return
        }
        val message = when (mood.lowercase()) {
            "happy" -> "You seem happy. Playing your happy mix."
            "sad" -> "You seem sad. Playing some comforting songs."
            "tired" -> "You seem tired. Here's a relaxing playlist."
            "neutral" -> "Neutral mood detected. How about some chill music?"
            "focused" -> "You seem focused. Playing deep work beats."
            "calm" -> "You seem calm. Enjoy this peaceful mix."
            else -> "Mood updated to $mood."
        }
        tts?.speak(message, TextToSpeech.QUEUE_FLUSH, null, "mood_${System.currentTimeMillis()}")
    }

    fun speakWithCallback(context: Context, text: String, onComplete: () -> Unit) {
        if (!isReady) {
            init(context) { speakWithCallback(context, text, onComplete) }
            return
        }
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) { onComplete() }
            override fun onError(utteranceId: String?) { onComplete() }
        })
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "callback_${System.currentTimeMillis()}")
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isReady = false
    }
}