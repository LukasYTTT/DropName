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
import com.lukasyt.dropname.ui.components.AuroraBackground
import com.lukasyt.dropname.ui.components.ProfileCard
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

    AuroraBackground {

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
                ProfileCard(profile = profile!!)

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
