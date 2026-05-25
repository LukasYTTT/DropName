package com.lukasyt.dropname.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lukasyt.dropname.data.HistoryRepository
import com.lukasyt.dropname.data.UserProfile
import com.lukasyt.dropname.theme.CardBackground
import com.lukasyt.dropname.theme.DarkBackground
import com.lukasyt.dropname.theme.PrimaryBlue
import com.lukasyt.dropname.theme.SurfaceColor
import com.lukasyt.dropname.theme.TextPrimary
import com.lukasyt.dropname.theme.TextSecondary
import kotlinx.coroutines.flow.firstOrNull

@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit,
    onProfileClick: (UserProfile) -> Unit
) {
    val context = LocalContext.current
    val repository = remember { HistoryRepository(context) }
    var history by remember { mutableStateOf<List<UserProfile>>(emptyList()) }

    LaunchedEffect(Unit) {
        history = repository.historyFlow.firstOrNull() ?: emptyList()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onNavigateBack) {
                    Text("‹ Back", color = PrimaryBlue, fontSize = 17.sp)
                }
                Spacer(modifier = Modifier.weight(1f))
            }
            
            Text(
                text = "Contact History",
                color = TextPrimary,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            if (history.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No contacts received yet.", color = TextSecondary, fontSize = 16.sp)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(history) { profile ->
                        HistoryItem(profile) {
                            onProfileClick(profile)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryItem(profile: UserProfile, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardBackground)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(SurfaceColor),
            contentAlignment = Alignment.Center
        ) {
            Text(profile.name.take(1), fontSize = 20.sp, color = TextPrimary)
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column {
            Text(
                text = profile.name,
                color = TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )
            val firstField = profile.fields.firstOrNull()
            if (firstField != null) {
                Text(
                    text = firstField.value,
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
        }
    }
}
