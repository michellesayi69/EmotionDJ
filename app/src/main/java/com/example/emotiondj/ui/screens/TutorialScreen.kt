package com.example.emotiondj.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TutorialScreen(
    onGetStarted: () -> Unit,
    onSkip: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val arrowRotation by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome to EmotionDJ", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Text("How to use:", fontSize = 18.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("①", fontSize = 24.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Text("Look at the front camera")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("②", fontSize = 24.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Text("The app reads your expression")
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = painterResource(id = android.R.drawable.arrow_up_float),
                contentDescription = "arrow",
                modifier = Modifier
                    .size(24.dp)
                    .rotate(arrowRotation)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("③", fontSize = 24.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Text("A playlist opens automatically")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("④", fontSize = 24.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Text("The whole app changes with your mood")
        }

        Spacer(modifier = Modifier.height(32.dp))
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A3A))
        ) {
            Text(
                "Camera data never leaves your device.\nNo recording, no sharing.",
                modifier = Modifier.padding(16.dp),
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = onGetStarted) { Text("Get Started") }
            TextButton(onClick = onSkip) { Text("Skip") }
        }
    }
}