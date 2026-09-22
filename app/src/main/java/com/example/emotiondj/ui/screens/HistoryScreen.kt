package com.example.emotiondj.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.emotiondj.utils.MoodRecord
import com.example.emotiondj.viewmodel.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    viewModel: HistoryViewModel = viewModel()
) {
    val moods by viewModel.moods.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) { Text("← Back") }
            Text("Mood History", style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = { viewModel.clearHistory() }) { Text("Clear") }
        }

        Divider()

        if (moods.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No moods logged yet.\nUse the app to start.")
            }
        } else {
            LazyColumn {
                items(moods) { record ->
                    MoodHistoryItem(record)
                }
            }
        }
    }
}

@Composable
fun MoodHistoryItem(record: MoodRecord) {
    val date = Date(record.timestamp)
    val formatter = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    val moodEmoji = when (record.mood) {
        "Happy" -> "😊"
        "Sad" -> "😢"
        "Tired" -> "😴"
        "Neutral" -> "😐"
        "Focused" -> "🎯"
        "Calm" -> "😌"
        else -> "🎵"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "$moodEmoji ${record.mood}", style = MaterialTheme.typography.titleMedium)
                Text(text = formatter.format(date), style = MaterialTheme.typography.bodySmall)
            }
            Text(text = "Playlist: ${record.playlist}", style = MaterialTheme.typography.bodySmall)
        }
    }
}