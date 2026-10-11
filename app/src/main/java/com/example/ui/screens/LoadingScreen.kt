package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(
    statusText: String,
    progress: Float,
    isInitialSplash: Boolean = false,
    onSplashComplete: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    if (isInitialSplash) {
        LaunchedEffect(Unit) {
            delay(1200)
            onSplashComplete()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF0A0F1D)
                    )
                )
            )
            .testTag("app_loading_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // Glowing Logo Badge
            Box(
                modifier = Modifier
                    .scale(pulseScale)
                    .size(96.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF06B6D4), Color(0xFF3B82F6), Color(0xFF8B5CF6))
                        )
                    )
                    .padding(3.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(25.dp))
                        .background(Color(0xFF0F172A)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "</>",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF38BDF8)
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            Text(
                text = "WebForge Studio",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(Modifier.height(6.dp))

            // Made with Azuki Pill
            Surface(
                shape = CircleShape,
                color = Color(0xFF1E293B),
                modifier = Modifier.border(1.dp, Color(0xFF334155), CircleShape)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.size(6.dp))
                    Text(
                        text = "Made with Azuki",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFF1F5F9)
                    )
                }
            }

            Spacer(Modifier.height(36.dp))

            // Animated Linear Progress
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                if (progress > 0f) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(6.dp),
                        color = Color(0xFF06B6D4),
                        trackColor = Color(0xFF1E293B)
                    )
                } else {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().height(6.dp),
                        color = Color(0xFF06B6D4),
                        trackColor = Color(0xFF1E293B)
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = statusText,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF94A3B8),
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
