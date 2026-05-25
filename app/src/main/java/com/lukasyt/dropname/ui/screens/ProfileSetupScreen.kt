package com.lukasyt.dropname.ui.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lukasyt.dropname.data.ProfileField
import com.lukasyt.dropname.data.ProfileRepository
import com.lukasyt.dropname.data.UserProfile
import com.lukasyt.dropname.theme.*
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
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, it))
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            }
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 64, 64, true)
            val outputStream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
            val bytes = outputStream.toByteArray()
            profileImageBase64 = Base64.encodeToString(bytes, Base64.DEFAULT)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Blurred background effect
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = 30.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(250.dp)
                    .offset(x = 50.dp, y = (-50).dp)
                    .background(PrimaryBlue.copy(alpha = 0.3f), shape = CircleShape)
            )
        }

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
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(CardBackground)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (profileImageBase64 != null) {
                    Text(text = "✓", color = Color.Green, fontSize = 40.sp)
                } else {
                    Text("+", color = PrimaryBlue, fontSize = 40.sp, fontWeight = FontWeight.Light)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))

            // iOS-style form container
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(CardBackground)
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )

                Divider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 8.dp))

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
                            label = { Text(field.label) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
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
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Save Profile", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
