package com.example.emotiondj.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.emotiondj.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onReplayTutorial: () -> Unit,
    settingsViewModel: SettingsViewModel
) {
    val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) { Text("← Back") }
            Text("Settings", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.width(48.dp))
        }
        var sensitivity by remember { mutableStateOf(1.0f) }
        Text("Sensitivity: ${"%.1f".format(sensitivity)}")
        Slider(
            value = sensitivity,
            onValueChange = { sensitivity = it },
            valueRange = 0.5f..1.5f
        )
// Save to DataStore and pass to MoodDetector (you'll need to inject into CameraHelper)

        Spacer(modifier = Modifier.height(24.dp))

        // Appearance section
        Text("Appearance", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Dark Mode")
            Switch(
                checked = isDarkTheme,
                onCheckedChange = { settingsViewModel.toggleTheme() }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // About section
        Text("About", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = onReplayTutorial
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Replay Tutorial")
                Text("→")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("EmotionDJ v1.0")
                Text("Created by Michelle Sandiswa Sayi")
                Text("© 2026 – All rights reserved.")
                Text("For educational use only.")
            }
        }
    }
}