package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.core.auth.AuthManager
import com.example.core.localization.AppLanguage
import com.example.data.model.Property
import com.example.data.model.PropertyShort
import com.example.data.repository.PropertyRepository
import com.example.data.repository.PropertyShortsRepository
import com.example.ui.theme.*

/**
 * Full-screen Vertical Scroll Real Estate Video Shorts / Reels (TikTok / YouTube Shorts style)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyShortsScreen(
    lang: AppLanguage,
    onSelectProperty: (Property) -> Unit,
    onBookTour: (PropertyShort) -> Unit,
    onBack: (() -> Unit)? = null
) {
    val shorts by PropertyShortsRepository.shorts.collectAsState()
    val properties by PropertyRepository.properties.collectAsState()
    val currentUser by AuthManager.currentUser.collectAsState()
    val context = LocalContext.current

    val pagerState = rememberPagerState(pageCount = { shorts.size })
    var selectedShortForComments by remember { mutableStateOf<PropertyShort?>(null) }
    var activeCategory by remember { mutableIntStateOf(0) } // 0 = Kwa Ajili Yako, 1 = Zinazovuma, 2 = Karibu Nawe

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("property_shorts_screen")
    ) {
        if (shorts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.VideoLibrary,
                        contentDescription = null,
                        tint = DopNeutralPearl.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Hakuna video fupi za nyumba kwa sasa.",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val short = shorts[page]
                val matchingProperty = properties.find { it.id == short.propertyId } ?: properties.firstOrNull()

                PropertyShortPageItem(
                    short = short,
                    onLike = { PropertyShortsRepository.toggleLike(short.id) },
                    onOpenComments = { selectedShortForComments = short },
                    onShare = {
                        Toast.makeText(
                            context,
                            "Kiungo cha video ya ${short.propertyTitle} kimenakiliwa!",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onViewFullProperty = {
                        if (matchingProperty != null) {
                            onSelectProperty(matchingProperty)
                        } else {
                            Toast.makeText(context, "Taarifa kamili za nyumba hii zinapakiwa...", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onBookVisit = { onBookTour(short) }
                )
            }
        }

        // Top Category Navigation Tabs (TikTok Style: For You / Trending / Nearby)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 8.dp, start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("shorts_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(48.dp))
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val categories = listOf("Kwa Ajili Yako", "Zinazovuma", "Karibu Nawe")
                categories.forEachIndexed { index, title ->
                    val isSelected = activeCategory == index
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { activeCategory = index }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = title,
                            fontSize = if (isSelected) 14.sp else 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f)
                        )
                        if (isSelected) {
                            Spacer(modifier = Modifier.height(3.dp))
                            Box(
                                modifier = Modifier
                                    .width(18.dp)
                                    .height(2.dp)
                                    .background(DopOchre, RoundedCornerShape(1.dp))
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(48.dp))
        }

        // Comments / Inquiry Modal Bottom Sheet
        if (selectedShortForComments != null) {
            val currentShort = selectedShortForComments!!
            ModalBottomSheet(
                onDismissRequest = { selectedShortForComments = null },
                containerColor = DopNavySurface,
                scrimColor = Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                CommentsBottomSheetContent(
                    short = currentShort,
                    onSendComment = { text ->
                        val userName = currentUser?.name ?: "Mteja DoP"
                        val userRole = when (currentUser?.role) {
                            com.example.data.model.UserRole.OWNER -> "Mwenye Nyumba"
                            com.example.data.model.UserRole.GUIDE -> "DoP Guide"
                            else -> "Mpangaji / Mteja"
                        }
                        PropertyShortsRepository.addComment(
                            shortId = currentShort.id,
                            userName = userName,
                            userRole = userRole,
                            text = text
                        )
                    }
                )
            }
        }
    }
}

/**
 * Individual Short Video Reel Item
 */
