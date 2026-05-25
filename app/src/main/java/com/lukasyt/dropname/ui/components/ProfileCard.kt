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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lukasyt.dropname.data.UserProfile
import com.lukasyt.dropname.theme.*

@Composable
fun ProfileCard(profile: UserProfile, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(GlassBackground)
                .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
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
                val brandColor = getBrandColor(field.label)
                val icon = getSocialIcon(field.label)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(brandColor),
                        contentAlignment = Alignment.Center
                    ) {
                        if (icon != null) {
                            Icon(
                                imageVector = icon,
                                contentDescription = field.label,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                text = field.label.take(1).uppercase(),
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column {
                        Text(text = field.label, color = TextSecondary, fontSize = 13.sp)
                        Text(text = field.value, color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Medium)
                    }
                }
                if (index < profile.fields.size - 1) {
                    HorizontalDivider(color = GlassBorder, modifier = Modifier.padding(start = 56.dp, top = 4.dp, bottom = 4.dp))
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
