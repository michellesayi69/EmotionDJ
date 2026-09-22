package com.example.emotiondj.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun PlaylistCard(
    mood: String,
    playlistUrl: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = {
            if (playlistUrl.isBlank() || !playlistUrl.startsWith("https://open.spotify.com/")) {
                Toast.makeText(context, "Invalid playlist link", Toast.LENGTH_SHORT).show()
                return@Card
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(playlistUrl))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "Spotify not installed? Open manually.", Toast.LENGTH_SHORT).show()
            }
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = mood,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Open in Spotify →",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}