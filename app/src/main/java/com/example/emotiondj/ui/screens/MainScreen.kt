package com.example.emotiondj.ui.screens

import android.Manifest
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.emotiondj.ui.components.MusicBar
import com.example.emotiondj.ui.components.OfflineWarningBanner
import com.example.emotiondj.utils.*
import com.example.emotiondj.viewmodel.MainViewModel
import com.google.accompanist.permissions.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val currentMood by viewModel.currentMood.collectAsState()
    val currentConfidence by viewModel.currentConfidence.collectAsState()
    var isConnected by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    // Network monitoring
    LaunchedEffect(Unit) {
        ConnectivityHelper.observeNetwork(context).collectLatest { connected ->
            isConnected = connected
        }
    }

    // Request camera permission
    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    // Voice launcher (for optional artist input)
    val voiceLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = matches?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                Toast.makeText(context, "Heard: $spokenText", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Artist prompt dialog
    val needsArtistState by viewModel.needsArtist.collectAsState()
    if (needsArtistState != null) {
        val (mood, _) = needsArtistState!!
        var artistInput by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { viewModel.clearArtistPrompt() },
            title = { Text("Favorite Artist for $mood") },
            text = {
                Column {
                    Text("Please type your favorite artist to listen to when you feel $mood.")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = artistInput,
                        onValueChange = { artistInput = it },
                        label = { Text("Artist name") }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (artistInput.isNotBlank()) {
                            scope.launch {
                                viewModel.saveArtistAndOpenPlaylist(mood, artistInput)
                            }
                        }
                    }
                ) { Text("Save & Open") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.clearArtistPrompt() }) { Text("Skip") }
            }
        )
    }

    // Pulse animation for mood badge
    var isPulsing by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPulsing) 1.05f else 1f,
        animationSpec = tween(150, easing = FastOutSlowInEasing)
    )
    LaunchedEffect(currentMood) {
        isPulsing = true
        delay(150)
        isPulsing = false
    }

    // Animated gradient background based on mood
    val gradientColors = when (currentMood) {
        "Happy" -> listOf(Color(0xFFFFF176), Color(0xFFFFB74D))
        "Sad" -> listOf(Color(0xFF90CAF9), Color(0xFF42A5F5))
        "Tired" -> listOf(Color(0xFFBCAAA4), Color(0xFF8D6E63))
        "Neutral" -> listOf(Color(0xFFCFD8DC), Color(0xFF90A4AE))
        "Focused" -> listOf(Color(0xFFA5D6A7), Color(0xFF4CAF50))
        "Calm" -> listOf(Color(0xFF80DEEA), Color(0xFF26C6DA))
        else -> listOf(Color(0xFF1A1A3A), Color(0xFF0A0A2A))
    }
    val animatedGradient = Brush.verticalGradient(
        colors = gradientColors,
        startY = 0f,
        endY = Float.POSITIVE_INFINITY
    )

    val moodColor = when (currentMood) {
        "Happy" -> Color(0xFFFFD54F)
        "Sad" -> Color(0xFF64B5F6)
        "Tired" -> Color(0xFFA1887F)
        "Neutral" -> Color(0xFFB0BEC5)
        "Focused" -> Color(0xFF81C784)
        "Calm" -> Color(0xFF80DEEA)
        else -> MaterialTheme.colorScheme.primaryContainer
    }
    val moodEmoji = when (currentMood) {
        "Happy" -> "😊"
        "Sad" -> "😢"
        "Tired" -> "😴"
        "Neutral" -> "😐"
        "Focused" -> "🎯"
        "Calm" -> "😌"
        else -> "🎵"
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(animatedGradient)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Offline banner
            OfflineWarningBanner(isConnected = isConnected)

            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onNavigateToHistory) { Text("History") }
                TextButton(onClick = onNavigateToSettings) { Text("Settings") }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Camera preview
            when {
                cameraPermissionState.status.isGranted -> {
                    val previewView = remember { PreviewView(context) }
                    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
                    DisposableEffect(Unit) {
                        val helper = CameraHelper(context, previewView) { mood, confidence ->
                            viewModel.updateMood(mood, confidence)
                        }
                        helper.startCamera(lifecycleOwner)
                        onDispose { helper.stopCamera() }
                    }
                    AndroidView(
                        factory = { previewView },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                }
                cameraPermissionState.status.shouldShowRationale -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Camera permission is needed to detect your mood.")
                        Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                            Text("Grant Permission")
                        }
                    }
                }
                else -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Camera permission denied. Please enable it in settings.")
                        Button(onClick = {
                            context.startActivity(android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = android.net.Uri.parse("package:${context.packageName}")
                            })
                        }) {
                            Text("Open Settings")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Mood badge with pulse
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(scale)
                    .shadow(12.dp, shape = RoundedCornerShape(20.dp))
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = moodColor),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = moodEmoji, fontSize = 48.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = currentMood,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentMood == "Happy") Color.Black else Color.White
                            )
                            Text(
                                text = "${(currentConfidence * 100).toInt()}% confident",
                                fontSize = 14.sp,
                                color = if (currentMood == "Happy") Color.Black.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!isConnected) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text("No internet connection. Playlist unavailable.", modifier = Modifier.padding(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val playback by viewModel.playback.collectAsState()
            MusicBar(
                state = playback,
                onToggle = viewModel::togglePlayback,
                onNext = viewModel::nextTrack,
                onPrevious = viewModel::previousTrack,
                onSeek = viewModel::seekTo,
            )
        }

        // Floating voice button (optional)
        FloatingActionButton(
            onClick = {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Say your favorite artist")
                }
                voiceLauncher.launch(intent)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.secondary
        ) {
            Text("🎤", fontSize = 24.sp)
        }
    }
}
