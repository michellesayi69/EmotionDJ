package com.example.emotiondj.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emotiondj.audio.PlaybackState

@Composable
fun MusicBar(
    state: PlaybackState,
    onToggle: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!state.hasQueue || state.track == null) return

    var dragging by remember { mutableStateOf(false) }
    var dragValue by remember { mutableFloatStateOf(0f) }

    Surface(modifier = modifier.fillMaxWidth(), tonalElevation = 3.dp) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text(state.track.title, style = MaterialTheme.typography.titleSmall, maxLines = 1)
            Text(state.track.artist, style = MaterialTheme.typography.bodySmall, maxLines = 1)

            val sliderValue = if (dragging) dragValue else state.progress
            Slider(
                value = sliderValue,
                onValueChange = { dragging = true; dragValue = it },
                onValueChangeFinished = {
                    onSeek((dragValue * state.durationMs).toLong())
                    dragging = false
                },
            )

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    formatTime(if (dragging) (dragValue * state.durationMs).toLong() else state.positionMs),
                    style = MaterialTheme.typography.bodySmall,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onPrevious) {
                        Text("\u23EE", fontSize = 22.sp)
                    }
                    IconButton(onClick = onToggle) {
                        Text(if (state.isPlaying) "\u23F8" else "\u25B6", fontSize = 22.sp)
                    }
                    IconButton(onClick = onNext) {
                        Text("\u23ED", fontSize = 22.sp)
                    }
                }
                Text(formatTime(state.durationMs), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSec = (ms / 1000).coerceAtLeast(0)
    return "%d:%02d".format(totalSec / 60, totalSec % 60)
}
