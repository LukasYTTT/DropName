package com.lukasyt.dropname.ui.screens

import android.content.Intent
import android.provider.ContactsContract
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lukasyt.dropname.data.HistoryRepository
import com.lukasyt.dropname.data.UserProfile
import com.lukasyt.dropname.theme.*

@Composable
fun ReceivedProfileScreen(
    profile: UserProfile,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val historyRepository = remember { HistoryRepository(context) }

    LaunchedEffect(profile) {
        historyRepository.addProfile(profile)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Blurred background effect showing connection success
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = 50.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(400.dp)
                    .background(Color(0xFF34C759).copy(alpha = 0.3f), shape = CircleShape) // iOS Green
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))
            
            Text(
                text = "Contact Received",
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Profile Card Apple Style
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(CardBackground)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(SurfaceColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(profile.name.take(1), fontSize = 40.sp, color = TextPrimary)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = profile.name,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Dynamic Fields inside Card
                profile.fields.forEach { field ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(field.label, color = TextSecondary, fontSize = 16.sp)
                        Text(field.value, color = PrimaryBlue, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                    Divider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 4.dp))
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_INSERT).apply {
                        type = ContactsContract.RawContacts.CONTENT_TYPE
                        putExtra(ContactsContract.Intents.Insert.NAME, profile.name)
                        // Try to find a phone or email field
                        profile.fields.find { it.label.contains("Phone", ignoreCase = true) }?.let {
                            putExtra(ContactsContract.Intents.Insert.PHONE, it.value)
                        }
                        profile.fields.find { it.label.contains("Email", ignoreCase = true) }?.let {
                            putExtra(ContactsContract.Intents.Insert.EMAIL, it.value)
                        }
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Save to Contacts", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onClose,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceColor)
            ) {
                Text("Done", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
