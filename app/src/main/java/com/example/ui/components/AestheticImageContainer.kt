package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Define gorgeous responsive presets
object PresetBrushes {
    val sunsetBeach = Brush.verticalGradient(
        listOf(Color(0xFFFF5E62), Color(0xFFFF9966))
    )
    val pastaFeast = Brush.radialGradient(
        listOf(Color(0xFFEA384D), Color(0xFF7D1221))
    )
    val futuristicAi = Brush.linearGradient(
        listOf(Color(0xFF6A0CBB), Color(0xFF00E5FF))
    )
    val oilPainting = Brush.sweepGradient(
        listOf(Color(0xFFFFD700), Color(0xFFFF5722), Color(0xFFFF007F), Color(0xFFFFD700))
    )
    val auroraDream = Brush.verticalGradient(
        listOf(Color(0xFF00FF87), Color(0xFF60EFFF))
    )
    val midnightCyber = Brush.radialGradient(
        listOf(Color(0xFF2C3E50), Color(0xFF000000))
    )
    val forestMoss = Brush.verticalGradient(
        listOf(Color(0xFF11998e), Color(0xFF38ef7d))
    )
    val lavenderIce = Brush.linearGradient(
        listOf(Color(0xFFE0C3FC), Color(0xFF8EC5FC))
    )
    val cosmicViolet = Brush.verticalGradient(
        listOf(Color(0xFF3F2B96), Color(0xFFA8C0FF))
    )

    fun get(id: String): Brush {
        return when (id) {
            "sunset_beach", "sunset_breeze" -> sunsetBeach
            "pasta_feast" -> pastaFeast
            "futuristic_ai" -> futuristicAi
            "oil_painting" -> oilPainting
            "aurora", "aurora_dream" -> auroraDream
            "midnight_cyber" -> midnightCyber
            "forest_moss" -> forestMoss
            "lavender_ice" -> lavenderIce
            "cosmic", "cosmic_violet" -> cosmicViolet
            "indigo" -> Brush.verticalGradient(listOf(Color(0xFF3F51B5), Color(0xFF5C6BC0)))
            "cherry" -> Brush.verticalGradient(listOf(Color(0xFFD32F2F), Color(0xFFE57373)))
            "teal" -> Brush.verticalGradient(listOf(Color(0xFF00796B), Color(0xFF4DB6AC)))
            "amber" -> Brush.linearGradient(listOf(Color(0xFFFFA000), Color(0xFFFFD54F)))
            "emerald" -> Brush.verticalGradient(listOf(Color(0xFF388E3C), Color(0xFF81C784)))
            "violet" -> Brush.linearGradient(listOf(Color(0xFF7B1FA2), Color(0xFFBA68C8)))
            "rose" -> Brush.verticalGradient(listOf(Color(0xFFC2185B), Color(0xFFF06292)))
            else -> Brush.verticalGradient(listOf(Color(0xFF6200EE), Color(0xFF3700B3)))
        }
    }

    fun getAvatarColors(id: String): Pair<Color, Color> {
        return when (id) {
            "aurora" -> Pair(Color(0xFF00FF87), Color(0xFF60EFFF))
            "indigo" -> Pair(Color(0xFF3F51B5), Color(0xFF5C6BC0))
            "cherry" -> Pair(Color(0xFFD32F2F), Color(0xFFE57373))
            "teal" -> Pair(Color(0xFF00796B), Color(0xFF4DB6AC))
            "amber" -> Pair(Color(0xFFFFA000), Color(0xFFFFD54F))
            "emerald" -> Pair(Color(0xFF388E3C), Color(0xFF81C784))
            "violet" -> Pair(Color(0xFF7B1FA2), Color(0xFFBA68C8))
            "rose" -> Pair(Color(0xFFC2185B), Color(0xFFF06292))
            else -> Pair(Color(0xFF616161), Color(0xFF9E9E9E))
        }
    }
}

