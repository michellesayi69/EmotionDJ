package com.example.emotiondj.utils

import com.google.mlkit.vision.face.Face

object MoodDetector {
    private var lastMood = "Neutral"
    private var lastUpdateTime = 0L
    private val THROTTLE_MS = 5000L

    fun detectMood(face: Face): Pair<String, Float> {
        val now = System.currentTimeMillis()
        if (now - lastUpdateTime < THROTTLE_MS) {
            return Pair(lastMood, 0.5f)
        }
        val smileProb = face.smilingProbability ?: 0.0f
        val leftEyeOpen = face.leftEyeOpenProbability ?: 0.5f
        val rightEyeOpen = face.rightEyeOpenProbability ?: 0.5f
        val eyeOpenAvg = (leftEyeOpen + rightEyeOpen) / 2

        val mood = when {
            smileProb > 0.7f -> "Happy"
            smileProb < 0.2f && eyeOpenAvg < 0.4f -> "Tired"
            smileProb < 0.2f && eyeOpenAvg > 0.5f -> "Sad"
            smileProb < 0.3f && eyeOpenAvg > 0.7f -> "Focused"
            smileProb in 0.2f..0.4f && eyeOpenAvg in 0.5f..0.7f -> "Calm"
            else -> "Neutral"
        }
        val confidence = when (mood) {
            "Happy" -> smileProb
            "Tired" -> (1f - eyeOpenAvg).coerceIn(0f, 1f)
            "Sad" -> (1f - smileProb).coerceIn(0f, 1f)
            "Focused" -> eyeOpenAvg
            "Calm" -> (0.5f + (0.5f - kotlin.math.abs(smileProb - 0.3f))).coerceIn(0.5f, 0.9f)
            else -> 0.6f
        }.coerceIn(0f, 1f)

        lastMood = mood
        lastUpdateTime = now
        return Pair(mood, confidence)
    }
}