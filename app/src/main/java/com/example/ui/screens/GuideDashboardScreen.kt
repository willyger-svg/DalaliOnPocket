package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.auth.AuthManager
import com.example.core.guide.GuideDispatchService
import com.example.core.localization.AppLanguage
import com.example.data.model.ViewingBooking
import com.example.data.repository.PropertyRepository
import com.example.ui.components.DopBentoCard
import com.example.ui.components.DopBadge
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun GuideMainDashboardScreen(
    lang: AppLanguage,
    onOpenChat: (ViewingBooking) -> Unit = {}
) {
    val bookings by PropertyRepository.viewingBookings.collectAsState()
    val availableJobs by GuideDispatchService.availableJobs.collectAsState()
    val earnings by GuideDispatchService.earnings.collectAsState()
    val user by AuthManager.currentUser.collectAsState()

    val profile = user?.guideProfile
    val levelName = profile?.level?.label ?: "Unknown"

    var isAvailable by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DopNeutralPearl)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Guide Header
        item {
            DopBentoCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(DopGuideAmberDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (user?.name ?: "DG").take(2).uppercase(),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = user?.name ?: "Mwongoza Ziara", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DopNavyPrimary)
                        Text(text = "DoP Guide • Level: $levelName", fontSize = 12.sp, color = DopTextSecondary)
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = DopTrustGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("VERIFIED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DopTrustGreen)
                        }
                    }
                }
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = DopTextMuted.copy(alpha = 0.2f))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(if (isAvailable) DopTrustGreen else DopError))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isAvailable) "Available" else "Unavailable", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Text(
                            if (isAvailable) "You are available for eligible assignments." else "You will not receive new assignments.",
                            fontSize = 11.sp,
                            color = DopTextSecondary
                        )
                    }
                    Switch(
                        checked = isAvailable,
                        onCheckedChange = { isAvailable = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = DopTrustGreen, checkedTrackColor = DopTrustGreenContainer)
                    )
                }
            }
        }

        // Today Overview
        item {
            Text("TODAY", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DopTextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OverviewCard(title = "New Jobs", count = availableJobs.size.toString(), modifier = Modifier.weight(1f))
                OverviewCard(title = "Upcoming", count = "1", modifier = Modifier.weight(1f))
                OverviewCard(title = "Completed", count = "2", modifier = Modifier.weight(1f))
            }
        }

        // Today's Earnings
        item {
            DopBentoCard(backgroundColor = DopNavyPrimary) {
                Column {
                    Text("Today's Earnings", fontSize = 12.sp, color = DopNeutralPearl.copy(alpha = 0.7f))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("TSh 5,000 pending", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DopTrustGreenLight)
                }
            }
        }
        
        // Performance
        item {
            Text("PERFORMANCE", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DopTextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            DopBentoCard {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    PerformanceStat("Rating", "4.9", Icons.Default.Star)
                    PerformanceStat("Response", "98%", Icons.Default.Timer)
                    PerformanceStat("Completed", "42", Icons.Default.CheckCircle)
                }
            }
        }
    }
}

@Composable
fun OverviewCard(title: String, count: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = DopSurfaceCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, DopTextMuted.copy(alpha = 0.1f)),
        modifier = modifier.aspectRatio(1f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(count, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DopNavyPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, fontSize = 11.sp, color = DopTextSecondary, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

@Composable
fun PerformanceStat(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = DopGuideAmberDark, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DopNavyPrimary)
        }
        Text(title, fontSize = 11.sp, color = DopTextSecondary)
    }
}
