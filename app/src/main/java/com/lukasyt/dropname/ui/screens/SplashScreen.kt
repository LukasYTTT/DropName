package com.lukasyt.dropname.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lukasyt.dropname.data.ProfileRepository
import com.lukasyt.dropname.theme.DarkBackground
import com.lukasyt.dropname.theme.PrimaryBlue
import com.lukasyt.dropname.theme.TextPrimary
import kotlinx.coroutines.flow.first

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToOnboarding: () -> Unit
) {
    val context = LocalContext.current
    val repository = ProfileRepository(context)

    LaunchedEffect(Unit) {
        val profile = repository.userProfileFlow.first()
        if (profile != null) {
            onNavigateToHome()
        } else {
            onNavigateToOnboarding()
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
                .blur(radius = 40.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(200.dp)
                    .background(PrimaryBlue.copy(alpha = 0.4f), shape = CircleShape)
            )
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "DropName",
                color = TextPrimary,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}
