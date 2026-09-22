package com.example.emotiondj.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MoodBadge(
    currentMood: String,
    currentConfidence: Float,
    moodColor: Color,
    scale: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(12.dp, shape = RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = moodColor),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when (currentMood) {
                    "Happy" -> "😊"
                    "Sad" -> "😢"
                    "Tired" -> "😴"
                    "Neutral" -> "😐"
                    "Focused" -> "🎯"
                    "Calm" -> "😌"
                    else -> "🎵"
                },
                fontSize = 36.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = currentMood,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (currentMood == "Happy") Color.Black else Color.White
                )
                Text(
                    text = "${(currentConfidence * 100).toInt()}% confident",
                    fontSize = 12.sp,
                    color = if (currentMood == "Happy") Color.Black.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}
