package com.lukasyt.dropname.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.lukasyt.dropname.R
import com.lukasyt.dropname.theme.DarkBackground
import com.lukasyt.dropname.theme.PrimaryBlue
import com.lukasyt.dropname.theme.SurfaceColor
import com.lukasyt.dropname.theme.TextPrimary
import com.lukasyt.dropname.theme.TextSecondary
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onNavigateToNext: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    
    // Background gradient that gets blurred
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Mock iOS Blurred background effect
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = 30.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(200.dp)
                    .offset(x = (-50).dp, y = (-50).dp)
                    .background(PrimaryBlue.copy(alpha = 0.5f), shape = RoundedCornerShape(100.dp))
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(300.dp)
                    .offset(x = 50.dp, y = 50.dp)
                    .background(Color(0xFF8A2BE2).copy(alpha = 0.3f), shape = RoundedCornerShape(150.dp))
            )
        }

        // Foreground content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "DropName",
                color = TextPrimary,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Sign in to share your digital identity",
                color = TextSecondary,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(64.dp))

            // iOS styled Google Login Button
            Button(
                onClick = {
                    coroutineScope.launch {
                        try {
                            val googleIdOption = GetGoogleIdOption.Builder()
                                .setFilterByAuthorizedAccounts(false)
                                .setServerClientId("YOUR_WEB_CLIENT_ID_HERE") // Placeholder
                                .setAutoSelectEnabled(true)
                                .build()

                            val request = GetCredentialRequest.Builder()
                                .addCredentialOption(googleIdOption)
                                .build()

                            val result = credentialManager.getCredential(
                                request = request,
                                context = context
                            )
                            // Success! Proceed to next
                            onNavigateToNext()

                        } catch (e: GetCredentialException) {
                            // Since we have a placeholder ID, it will fail.
                            // For testing purposes, we show a toast and proceed anyway.
                            Toast.makeText(context, "Google Sign-In Mocked (Missing Client ID)", Toast.LENGTH_SHORT).show()
                            onNavigateToNext()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
                            onNavigateToNext() // Fallback
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(14.dp)), // iOS corner radius
                colors = ButtonDefaults.buttonColors(
                    containerColor = SurfaceColor,
                    contentColor = TextPrimary
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Apple style buttons usually have clean typography and standard icons
                    Text(
                        text = "Sign in with Google",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(onClick = onNavigateToNext) {
                Text(
                    text = "Skip for now",
                    color = TextSecondary,
                    fontSize = 15.sp
                )
            }
        }
    }
}
