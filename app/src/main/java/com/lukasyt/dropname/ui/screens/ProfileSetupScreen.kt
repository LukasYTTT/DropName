package com.lukasyt.dropname.ui.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lukasyt.dropname.data.ProfileField
import com.lukasyt.dropname.data.ProfileRepository
import com.lukasyt.dropname.data.UserProfile
import com.lukasyt.dropname.theme.*
import com.lukasyt.dropname.ui.components.AuroraBackground
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    onNavigateToHome: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { ProfileRepository(context) }
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var profileImageBase64 by remember { mutableStateOf<String?>(null) }
    
    var fields by remember { mutableStateOf(listOf(
        ProfileField("Phone", ""),
        ProfileField("Email", "")
    )) }
    
    var showNetworkDialog by remember { mutableStateOf(false) }
    val networkOptions = listOf("Instagram", "TikTok", "YouTube", "Snapchat", "LinkedIn", "Twitter", "WhatsApp", "Custom Link")

    LaunchedEffect(Unit) {
        val profile = repository.userProfileFlow.firstOrNull()
        if (profile != null) {
            name = profile.name
            profileImageBase64 = profile.profileImageBase64
            if (profile.fields.isNotEmpty()) {
                fields = profile.fields
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                    decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                    decoder.isMutableRequired = true
                }
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            }
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 256, 256, true)
            val outputStream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
            val bytes = outputStream.toByteArray()
            profileImageBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
        }
    }

    AuroraBackground {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))
            
            Text(
                text = "My Profile",
                color = TextPrimary,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Profile Picture
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
                    .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (profileImageBase64 != null) {
                    val bitmap = remember(profileImageBase64) {
                        try {
                            val bytes = Base64.decode(profileImageBase64, Base64.NO_WRAP)
                            android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
                        } catch (e: Exception) { null }
                    }
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(text = "✓", color = Color.Green, fontSize = 40.sp)
                    }
                } else {
                    Text("+", color = PrimaryBlue, fontSize = 40.sp, fontWeight = FontWeight.Light)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Tippe um Foto auszuwählen", fontSize = 14.sp, color = TextSecondary)
            
            Spacer(modifier = Modifier.height(32.dp))

            // iOS-style form container
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GlassBorder,
                        unfocusedBorderColor = GlassBorder,
                        focusedContainerColor = GlassBackground,
                        unfocusedContainerColor = GlassBackground,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                // Dynamic Fields
                fields.forEachIndexed { index, field ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = field.value,
                            onValueChange = { newValue ->
                                val updated = fields.toMutableList()
                                updated[index] = field.copy(value = newValue)
                                fields = updated
                            },
                            label = { Text(field.label, color = TextSecondary) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GlassBorder,
                                unfocusedBorderColor = GlassBorder,
                                focusedContainerColor = GlassBackground,
                                unfocusedContainerColor = GlassBackground,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                        
                        IconButton(onClick = {
                            val updated = fields.toMutableList()
                            updated.removeAt(index)
                            fields = updated
                        }) {
                            Text("✕", color = Color.Red, fontSize = 20.sp)
                        }
                    }
                    if (index < fields.size - 1) {
                        Divider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Add Custom Field Button
            TextButton(
                onClick = { showNetworkDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("+", color = PrimaryBlue, fontSize = 24.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Social Link", color = PrimaryBlue, fontSize = 17.sp)
            }

            if (showNetworkDialog) {
                AlertDialog(
                    onDismissRequest = { showNetworkDialog = false },
                    title = { Text("Select Network") },
                    text = {
                        Column {
                            networkOptions.forEach { network ->
                                TextButton(
                                    onClick = {
                                        fields = fields + ProfileField(network, "")
                                        showNetworkDialog = false
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(network, fontSize = 16.sp, color = TextPrimary)
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showNetworkDialog = false }) {
                            Text("Cancel", color = Color.Red)
                        }
                    },
                    containerColor = CardBackground
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        scope.launch {
                            val profile = UserProfile(name, fields, profileImageBase64)
                            repository.saveProfile(profile)
                            onNavigateToHome()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = iOSBlue)
            ) {
                Text("Weiter", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
