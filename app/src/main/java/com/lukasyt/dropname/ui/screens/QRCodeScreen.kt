package com.lukasyt.dropname.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.lukasyt.dropname.data.ProfileRepository
import com.lukasyt.dropname.data.UserProfile
import com.lukasyt.dropname.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun QRCodeScreen(
    profile: UserProfile,
    onNavigateBack: () -> Unit,
    onProfileReceived: ((UserProfile) -> Unit)? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoadingProfile by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(profile) {
        val url = if (profile.id != null) {
            "${ProfileRepository.BASE_URL}/p/${profile.id}"
        } else {
            ProfileRepository.BASE_URL
        }
        withContext(Dispatchers.IO) {
            qrBitmap = generateQrCode(url)
        }
    }

    // QR-Code Scanner via system camera intent
    val qrScanLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // The system camera doesn't return QR data. We direct users to use
        // their camera app which will trigger our deep link instead.
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onNavigateBack) {
                    Text("‹ Zurück", color = PrimaryBlue, fontSize = 17.sp)
                }
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Teilen",
                color = TextPrimary,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Zeig diesen QR-Code oder halte die Handys aneinander",
                color = TextSecondary,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )

            // QR Code Container
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White)
                    .padding(18.dp),
                contentAlignment = Alignment.Center
            ) {
                if (qrBitmap != null) {
                    Image(
                        bitmap = qrBitmap!!.asImageBitmap(),
                        contentDescription = "QR Code",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    CircularProgressIndicator(color = PrimaryBlue, modifier = Modifier.size(40.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // URL anzeigen (kurz)
            if (profile.id != null) {
                Text(
                    text = "dropname-api.lukasyt887.workers.dev/p/${profile.id}",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // QR scannen Button — öffnet Kamera und erklärt den Ablauf
            Button(
                onClick = {
                    scope.launch {
                        // Profil URL im Browser öffnen geht über Deep Link.
                        // Wir zeigen die Erklärung wie man scannen muss.
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                            data = android.net.Uri.parse("https://play.google.com/store/search?q=qr+scanner&c=apps")
                        }
                        try {
                            // Versuche zuerst eine native Scanner-App zu öffnen
                            val scanIntent = context.packageManager.getLaunchIntentForPackage("com.google.android.apps.lens")
                                ?: context.packageManager.getLaunchIntentForPackage("com.google.zxing.client.android")
                            if (scanIntent != null) {
                                context.startActivity(scanIntent)
                            } else {
                                // Fallback: Standard-Kamera öffnen
                                val cameraIntent = android.content.Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
                                context.startActivity(cameraIntent)
                            }
                        } catch (e: Exception) {
                            errorMessage = "Öffne einfach deine Kamera-App und halte sie über den QR-Code!"
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .border(1.dp, GlassBorder, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GlassBackground)
            ) {
                Text("📷  QR-Code scannen", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            // Info Hinweis
            errorMessage?.let { msg ->
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(PrimaryBlue.copy(alpha = 0.12f))
                        .border(1.dp, PrimaryBlue.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "ℹ️  $msg",
                        color = PrimaryBlue,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Anleitung Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.06f))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("So teilst du dein Profil:", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text("📡  NFC: Rücken an Rücken halten", color = TextSecondary, fontSize = 13.sp)
                    Text("📷  QR: Kamera-App öffnen → QR-Code scannen → App öffnet sich automatisch", color = TextSecondary, fontSize = 13.sp)
                    Text("🌐  Ohne App: Link öffnet die Webseite mit deiner Visitenkarte", color = TextSecondary, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

private fun generateQrCode(text: String): Bitmap? {
    return try {
        val size = 512
        val hints = mapOf(EncodeHintType.MARGIN to 1)
        val bitMatrix = QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size, hints)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
            }
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
