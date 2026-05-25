package com.lukasyt.dropname.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lukasyt.dropname.theme.DarkBackground
import com.lukasyt.dropname.theme.PrimaryBlue
import com.lukasyt.dropname.theme.TextPrimary
import com.lukasyt.dropname.theme.TextSecondary

@Composable
fun OnboardingScreen(
    onNavigateToSetup: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Blurred background effect
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = 50.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(300.dp)
                    .background(PrimaryBlue.copy(alpha = 0.3f), shape = CircleShape)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Welcome to",
                color = TextSecondary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "DropName",
                color = TextPrimary,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Share your profile instantly by holding your phone near another DropName user. No need to open the app.",
                color = TextSecondary,
                fontSize = 17.sp,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onNavigateToSetup,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp), // iOS corner radius
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text(
                    text = "Get Started",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}
