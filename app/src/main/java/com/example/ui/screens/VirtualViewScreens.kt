package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.localization.AppLanguage
import com.example.core.localization.DoPStrings
import com.example.data.model.Property
import com.example.data.model.VirtualScene
import com.example.ui.components.DopBadge
import com.example.ui.theme.*

// ============================================================
// LEVEL 2: 360° VIRTUAL TOUR (INTERACTIVE CANVAS & HOTSPOTS)
// ============================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VirtualTour360Screen(
    property: Property,
    lang: AppLanguage,
    onBack: () -> Unit
) {
    val scenes = property.virtualScenes.ifEmpty {
        listOf(
            VirtualScene("ext", "Sebule / Living Lounge", "Living room with large balcony", 0xFF0A192F, "bed"),
            VirtualScene("bed", "Chumba Kikuu / Master Bed", "Spacious room with built-in wardrobe", 0xFF112240, "kitchen"),
            VirtualScene("kitchen", "Jiko / Kitchen", "Fitted granite counters and pantry", 0xFF1E3A5F, null)
        )
    }
    var currentSceneIndex by remember { mutableIntStateOf(0) }
    val currentScene = scenes[currentSceneIndex.coerceIn(0, scenes.size - 1)]

    // Interactive 360 rotation panning state
    var rotationOffset by remember { mutableFloatStateOf(0f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("360° Virtual Tour", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(currentScene.roomName, fontSize = 12.sp, color = DopOchreLight)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DopNavyPrimary)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(DopNavyDark)
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->
                        rotationOffset = (rotationOffset + dragAmount.x * 0.4f) % 360f
                    }
                }
        ) {
            // Simulated 360 panorama surface with room tone & horizon rotation
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(currentScene.panoramaColorHex),
                                DopNavySurface,
                                DopNavyPrimary,
                                Color(currentScene.panoramaColorHex)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Architectural Panorama Grid Horizon
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Explore,
                        contentDescription = "Pan View",
                        tint = DopOchreLight.copy(alpha = 0.8f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = currentScene.roomName,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currentScene.description,
                        color = DopNeutralPearl.copy(alpha = 0.8f),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Buruta kidole chako kuzunguka digrii 360° • Angle: ${rotationOffset.toInt()}°",
                        color = DopOchre,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )

                    // Hotspot Pin to next room
                    Spacer(modifier = Modifier.height(28.dp))
                    Button(
                        onClick = {
                            currentSceneIndex = (currentSceneIndex + 1) % scenes.size
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DopTrustGreen),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("hotspot_navigate_button")
                    ) {
                        Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Tembelea Chumba Kinachofuata / Next Room", fontSize = 12.sp)
                    }
                }
            }

            // Room Switcher Ribbon at bottom
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                color = DopNavySurface.copy(alpha = 0.95f),
                border = BorderStroke(1.dp, DopNavyElevated)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Vyumba vya Nyumba Hii (${scenes.size})",
                        color = DopOchreLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(scenes.indices.toList()) { index ->
                            val scene = scenes[index]
                            val isSelected = index == currentSceneIndex
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) DopOchre else DopNavyElevated,
                                modifier = Modifier.clickable { currentSceneIndex = index }
                            ) {
                                Text(
                                    text = scene.roomName,
                                    color = if (isSelected) Color.White else DopNeutralPearl,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================
// LEVEL 3: LIVE VIRTUAL VIEWING (GUIDE STREAM & MASKED CHAT)
// ============================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveVirtualViewingScreen(
    property: Property,
    lang: AppLanguage,
    onEndSession: () -> Unit
) {
    var isMuted by remember { mutableStateOf(false) }
    var isCameraSwitched by remember { mutableStateOf(false) }
    var chatInput by remember { mutableStateOf("") }
    val chatMessages = remember {
        mutableStateListOf(
            "DoP Guide Rashid: Habari Juma! Nimeingia ndani ya Masaki Apartment. Je, unaniona vizuri?",
            "Juma (Wewe): Habari Rashid! Ndio nakiona vizuri. Tafadhali nionyeshe ukubwa wa kabati za jikoni."
        )
    }

    Scaffold(
        containerColor = DopNavyDark
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Fullscreen Live Guide Camera Feed simulation
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                DopNavySurface,
                                Color(0xFF132A4A),
                                DopNavyDark
                            )
                        )
                    )
            ) {
                // Room watermark & feed visual
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Live Cam",
                        tint = DopTrustGreenLight,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Moja kwa Moja kutoka Masaki, Dar es Salaam",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Guide: Rashid 'Dalali' Mwita • Camera: ${if (isCameraSwitched) "Front/Selfie" else "Back (Wide)"}",
                        color = DopNeutralPearl.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }

            // Top Status Bar: Live badge, Connection quality, End button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DopError
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("LIVE", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Tanzanian 4G Connection Quality Meter
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DopNavyElevated.copy(alpha = 0.9f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.NetworkCheck, contentDescription = null, tint = DopTrustGreenLight, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("4G Dar (32ms)", color = DopNeutralPearl, fontSize = 11.sp)
                    }
                }

                // End Session Button
                IconButton(
                    onClick = onEndSession,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(DopError)
                        .testTag("end_viewing_button")
                ) {
                    Icon(Icons.Default.CallEnd, contentDescription = "End Call", tint = Color.White)
                }
            }

            // Overlay Bottom Sheet: Live In-Call Masked Chat & Control Bar
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Chat bubble log
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DopNavySurface.copy(alpha = 0.85f)),
                    border = BorderStroke(1.dp, DopNavyElevated),
                    modifier = Modifier.fillMaxWidth().heightIn(max = 160.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.padding(10.dp),
                        reverseLayout = true
                    ) {
                        items(chatMessages.reversed()) { msg ->
                            Text(
                                text = msg,
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(vertical = 3.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Chat Input Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = chatInput,
                        onValueChange = { chatInput = it },
                        placeholder = { Text("Uliza swali kwa Guide...", color = DopTextMuted, fontSize = 12.sp) },
                        modifier = Modifier.weight(1f).testTag("live_chat_input"),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = DopOchre,
                            unfocusedBorderColor = DopNavyElevated,
                            focusedContainerColor = DopNavySurface,
                            unfocusedContainerColor = DopNavySurface
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (chatInput.isNotBlank()) {
                                chatMessages.add("Juma (Wewe): $chatInput")
                                chatInput = ""
                            }
                        },
                        modifier = Modifier.clip(CircleShape).background(DopOchre).testTag("live_chat_send_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Call Media Controls (Mute, Camera, Info)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FilledTonalIconButton(
                        onClick = { isMuted = !isMuted },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = if (isMuted) DopError else DopNavyElevated
                        )
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Mute",
                            tint = Color.White
                        )
                    }

                    FilledTonalIconButton(
                        onClick = { isCameraSwitched = !isCameraSwitched },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = DopNavyElevated)
                    ) {
                        Icon(Icons.Default.FlipCameraAndroid, contentDescription = "Switch Camera", tint = Color.White)
                    }

                    FilledTonalIconButton(
                        onClick = {
                            chatMessages.add("Juma (Wewe): Tafadhali nionyeshe mita ya LUKU na tanki la maji.")
                        },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = DopNavyElevated)
                    ) {
                        Icon(Icons.Default.HelpOutline, contentDescription = "Quick Question", tint = DopOchreLight)
                    }
                }
            }
        }
    }
}
