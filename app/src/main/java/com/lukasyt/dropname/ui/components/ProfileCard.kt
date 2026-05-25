package com.lukasyt.dropname.ui.components

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lukasyt.dropname.data.UserProfile
import com.lukasyt.dropname.theme.*

@Composable
fun ProfileCard(profile: UserProfile, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .size(width = 320.dp, height = 240.dp)
            .graphicsLayer {
                renderEffect = BlurEffect(20f, 20f, TileMode.Clamp)
            },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.12f))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (profile.profileImageBase64 != null) {
                val bitmap = remember(profile.profileImageBase64) {
                    try {
                        val bytes = Base64.decode(profile.profileImageBase64, Base64.DEFAULT)
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
                    } catch (e: Exception) { null }
                }
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.Gray),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    PlaceholderAvatar(profile.name)
                }
            } else {
                PlaceholderAvatar(profile.name)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = profile.name,
                color = TextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Dynamic fields
            profile.fields.forEachIndexed { index, field ->
                val icon = getSocialIcon(field.label)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (icon != null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = field.label,
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                    Text(text = field.value, color = Color.White.copy(alpha = 0.7f), fontSize = 16.sp, fontWeight = FontWeight.Normal)
                }
            }
        }
    }
}

private fun getSocialIcon(label: String): androidx.compose.ui.graphics.vector.ImageVector? {
    val lower = label.lowercase()
    return when {
        "insta" in lower -> SocialIcons.Instagram
        "tiktok" in lower -> SocialIcons.TikTok
        "youtube" in lower -> SocialIcons.YouTube
        "linkedin" in lower -> SocialIcons.LinkedIn
        "twitter" in lower || "x" == lower -> SocialIcons.Twitter
        "snapchat" in lower -> SocialIcons.Snapchat
        "whatsapp" in lower -> SocialIcons.WhatsApp
        else -> null
    }
}

private fun getBrandColor(label: String): Color {
    val lower = label.lowercase()
    return when {
        "insta" in lower -> Color(0xFFC13584)
        "tiktok" in lower -> Color(0xFF000000)
        "snapchat" in lower -> Color(0xFFFFCC00) // Darker yellow for visibility
        "linkedin" in lower -> Color(0xFF0077B5)
        "whatsapp" in lower -> Color(0xFF25D366)
        "twitter" in lower || "x" == lower -> Color(0xFF1DA1F2)
        "youtube" in lower -> Color(0xFFFF0000)
        "phone" in lower -> Color(0xFF34C759) // iOS Green
        "mail" in lower || "email" in lower -> Color(0xFF007AFF) // iOS Blue
        else -> Color(0xFF8E8E93) // Gray fallback
    }
}

@Composable
private fun PlaceholderAvatar(name: String = "") {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .background(SurfaceColor),
        contentAlignment = Alignment.Center
    ) {
        Text(name.take(1), fontSize = 40.sp, color = TextPrimary)
    }
}