@Composable
fun AestheticImageContainer(
    presetId: String,
    modifier: Modifier = Modifier,
    overlayText: String? = null
) {
    val brush = PresetBrushes.get(presetId)

    Box(
        modifier = modifier
            .background(brush),
        contentAlignment = Alignment.Center
    ) {
        // Overlay exquisite manual artwork drawing in canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            when (presetId) {
                "sunset_beach", "sunset_breeze" -> {
                    // Sunset sun glow and reflection lines
                    drawCircle(
                        color = Color.White.copy(alpha = 0.82f),
                        radius = h * 0.22f,
                        center = Offset(w * 0.5f, h * 0.45f)
                    )
                    // Water horizon line
                    drawLine(
                        color = Color.White.copy(alpha = 0.4f),
                        start = Offset(w * 0.1f, h * 0.65f),
                        end = Offset(w * 0.9f, h * 0.65f),
                        strokeWidth = 3f
                    )
                    drawLine(
                        color = Color.White.copy(alpha = 0.25f),
                        start = Offset(w * 0.22f, h * 0.72f),
                        end = Offset(w * 0.78f, h * 0.72f),
                        strokeWidth = 2f
                    )
                }
                "pasta_feast" -> {
                    // Modern culinary plate aesthetics
                    drawCircle(
                        color = Color.White.copy(alpha = 0.15f),
                        radius = h * 0.42f,
                        center = Offset(w * 0.5f, h * 0.5f),
                        style = Stroke(width = 8f)
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.08f),
                        radius = h * 0.32f,
                        center = Offset(w * 0.5f, h * 0.5f)
                    )
                }
                "futuristic_ai" -> {
                    // Cyber grids and nodes
                    val cols = 8
                    val rows = 8
                    for (i in 1..cols) {
                        drawLine(
                            color = Color.White.copy(alpha = 0.12f),
                            start = Offset(w * (i.toFloat() / (cols + 1)), 0f),
                            end = Offset(w * (i.toFloat() / (cols + 1)), h),
                            strokeWidth = 1.5f
                        )
                    }
                    for (i in 1..rows) {
                        drawLine(
                            color = Color.White.copy(alpha = 0.12f),
                            start = Offset(0f, h * (i.toFloat() / (rows + 1))),
                            end = Offset(w, h * (i.toFloat() / (rows + 1))),
                            strokeWidth = 1.5f
                        )
                    }
                    // Nodes
                    drawCircle(
                        color = Color(0xFF00E5FF).copy(alpha = 0.7f),
                        radius = 12f,
                        center = Offset(w * 0.4f, h * 0.4f)
                    )
                    drawCircle(
                        color = Color(0xFF6A0CBB).copy(alpha = 0.8f),
                        radius = 16f,
                        center = Offset(w * 0.6f, h * 0.6f)
                    )
                    drawLine(
                        color = Color.White.copy(alpha = 0.5f),
                        start = Offset(w * 0.4f, h * 0.4f),
                        end = Offset(w * 0.6f, h * 0.6f),
                        strokeWidth = 3f
                    )
                }
                "oil_painting" -> {
                    // Swirling paint brush marks
                    drawArc(
                        color = Color.White.copy(alpha = 0.25f),
                        startAngle = 45f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(w * 0.1f, h * 0.1f),
                        size = Size(w * 0.8f, h * 0.8f),
                        style = Stroke(width = 24f)
                    )
                    drawArc(
                        color = Color.Black.copy(alpha = 0.15f),
                        startAngle = 220f,
                        sweepAngle = 90f,
                        useCenter = false,
                        topLeft = Offset(w * 0.2f, h * 0.2f),
                        size = Size(w * 0.6f, h * 0.6f),
                        style = Stroke(width = 16f)
                    )
                }
                "midnight_cyber" -> {
                    // Circular concentric radar
                    for (i in 1..4) {
                        drawCircle(
                            color = Color(0xFF00FF87).copy(alpha = 0.08f * i),
                            radius = (h * 0.18f * i),
                            center = Offset(w * 0.5f, h * 0.5f),
                            style = Stroke(width = 2f)
                        )
                    }
                }
                else -> {
                    // Soft organic decorative circles
                    drawCircle(
                        color = Color.White.copy(alpha = 0.12f),
                        radius = h * 0.35f,
                        center = Offset(w * 0.2f, h * 0.2f)
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.08f),
                        radius = h * 0.4f,
                        center = Offset(w * 0.8f, h * 0.8f)
                    )
                }
            }
        }

        if (overlayText != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = overlayText,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(24.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
fun UserAvatar(
    presetId: String,
    displayName: String,
    size: Dp = 40.dp,
    modifier: Modifier = Modifier
) {
    val (color1, color2) = PresetBrushes.getAvatarColors(presetId)
    val initials = displayName.trim().take(1).uppercase()

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Brush.verticalGradient(listOf(color1, color2))),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.White,
            fontSize = (size.value * 0.42f).sp,
            fontWeight = FontWeight.Bold
        )
    }
}
