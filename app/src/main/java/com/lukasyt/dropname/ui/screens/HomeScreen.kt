package com.lukasyt.dropname.ui.screens

import android.content.Intent
import android.nfc.NfcAdapter
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lukasyt.dropname.data.ProfileRepository
import com.lukasyt.dropname.data.UserProfile
import com.lukasyt.dropname.theme.*
import com.lukasyt.dropname.ui.components.AuroraBackground
import com.lukasyt.dropname.ui.components.ProfileCard
import kotlinx.coroutines.flow.firstOrNull

@Composable
fun HomeScreen(
    onNavigateToHistory: () -> Unit,
    onNavigateToQrCode: (UserProfile) -> Unit,
    onNavigateToSetup: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { ProfileRepository(context) }
    var profile by remember { mutableStateOf<UserProfile?>(null) }

    // NFC Status prüfen
    val nfcAdapter = remember { NfcAdapter.getDefaultAdapter(context) }
    val isNfcSupported = nfcAdapter != null
    val isNfcEnabled = remember { mutableStateOf(nfcAdapter?.isEnabled == true) }

    LaunchedEffect(Unit) {
        profile = repository.userProfileFlow.firstOrNull()
        isNfcEnabled.value = nfcAdapter?.isEnabled == true
    }

    AuroraBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "My Card",
                color = TextPrimary,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // NFC-Warning Banner
            AnimatedVisibility(
                visible = isNfcSupported && !isNfcEnabled.value,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFFF9F0A).copy(alpha = 0.15f))
                        .border(1.dp, Color(0xFFFF9F0A).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .clickable {
                            context.startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
                        }
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("⚠️", fontSize = 22.sp)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "NFC ist deaktiviert",
                                color = Color(0xFFFF9F0A),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Tippe hier um NFC in den Einstellungen zu aktivieren",
                                color = Color(0xFFFF9F0A).copy(alpha = 0.75f),
                                fontSize = 12.sp
                            )
                        }
                        Text("›", color = Color(0xFFFF9F0A), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // NFC nicht unterstützt Banner
            AnimatedVisibility(visible = !isNfcSupported) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFFF453A).copy(alpha = 0.12f))
                        .border(1.dp, Color(0xFFFF453A).copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("📵", fontSize = 22.sp)
                        Text(
                            text = "Dieses Gerät unterstützt kein NFC. Nutze den QR-Code zum Teilen.",
                            color = Color(0xFFFF453A).copy(alpha = 0.9f),
                            fontSize = 13.sp
                        )
                    }
                }
            }

            if (isNfcSupported && !isNfcEnabled.value || !isNfcSupported) {
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (profile != null) {
                ProfileCard(profile = profile!!)

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onNavigateToSetup,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .border(1.dp, GlassBorder, RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GlassBackground)
                ) {
                    Text("Edit Profile", color = TextPrimary, fontSize = 17.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onNavigateToHistory,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .border(1.dp, GlassBorder, RoundedCornerShape(14.dp)),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GlassBackground)
                    ) {
                        Text("History", color = TextPrimary, fontSize = 17.sp)
                    }

                    Button(
                        onClick = { onNavigateToQrCode(profile!!) },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Text("QR Code", color = Color.White, fontSize = 17.sp)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // NFC Hint unten
                if (isNfcEnabled.value) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Text("📡  ", fontSize = 14.sp)
                        Text(
                            "NFC aktiv – Halte dein Handy an ein anderes",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

            } else {
                Spacer(modifier = Modifier.weight(1f))
                CircularProgressIndicator(color = PrimaryBlue)
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}
