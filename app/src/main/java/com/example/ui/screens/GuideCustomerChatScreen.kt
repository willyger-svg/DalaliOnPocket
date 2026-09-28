package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.auth.AuthManager
import com.example.core.localization.AppLanguage
import com.example.data.model.ChatAttachmentType
import com.example.data.model.GuideChatMessage
import com.example.data.model.UserRole
import com.example.data.repository.GuideChatRepository
import com.example.data.repository.PropertyRepository
import com.example.ui.components.DopBadge
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideCustomerChatScreen(
    lang: AppLanguage,
    bookingCode: String = "DOP-BK-9182",
    onBack: () -> Unit,
    onCallPhone: (String) -> Unit = {}
) {
    val currentUser by AuthManager.currentUser.collectAsState()
    val userRole = currentUser?.role ?: UserRole.CUSTOMER
    val isCustomer = userRole == UserRole.CUSTOMER

    val allMessages by GuideChatRepository.messages.collectAsState()
    val relevantMessages = remember(allMessages, bookingCode) {
        allMessages.filter { it.bookingCode == bookingCode }
    }

    val bookings by PropertyRepository.viewingBookings.collectAsState()
    val activeBooking = remember(bookings, bookingCode) {
        bookings.find { it.bookingCode == bookingCode } ?: bookings.firstOrNull()
    }

    val counterpartName = if (isCustomer) {
        activeBooking?.guideName ?: "Rashid 'Dalali' Mwita (Guide)"
    } else {
        activeBooking?.customerName ?: "Baraka Mushi (Mteja)"
    }

    val counterpartPhone = if (isCustomer) {
        activeBooking?.guidePhone ?: "+255 754 819 201"
    } else {
        "+255 768 112 304"
    }

    val propertyTitle = activeBooking?.propertyTitle ?: "Mikocheni B Beachfront Villa"

    var inputText by remember { mutableStateOf("") }
    var showAttachmentSheet by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(relevantMessages.size) {
        if (relevantMessages.isNotEmpty()) {
            listState.animateScrollToItem(relevantMessages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (isCustomer) DopTrustGreen else DopNavyPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = counterpartName.take(2).uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            // Online indicator dot
                            Box(
                                modifier = Modifier
                                    .size(11.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF22C55E))
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = counterpartName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isCustomer) "DoP Guide Aliyepangiwa • Online" else "Mteja wa Ziara • Online",
                                    fontSize = 11.sp,
                                    color = DopTrustGreen
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("chat_back_btn")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onCallPhone(counterpartPhone) },
                        modifier = Modifier.testTag("chat_call_btn")
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = "Call", tint = DopTrustGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .navigationBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                // Quick Suggestion Chips
                val quickChips = if (isCustomer) {
                    listOf(
                        "Nimefika getini nasubiri 🚪",
                        "Nipo njiani, foleni ya dakika 10 🚗",
                        "Naomba kuelekezwa njia rahisi 🗺️",
                        "Je, mwenye nyumba yupo hapo? 🔑",
                        "Maji ya DAWASA na Luku viko tayari? 💡"
                    )
                } else {
                    listOf(
                        "Nimefika eneo la nyumba tayari 📍",
                        "Nimesimama getini nina funguo 🔑",
                        "Karibu sana, nakuona hapa nje 👋",
                        "Luku na maji yote yako tayari 💧",
                        "Nipo njia panda nakusubiri ⏳"
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    items(quickChips) { chipText ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable {
                                GuideChatRepository.sendMessage(
                                    bookingCode = bookingCode,
                                    propertyTitle = propertyTitle,
                                    senderRole = userRole,
                                    senderName = currentUser?.name ?: if (isCustomer) "Mteja" else "Guide",
                                    text = chipText
                                )
                            }
                        ) {
                            Text(
                                text = chipText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Input Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { showAttachmentSheet = true },
                        modifier = Modifier.testTag("chat_attachment_btn")
                    ) {
                        Icon(
                            Icons.Default.AddCircleOutline,
                            contentDescription = "Attach",
                            tint = DopOchre
                        )
                    }

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                text = if (isCustomer) "Andika ujumbe kwa Guide..." else "Andika ujumbe kwa Mteja...",
                                fontSize = 13.sp
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field"),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                val textToSend = inputText
                                inputText = ""
                                GuideChatRepository.sendMessage(
                                    bookingCode = bookingCode,
                                    propertyTitle = propertyTitle,
                                    senderRole = userRole,
                                    senderName = currentUser?.name ?: if (isCustomer) "Mteja" else "Guide",
                                    text = textToSend
                                )
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .testTag("chat_send_btn")
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Context Header: Booking Details
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(DopOchreContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.HomeWork, contentDescription = null, tint = DopOchre)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = propertyTitle,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Kodi ya Ziara: $bookingCode • Saa 10:00 Jioni",
                                fontSize = 11.sp,
                                color = DopTextSecondary
                            )
                        }
                        DopBadge("VERIFIED", Icons.Default.Verified, DopTrustGreen, DopTrustGreenContainer)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Chat Messages
            items(relevantMessages) { msg ->
                val isMyMessage = msg.senderRole == userRole
                ChatMessageBubble(msg = msg, isMyMessage = isMyMessage)
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    // Attachment Dialog / Bottom Sheet
    if (showAttachmentSheet) {
        AlertDialog(
            onDismissRequest = { showAttachmentSheet = false },
            title = { Text("Chagua Taarifa ya Kutuma") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showAttachmentSheet = false
                                GuideChatRepository.sendMessage(
                                    bookingCode = bookingCode,
                                    propertyTitle = propertyTitle,
                                    senderRole = userRole,
                                    senderName = currentUser?.name ?: if (isCustomer) "Mteja" else "Guide",
                                    text = if (isCustomer) "Hapa ndipo nilipo sasa hivi (GPS Location)" else "Hapa ndipo geti la nyumba lilipo (GPS Location)",
                                    attachmentType = ChatAttachmentType.LOCATION_PIN,
                                    attachmentData = "Mikocheni B, Karibu na Shoppers Plaza (-6.7621, 39.2458)"
                                )
                            }
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFE53935))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Tuma GPS Location ya Sasa", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Shiriki eneo halisi ili mpate kuonana haraka", fontSize = 11.sp, color = DopTextSecondary)
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showAttachmentSheet = false
                                GuideChatRepository.sendMessage(
                                    bookingCode = bookingCode,
                                    propertyTitle = propertyTitle,
                                    senderRole = userRole,
                                    senderName = currentUser?.name ?: if (isCustomer) "Mteja" else "Guide",
                                    text = "Uhakiki wa Huduma: Luku na Maji",
                                    attachmentType = ChatAttachmentType.QUICK_NOTE,
                                    attachmentData = "Umeme: Luku Inafanya Kazi • Maji: DAWASA Yanatoka • Parking: Gari 2"
                                )
                            }
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = DopOchre)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Taarifa za Maji na Umeme (Luku/DAWASA)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Thibitisha hali ya huduma muhimu", fontSize = 11.sp, color = DopTextSecondary)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAttachmentSheet = false }) {
                    Text("Funga")
                }
            }
        )
    }
}

