package com.example.emotiondj.utils

import android.content.Intent
import android.speech.RecognizerIntent
import java.util.Locale

object VoiceHelper {
    fun getVoiceIntent(): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Say your mood (happy, sad, etc.)")
        }
    }

    fun parseMoodFromText(text: String): String? {
        val lower = text.lowercase()
        return when {
            lower.contains("happy") -> "Happy"
            lower.contains("sad") -> "Sad"
            lower.contains("tired") -> "Tired"
            lower.contains("neutral") -> "Neutral"
            lower.contains("focus") || lower.contains("concentrate") -> "Focused"
            lower.contains("calm") || lower.contains("relax") -> "Calm"
            else -> null
        }
    }
}