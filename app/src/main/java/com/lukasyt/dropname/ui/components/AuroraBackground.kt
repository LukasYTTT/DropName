package com.lukasyt.dropname.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import com.lukasyt.dropname.theme.DarkBackground

@Composable
fun AuroraBackground(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "aurora")
    
    val x1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "x1"
    )
    val y1 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "y1"
    )
    val x2 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "x2"
    )

    Box(modifier = modifier.fillMaxSize().background(DarkBackground)) {
        Canvas(modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                renderEffect = BlurEffect(80f, 80f, TileMode.Clamp)
            }
        ) {
            val width = size.width
            val height = size.height
            
            // Blob 1: Purple
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF8B5CF6), Color.Transparent),
                    center = Offset(width * x1, height * y1),
                    radius = width * 0.8f
                ),
                radius = width * 0.8f,
                center = Offset(width * x1, height * y1),
                blendMode = BlendMode.Screen
            )
            
            // Blob 2: Cyan/Blue
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF06B6D4), Color(0xFF3B82F6), Color.Transparent),
                    center = Offset(width * x2, height * (1f - y1)),
                    radius = width * 0.7f
                ),
                radius = width * 0.7f,
                center = Offset(width * x2, height * (1f - y1)),
                blendMode = BlendMode.Screen
            )
            
            // Blob 3: Pink
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFEC4899), Color.Transparent),
                    center = Offset(width * 0.5f, height * x1),
                    radius = width * 0.6f
                ),
                radius = width * 0.6f,
                center = Offset(width * 0.5f, height * x1),
                blendMode = BlendMode.Screen
            )
        }
        
        content()
    }
}
