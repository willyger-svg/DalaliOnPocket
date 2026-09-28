package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.auth.AuthManager
import com.example.core.guide.GuideDispatchService
import com.example.core.guide.GuideSafetyService
import com.example.core.localization.AppLanguage
import com.example.core.localization.DoPStrings
import com.example.data.model.ViewingBooking
import com.example.data.model.ViewingStatus
import com.example.data.model.GuideLevel
import com.example.data.repository.PropertyRepository
import com.example.ui.components.DopBadge
import com.example.ui.components.DopBentoCard
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun GuideJobsScreen(
    lang: AppLanguage,
    onOpenChat: (ViewingBooking) -> Unit = {}
) {
    val bookings by PropertyRepository.viewingBookings.collectAsState()
    val availableJobs by GuideDispatchService.availableJobs.collectAsState()
    val earnings by GuideDispatchService.earnings.collectAsState()
    val scope = rememberCoroutineScope()
    val user by AuthManager.currentUser.collectAsState()

    val profile = user?.guideProfile
    val levelName = profile?.level?.label ?: "Unknown"

    LaunchedEffect(Unit) {
        GuideDispatchService.loadMockJobs()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DopNeutralPearl)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Guide Jobs", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DopNavyPrimary)
        }

        item {
            Text("Live Virtual Viewings", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DopNavyPrimary)
        }
        item {
            DopBentoCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Videocam, contentDescription = null, tint = DopGuideAmberDark)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Live Virtual Viewing", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Service not yet connected", fontSize = 12.sp, color = DopTextSecondary)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("Live Virtual Viewing will be available when the video service is connected.", fontSize = 11.sp, color = DopTextMuted)
            }
        }
        
        if (availableJobs.isNotEmpty()) {
            item {
                Text(
                    text = "Kazi Mpya (Available Jobs)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = DopNavyPrimary
                )
            }
            items(availableJobs) { job ->
                DopBentoCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "New Viewing Request", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = job.propertyTitle, fontSize = 13.sp, color = DopTextSecondary)
                            Text(text = job.propertyLocation, fontSize = 12.sp, color = DopTextMuted)
                        }
                        DopBadge("TSh 2,500", Icons.Default.Payments, DopGuideAmberDark, DopGuideAmberContainer)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        OutlinedButton(
                            onClick = { scope.launch { GuideDispatchService.declineJob(job.bookingCode) } },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = DopError)
                        ) {
                            Text("Decline")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { scope.launch { GuideDispatchService.acceptJob(job.bookingCode) } },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = DopGuideAmber)
                        ) {
                            Text("Accept Job")
                        }
                    }
                }
            }
        }

        // Active Dispatch Jobs
        item {
            Text(
                text = "Kazi Zangu (My Active Jobs)",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = DopNavyPrimary
            )
        }

        val activeBookings = bookings.filter { it.status in listOf(ViewingStatus.GUIDE_ASSIGNED, ViewingStatus.GUIDE_ON_THE_WAY, ViewingStatus.GUIDE_ARRIVED, ViewingStatus.VIEWING_IN_PROGRESS) }
        if (activeBookings.isEmpty()) {
            item {
                Text("Hakuna kazi zinazoendelea sasa hivi.", fontSize = 12.sp, color = DopTextSecondary)
            }
        }

        items(activeBookings) { booking ->
            DopBentoCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = booking.bookingCode, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DopNavyPrimary)
                    DopBadge(
                        text = booking.status.name,
                        icon = Icons.Default.Schedule,
                        color = DopGuideAmberDark,
                        backgroundColor = DopGuideAmberContainer
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = booking.propertyTitle, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Text(text = "Eneo: ${booking.propertyLocation}", fontSize = 12.sp, color = DopTextSecondary)
                Text(text = "Mteja: ${booking.customerName}", fontSize = 12.sp, color = DopNavyPrimary)
                Text(text = "Muda: ${booking.scheduledDate}", fontSize = 11.sp, color = DopGuideAmber, fontWeight = FontWeight.Medium)
                Text(text = "Sehemu ya Kukutana: ${booking.meetingPoint}", fontSize = 11.sp, color = DopTextMuted)

                Spacer(modifier = Modifier.height(10.dp))

                // Chat with Customer button
                OutlinedButton(
                    onClick = { onOpenChat(booking) },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DopNavyPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("guide_chat_btn_${booking.bookingCode}")
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, tint = DopNavyPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Chat na Mteja (${booking.customerName}) 💬", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Guide Dispatch State Machine Transition Buttons
                when (booking.status) {
                    ViewingStatus.GUIDE_ASSIGNED -> {
                        Button(
                            onClick = { PropertyRepository.advanceViewingStatus(booking.bookingCode, ViewingStatus.GUIDE_ON_THE_WAY) },
                            colors = ButtonDefaults.buttonColors(containerColor = DopNavyPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("guide_start_trip_${booking.bookingCode}")
                        ) {
                            Icon(Icons.Default.DirectionsCar, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Nipo Njia Panda / Nakuja")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { GuideSafetyService.reportSafetyIssue(booking.bookingCode, "Kuna shida ya usalama/dharura") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = DopError)
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Ripoti Shida (Report Issue)")
                        }
                    }
                    ViewingStatus.GUIDE_ON_THE_WAY -> {
                        Button(
                            onClick = { PropertyRepository.advanceViewingStatus(booking.bookingCode, ViewingStatus.GUIDE_ARRIVED) },
                            colors = ButtonDefaults.buttonColors(containerColor = DopGuideAmber),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("guide_arrived_${booking.bookingCode}")
                        ) {
                            Icon(Icons.Default.Place, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Nimefika Eneo la Nyumba")
                        }
                    }
                    ViewingStatus.GUIDE_ARRIVED -> {
                        Button(
                            onClick = { PropertyRepository.advanceViewingStatus(booking.bookingCode, ViewingStatus.VIEWING_IN_PROGRESS) },
                            colors = ButtonDefaults.buttonColors(containerColor = DopNavyElevated),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("guide_start_viewing_${booking.bookingCode}")
                        ) {
                            Icon(Icons.Default.Visibility, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Anza Ukaguzi na Mteja")
                        }
                    }
                    ViewingStatus.VIEWING_IN_PROGRESS -> {
                        Button(
                            onClick = { GuideDispatchService.completeViewing(booking.bookingCode) },
                            colors = ButtonDefaults.buttonColors(containerColor = DopTrustGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("guide_complete_viewing_${booking.bookingCode}")
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Kamilisha Ziara (Pokea TSh 2,500)")
                        }
                    }
                    else -> {}
                }
            }
        }
        
        val completedBookings = bookings.filter { it.status == ViewingStatus.COMPLETED }
        if (completedBookings.isNotEmpty()) {
            item {
                Text(
                    text = "Zilizokamilika (Completed)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = DopNavyPrimary
                )
            }
            items(completedBookings) { booking ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DopTrustGreenContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = DopTrustGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(booking.propertyTitle, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text(booking.customerName, fontSize = 11.sp)
                            }
                        }
                        Text("+ TSh 2,500", fontSize = 13.sp, color = DopTrustGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun GuidePublicProfileScreen(guideName: String, guideLevel: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DopNeutralPearl)
    ) {
        Surface(
            color = DopSurfaceCard,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = DopNavyPrimary)
                }
                Text("Guide Profile", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DopNavyPrimary)
            }
        }
        
        Column(modifier = Modifier.padding(16.dp)) {
            DopBentoCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(80.dp).clip(CircleShape).background(DopGuideAmberDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(guideName.take(2).uppercase(), color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(guideName, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DopNavyPrimary)
                        Text("Level: $guideLevel", fontSize = 14.sp, color = DopGuideAmberDark)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = DopTrustGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Verified DoP Guide", fontSize = 12.sp, color = DopTrustGreen)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("SERVICE DETAILS", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DopTextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            DopBentoCard {
                Text("Areas Served", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DopNavyPrimary)
                Text("Dar es Salaam, Masaki, Oysterbay, Mikocheni", fontSize = 14.sp, color = DopTextSecondary)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Languages", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DopNavyPrimary)
                Text("Swahili, English", fontSize = 14.sp, color = DopTextSecondary)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Completed Visits", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DopNavyPrimary)
                Text("42", fontSize = 14.sp, color = DopTextSecondary)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Rating", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DopNavyPrimary)
                Text("4.9 ★", fontSize = 14.sp, color = DopGuideAmberDark)
            }
        }
    }
}

@Composable
fun GuideScheduleScreen(lang: AppLanguage) {
    val context = LocalContext.current
    var isAvailable by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize().background(DopNeutralPearl).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Ratiba (Schedule & Availability)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DopNavyPrimary)
        Spacer(modifier = Modifier.height(20.dp))
        
        DopBentoCard {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Hali ya Upatikanaji", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(if (isAvailable) "Upo tayari kupokea kazi mpya" else "Hupokei kazi kwa sasa", fontSize = 12.sp, color = DopTextSecondary)
                }
                Switch(
                    checked = isAvailable,
                    onCheckedChange = { 
                        isAvailable = it 
                        Toast.makeText(context, if (it) "Sasa upo online!" else "Umejiweka offline.", Toast.LENGTH_SHORT).show()
                    },
                    colors = SwitchDefaults.colors(checkedThumbColor = DopOchre, checkedTrackColor = DopOchreContainer)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Mock upcoming
        Text("Kazi Zijazo (Upcoming)", modifier = Modifier.align(Alignment.Start), fontWeight = FontWeight.Bold, color = DopNavyPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        
        Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
            Text("Hakuna kazi mpya zilizopangwa kwa sasa.", color = DopTextSecondary)
        }
    }
}
