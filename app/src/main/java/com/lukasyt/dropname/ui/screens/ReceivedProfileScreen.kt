package com.lukasyt.dropname.ui.screens

import android.content.Intent
import android.provider.ContactsContract
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lukasyt.dropname.data.HistoryRepository
import com.lukasyt.dropname.data.ProfileRepository
import com.lukasyt.dropname.data.UserProfile
import com.lukasyt.dropname.theme.*
import com.lukasyt.dropname.ui.components.AuroraBackground
import com.lukasyt.dropname.ui.components.ProfileCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@Composable
fun ReceivedProfileScreen(
    profile: UserProfile,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val historyRepository = remember { HistoryRepository(context) }
    val profileRepository = remember { ProfileRepository(context) }
    val scope = rememberCoroutineScope()

    var isVisible by remember { mutableStateOf(false) }
    var ownProfile by remember { mutableStateOf<UserProfile?>(null) }

    LaunchedEffect(profile) {
        ownProfile = profileRepository.userProfileFlow.firstOrNull()
        historyRepository.addProfile(profile)
        isVisible = true
    }

    val topCardOffsetY by animateDpAsState(
        targetValue = if (isVisible) 0.dp else (-600).dp,
        animationSpec = tween(
            durationMillis = 800,
            delayMillis = 100,
            easing = FastOutSlowInEasing
        ),
        label = "topCard"
    )

    val bottomCardOffsetY by animateDpAsState(
        targetValue = if (isVisible) 0.dp else 600.dp,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        ),
        label = "bottomCard"
    )

    fun handleClose() {
        isVisible = false
        scope.launch {
            delay(600) // Wait for exit animation
            onClose()
        }
    }

    AuroraBackground {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Fremde Karte (Top)
            Box(modifier = Modifier.offset(y = topCardOffsetY)) {
                ProfileCard(profile = profile)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Eigene Karte (Bottom - Placeholder or Real if loaded)
            Box(modifier = Modifier.offset(y = bottomCardOffsetY)) {
                val displayProfile = ownProfile ?: UserProfile(
                    name = "My Profile",
                    fields = emptyList()
                )
                ProfileCard(profile = displayProfile)
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
            ) {
                Button(
                    onClick = { handleClose() },
                    colors = ButtonDefaults.buttonColors(containerColor = iOSDarkGray),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.size(width = 160.dp, height = 52.dp)
                ) {
                    Text("Ablehnen", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }

                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_INSERT).apply {
                            type = ContactsContract.RawContacts.CONTENT_TYPE
                            putExtra(ContactsContract.Intents.Insert.NAME, profile.name)
                            profile.fields.find { it.label.contains("Phone", ignoreCase = true) }?.let {
                                putExtra(ContactsContract.Intents.Insert.PHONE, it.value)
                            }
                            profile.fields.find { it.label.contains("Email", ignoreCase = true) }?.let {
                                putExtra(ContactsContract.Intents.Insert.EMAIL, it.value)
                            }
                        }
                        context.startActivity(intent)
                        handleClose()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = iOSGreen),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.size(width = 160.dp, height = 52.dp)
                ) {
                    Text("Teilen", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }
        }
    }
}
