package com.example.emotiondj.audio

import androidx.annotation.RawRes
import com.example.emotiondj.R

data class Track(
    @RawRes val resId: Int,
    val title: String,
    val artist: String,
)

object MoodCatalog {
    private val fallback = listOf(Track(R.raw.track_neutral_1, "Neutral Drift", "Placeholder Artist"))

    private val byMood: Map<String, List<Track>> = mapOf(
        "Happy" to listOf(
            Track(R.raw.track_happy_1, "Sunrise", "Placeholder Artist"),
            Track(R.raw.track_happy_2, "Good Day", "Placeholder Artist"),
        ),
        "Sad" to listOf(Track(R.raw.track_sad_1, "Slow Rain", "Placeholder Artist")),
        "Calm" to listOf(Track(R.raw.track_calm_1, "Still Water", "Placeholder Artist")),
        "Focused" to listOf(Track(R.raw.track_focused_1, "Deep Work", "Placeholder Artist")),
        "Tired" to listOf(Track(R.raw.track_tired_1, "Late Hours", "Placeholder Artist")),
        "Neutral" to listOf(Track(R.raw.track_neutral_1, "Neutral Drift", "Placeholder Artist")),
    )

    fun tracksFor(mood: String): List<Track> = byMood[mood] ?: fallback
    fun supportedMoods(): Set<String> = byMood.keys
}
