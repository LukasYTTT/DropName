package com.lukasyt.dropname.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.lukasyt.dropname.data.ProfileRepository
import com.lukasyt.dropname.data.UserProfile
import com.lukasyt.dropname.theme.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onNavigateToHistory: () -> Unit,
    onNavigateToQrCode: (UserProfile) -> Unit,
    onNavigateToSetup: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { ProfileRepository(context) }
    var profile by remember { mutableStateOf<UserProfile?>(null) }

    LaunchedEffect(Unit) {
        profile = repository.userProfileFlow.firstOrNull()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = 40.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(250.dp)
                    .offset(x = 50.dp, y = (-50).dp)
                    .background(PrimaryBlue.copy(alpha = 0.4f), shape = CircleShape)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .size(300.dp)
                    .offset(x = (-50).dp, y = 50.dp)
                    .background(androidx.compose.ui.graphics.Color(0xFF8A2BE2).copy(alpha = 0.3f), shape = CircleShape)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            
            Text(
                text = "My Card",
                color = TextPrimary,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            if (profile != null) {
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
                        Text(profile?.name?.take(1) ?: "", fontSize = 40.sp, color = TextPrimary)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = profile?.name ?: "",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Dynamic Fields inside Card
                    profile?.fields?.forEach { field ->
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

                Spacer(modifier = Modifier.height(32.dp))

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
                
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
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
                
                Text(
                    "Ready to Share",
                    color = TextSecondary,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    "Hold your phone near another DropName user",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(32.dp))

            } else {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        }
    }
}