@Composable
private fun PropertyShortPageItem(
    short: PropertyShort,
    onLike: () -> Unit,
    onOpenComments: () -> Unit,
    onShare: () -> Unit,
    onViewFullProperty: () -> Unit,
    onBookVisit: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(true) }
    var isMuted by remember { mutableStateOf(false) }

    // Vinyl spinning animation for background sound indicator
    val infiniteTransition = rememberInfiniteTransition(label = "disc_spin")
    val spinAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "disc_angle"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isPlaying = !isPlaying
            }
    ) {
        // High Quality Visual Walkthrough Layer
        AsyncImage(
            model = short.thumbnailUrl,
            contentDescription = short.propertyTitle,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient Scrim Overlays
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.5f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.45f),
                            Color.Black.copy(alpha = 0.92f)
                        )
                    )
                )
        )

        // Paused Indicator Overlay
        if (!isPlaying) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Paused",
                        tint = Color.White,
                        modifier = Modifier.size(42.dp)
                    )
                }
            }
        }

        // Top Right Sound & Video Badge
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 10.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { isMuted = !isMuted },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                    contentDescription = "Sound Toggle",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Right-Side Interaction Column (TikTok Action Bar)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 90.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Owner Profile Avatar
            Box(contentAlignment = Alignment.BottomCenter) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(DopNavyElevated)
                        .clickable { onViewFullProperty() },
                    contentAlignment = Alignment.Center
                ) {
                    if (short.ownerAvatarUrl.isNotBlank()) {
                        AsyncImage(
                            model = short.ownerAvatarUrl,
                            contentDescription = short.ownerName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(Icons.Default.Person, contentDescription = null, tint = DopOchreLight, modifier = Modifier.size(26.dp))
                    }
                }
                Box(
                    modifier = Modifier
                        .offset(y = 6.dp)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(DopTrustGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Like Button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onLike,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.45f))
                        .testTag("shorts_like_btn_${short.id}")
                ) {
                    Icon(
                        imageVector = if (short.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (short.isLiked) Color.Red else Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Text(
                    text = "${short.likesCount}",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Comments / Inquire Button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onOpenComments,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.45f))
                        .testTag("shorts_comments_btn_${short.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Comments",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "${short.commentsCount}",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Share Button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onShare,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.45f))
                        .testTag("shorts_share_btn_${short.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "Shiriki",
                    color = Color.White,
                    fontSize = 10.sp
                )
            }

            // View Full Property Details Shortcut
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onViewFullProperty,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(DopNavyPrimary.copy(alpha = 0.85f))
                        .testTag("shorts_property_btn_${short.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.HomeWork,
                        contentDescription = "View Property",
                        tint = DopOchreLight,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Text(
                    text = "Nyumba",
                    color = DopOchreLight,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Spinning Sound Disc Icon
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .rotate(if (isPlaying) spinAngle else 0f),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = DopOchreLight,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Bottom Property Information Overlay
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.80f)
                .padding(start = 16.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Owner Name & Location Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "@${short.ownerName}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DopTrustGreen.copy(alpha = 0.9f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = Color.White, modifier = Modifier.size(11.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Mwenye Nyumba", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Location Tag
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = DopOchreLight, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = short.location,
                    fontSize = 12.sp,
                    color = DopNeutralPearl.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium
                )
            }

            // Property Title & Price Highlight
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = DopNavyElevated.copy(alpha = 0.85f),
                border = BorderStroke(1.dp, DopOchre.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = short.propertyTitle,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = short.priceDisplay,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DopOchreLight
                    )
                }
            }

            // Description and Tags
            Text(
                text = short.description,
                fontSize = 11.sp,
                color = DopNeutralPearl.copy(alpha = 0.85f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 15.sp
            )

            // Hashtags
            Text(
                text = short.tags.joinToString(" "),
                fontSize = 11.sp,
                color = DopOchreLight,
                fontWeight = FontWeight.SemiBold
            )

            // CTA Button: Weka Nafasi ya Ziara / Ukaguzi
            Button(
                onClick = onBookVisit,
                colors = ButtonDefaults.buttonColors(containerColor = DopOchre),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                modifier = Modifier
                    .height(38.dp)
                    .testTag("shorts_book_visit_btn_${short.id}")
            ) {
                Icon(Icons.Default.DirectionsWalk, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Panga Ziara ya Ukaguzi",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Comments & Inquiry Modal Content for a Short Reel
 */
@Composable
private fun CommentsBottomSheetContent(
    short: PropertyShort,
    onSendComment: (String) -> Unit
) {
    var newCommentText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Maswali na Maoni ya Wapangaji",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${short.comments.size} maoni kwenye ${short.propertyTitle}",
                    fontSize = 11.sp,
                    color = DopOchreLight
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Comments List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .heightIn(max = 280.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (short.comments.isEmpty()) {
                item {
                    Text(
                        text = "Kuwa wa kwanza kuuliza swali kuhusu nyumba hii!",
                        fontSize = 12.sp,
                        color = DopNeutralPearl.copy(alpha = 0.6f),
                        modifier = Modifier.padding(vertical = 20.dp)
                    )
                }
            } else {
                items(short.comments) { comment ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DopNavyElevated.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(DopOchreContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = DopOchre, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = comment.userName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = comment.timeAgo,
                                    fontSize = 9.sp,
                                    color = DopNeutralPearl.copy(alpha = 0.5f)
                                )
                            }
                            Text(
                                text = comment.userRole,
                                fontSize = 9.sp,
                                color = DopOchreLight
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = comment.comment,
                                fontSize = 11.sp,
                                color = DopNeutralPearl,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Input Field to Send Question / Comment
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newCommentText,
                onValueChange = { newCommentText = it },
                placeholder = {
                    Text(
                        "Uliza kuhusu kodi, umeme, maji au fensi...",
                        fontSize = 11.sp,
                        color = DopNeutralPearl.copy(alpha = 0.5f)
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("shorts_comment_input"),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DopOchre,
                    unfocusedBorderColor = DopNavyElevated,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = DopNavyElevated.copy(alpha = 0.5f),
                    unfocusedContainerColor = DopNavyElevated.copy(alpha = 0.5f)
                ),
                maxLines = 1
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (newCommentText.isNotBlank()) {
                        onSendComment(newCommentText)
                        newCommentText = ""
                    }
                },
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(DopOchre)
                    .testTag("shorts_send_comment_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Tuma",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
