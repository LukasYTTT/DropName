package com.lukasyt.dropname.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object SocialIcons {
    // We are using simple vector paths to ensure it compiles without issues.
    
    val Instagram: ImageVector
        get() = ImageVector.Builder(
            name = "Instagram",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(7f, 2f)
                lineTo(17f, 2f)
                curveTo(19.76f, 2f, 22f, 4.24f, 22f, 7f)
                lineTo(22f, 17f)
                curveTo(22f, 19.76f, 19.76f, 22f, 17f, 22f)
                lineTo(7f, 22f)
                curveTo(4.24f, 22f, 2f, 19.76f, 2f, 17f)
                lineTo(2f, 7f)
                curveTo(2f, 4.24f, 4.24f, 2f, 7f, 2f)
                close()
            }
        }.build()

    val TikTok: ImageVector
        get() = ImageVector.Builder(
            name = "TikTok",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(12f, 2f)
                lineTo(12f, 22f)
                lineTo(22f, 12f)
                close()
            }
        }.build()

    val YouTube: ImageVector
        get() = ImageVector.Builder(
            name = "YouTube",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(2f, 7f)
                lineTo(22f, 7f)
                lineTo(22f, 17f)
                lineTo(2f, 17f)
                close()
            }
        }.build()

    val LinkedIn: ImageVector
        get() = ImageVector.Builder(
            name = "LinkedIn",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(2f, 2f)
                lineTo(22f, 2f)
                lineTo(22f, 22f)
                lineTo(2f, 22f)
                close()
            }
        }.build()

    val Twitter: ImageVector
        get() = ImageVector.Builder(
            name = "Twitter",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(2f, 2f)
                lineTo(22f, 22f)
                moveTo(22f, 2f)
                lineTo(2f, 22f)
            }
        }.build()

    val Snapchat: ImageVector
        get() = ImageVector.Builder(
            name = "Snapchat",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(12f, 2f)
                lineTo(22f, 22f)
                lineTo(2f, 22f)
                close()
            }
        }.build()
        
    val WhatsApp: ImageVector
        get() = ImageVector.Builder(
            name = "WhatsApp",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(12f, 2f)
                lineTo(22f, 12f)
                lineTo(12f, 22f)
                lineTo(2f, 12f)
                close()
            }
        }.build()
}