@Composable
private fun ChatMessageBubble(
    msg: GuideChatMessage,
    isMyMessage: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMyMessage) Alignment.End else Alignment.Start
    ) {
        // Sender Label
        Text(
            text = msg.senderName,
            fontSize = 10.sp,
            color = DopTextMuted,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMyMessage) 16.dp else 4.dp,
                bottomEnd = if (isMyMessage) 4.dp else 16.dp
            ),
            color = if (isMyMessage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            shadowElevation = 1.dp,
            modifier = Modifier.widthIn(max = 290.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                // Main Text
                Text(
                    text = msg.messageText,
                    fontSize = 13.sp,
                    color = if (isMyMessage) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    lineHeight = 18.sp
                )

                // Attachment Card if present
                if (msg.attachmentType != ChatAttachmentType.NONE && msg.attachmentData != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isMyMessage) Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val icon = when (msg.attachmentType) {
                                ChatAttachmentType.LOCATION_PIN -> Icons.Default.Place
                                ChatAttachmentType.QUICK_NOTE -> Icons.Default.CheckCircleOutline
                                else -> Icons.Default.Info
                            }
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isMyMessage) Color.White else DopOchre,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = msg.attachmentData,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isMyMessage) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Timestamp & Checkmarks
                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = msg.timestamp,
                        fontSize = 9.sp,
                        color = if (isMyMessage) Color.White.copy(alpha = 0.75f) else DopTextMuted
                    )
                    if (isMyMessage) {
                        Spacer(modifier = Modifier.width(3.dp))
                        Icon(
                            Icons.Default.DoneAll,
                            contentDescription = "Read",
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}
