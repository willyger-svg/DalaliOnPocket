package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.core.auth.AuthManager
import com.example.core.market.MarketService
import com.example.core.market.Region
import com.example.core.localization.AppLanguage
import com.example.core.localization.DoPStrings
import com.example.core.sync.SyncManager
import com.example.data.model.*
import com.example.data.repository.PropertyRepository
import com.example.data.repository.PropertyShortsRepository
import com.example.ui.components.*
import com.example.ui.theme.*

// ============================================================
// 1. CUSTOMER HOME SCREEN
// ============================================================
@Composable
fun CustomerHomeScreen(
    lang: AppLanguage,
    onSelectProperty: (Property) -> Unit,
    onOpen360Tour: (Property) -> Unit,
    onOpenLiveTour: (Property) -> Unit,
    onOpenShorts: () -> Unit = {}
) {
    val properties by PropertyRepository.properties.collectAsState()
    val savedIds by PropertyRepository.savedPropertyIds.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "Nyumba", "Apartimenti", "Vyumba", "Biashara", "Viwanja")

    val filteredProperties = properties.filter { prop ->
        val matchesCategory = when (selectedCategory) {
            "Nyumba" -> prop.propertyType == PropertyType.HOUSE
            "Apartimenti" -> prop.propertyType == PropertyType.APARTMENT
            "Vyumba" -> prop.propertyType == PropertyType.ROOM
            "Biashara" -> prop.propertyType.category == PropertyCategory.COMMERCIAL
            "Viwanja" -> prop.propertyType.category == PropertyCategory.LAND
            else -> true
        }
        val matchesSearch = searchQuery.isBlank() ||
                prop.title.contains(searchQuery, ignoreCase = true) ||
                prop.ward.contains(searchQuery, ignoreCase = true) ||
                prop.district.contains(searchQuery, ignoreCase = true) ||
                prop.region.contains(searchQuery, ignoreCase = true)

        matchesCategory && matchesSearch
    }.sortedByDescending { it.isBoosted || it.boostTier != ListingBoostTier.STANDARD }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DopNeutralPearl)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Hero Bento Banner
        item {
            DopBentoCard(backgroundColor = DopNavyPrimary) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = DoPStrings.heroHeadline(lang),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = DoPStrings.heroSubhead(lang),
                            fontSize = 12.sp,
                            color = DopNeutralPearl.copy(alpha = 0.8f)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = DopNavyElevated
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "Verified Ecosystem",
                            tint = DopOchreLight,
                            modifier = Modifier.padding(10.dp).size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Search Box
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = DoPStrings.searchPlaceholder(lang),
                            fontSize = 12.sp,
                            color = DopTextMuted
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = DopOchreLight)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.White)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_search_field"),
                    shape = RoundedCornerShape(16.dp),
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
            }
        }

        // Category Pills
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    val isSelected = cat == selectedCategory
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) DopNavyPrimary else DopSurfaceCard,
                        border = BorderStroke(1.dp, if (isSelected) DopNavyPrimary else DopBorderSubtle),
                        modifier = Modifier.clickable { selectedCategory = cat }
                    ) {
                        Text(
                            text = cat,
                            color = if (isSelected) Color.White else DopTextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // DoP Shorts Video Feed Carousel (TikTok / YouTube Shorts style walkthroughs)
        item {
            val shortsList by PropertyShortsRepository.shorts.collectAsState()
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE53935)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Short Videos (Ziara za Ndani)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DopNavyPrimary
                        )
                    }
                    TextButton(onClick = onOpenShorts) {
                        Text("Tazama Zote →", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DopOchre)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(shortsList) { itemShort ->
                        Card(
                            modifier = Modifier
                                .width(135.dp)
                                .height(210.dp)
                                .clickable { onOpenShorts() }
                                .testTag("home_short_card_${itemShort.id}"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                AsyncImage(
                                    model = itemShort.thumbnailUrl,
                                    contentDescription = itemShort.propertyTitle,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                                            )
                                        )
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFE53935),
                                    modifier = Modifier.padding(6.dp).align(Alignment.TopStart)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("SHORT", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                                    }
                                }

                                Column(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = itemShort.propertyTitle,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = itemShort.location,
                                        color = DopNeutralPearl.copy(alpha = 0.85f),
                                        fontSize = 9.sp,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.Red, modifier = Modifier.size(10.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("${itemShort.likesCount}", color = Color.White, fontSize = 9.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // DoP Express Featured Shelf
        item {
            val expressList = properties.filter { it.isDopExpress }
            if (expressList.isNotEmpty()) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            DopExpressBadge()
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Ziara ya Haraka na Guide",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DopNavyPrimary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(expressList) { prop ->
                            ExpressPropertyCard(
                                property = prop,
                                lang = lang,
                                onClick = { onSelectProperty(prop) }
                            )
                        }
                    }
                }
            }
        }

        // 360° Virtual Tour Spotlight Banner
        item {
            val tourProperty = properties.firstOrNull { it.hasVirtualTour }
            if (tourProperty != null) {
                DopBentoCard(
                    backgroundColor = DopNavySurface,
                    borderColor = DopOchre,
                    onClick = { onOpen360Tour(tourProperty) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            DopBadge("360° VIRTUAL TOUR", Icons.Default.Explore, DopOchreLight, DopNavyElevated)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Tembelea Nyumba ya Masaki Kielektroniki",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Zunguka vyumba vyote vya ndani kabla ya kufanya ziara halisi.",
                                fontSize = 11.sp,
                                color = DopNeutralPearl.copy(alpha = 0.7f)
                            )
                        }
                        Button(
                            onClick = { onOpen360Tour(tourProperty) },
                            colors = ButtonDefaults.buttonColors(containerColor = DopOchre),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("home_360_spotlight_btn")
                        ) {
                            Text("Anza Sasa", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section Title: Verified Listings in Tanzania
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mali Zilizothibitishwa (${filteredProperties.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DopNavyPrimary
                )
                Text(
                    text = "Tanzania",
                    fontSize = 12.sp,
                    color = DopTrustGreen,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Property List Cards
        items(filteredProperties) { property ->
            PropertyCard(
                property = property,
                lang = lang,
                isSaved = savedIds.contains(property.id),
                onSaveToggle = { PropertyRepository.toggleSave(property.id) },
                onClick = { onSelectProperty(property) },
                onVirtualTourClick = { onOpen360Tour(property) },
                onLiveViewingClick = { onOpenLiveTour(property) }
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

// ============================================================
// PROPERTY CARD COMPONENT
// ============================================================
@Composable
fun PropertyCard(
    property: Property,
    lang: AppLanguage,
    isSaved: Boolean,
    onSaveToggle: () -> Unit,
    onClick: () -> Unit,
    onVirtualTourClick: () -> Unit,
    onLiveViewingClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("property_card_${property.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DopSurfaceCard),
        border = BorderStroke(1.dp, DopBorderSubtle),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Image Header with Badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                val imageUrl = property.images.firstOrNull() ?: ""
                AsyncImage(
                    model = imageUrl,
                    contentDescription = property.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark subtle gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Black.copy(alpha = 0.4f), Color.Transparent, Color.Black.copy(alpha = 0.6f))
                            )
                        )
                )

                // Top Floating Badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (property.isBoosted || property.boostTier != ListingBoostTier.STANDARD) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = DopOchre,
                                shadowElevation = 2.dp
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.White, modifier = Modifier.size(11.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = property.boostTier.name,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                        if (property.isDopExpress) {
                            DopExpressBadge()
                        }
                        LocationPrivacyChip(privacy = property.locationPrivacy)
                    }

                    // Save / Bookmark Icon Button
                    IconButton(
                        onClick = onSaveToggle,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .size(36.dp)
                            .testTag("save_btn_${property.id}")
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Save",
                            tint = if (isSaved) DopError else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Bottom Floating Specs on image
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "${property.ward}, ${property.region}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (property.hasVirtualTour) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = DopNavyPrimary.copy(alpha = 0.85f),
                                modifier = Modifier.clickable { onVirtualTourClick() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Explore, contentDescription = null, tint = DopOchreLight, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("360°", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        if (property.hasLiveViewing) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = DopError.copy(alpha = 0.85f),
                                modifier = Modifier.clickable { onLiveViewingClick() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("LIVE", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Card Body Details
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = property.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = DopNavyPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Price Row & Verified Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DopPriceDisplay(
                        priceTzs = property.priceTzs,
                        transactionType = property.transactionType,
                        lang = lang
                    )
                    DopBadge("VERIFIED", Icons.Default.CheckCircle, DopTrustGreen, DopTrustGreenContainer)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Room & Area Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (property.bedrooms > 0) {
                        IconTextChip(Icons.Default.Bed, "${property.bedrooms} Beds")
                    }
                    if (property.bathrooms > 0) {
                        IconTextChip(Icons.Default.Bathtub, "${property.bathrooms} Baths")
                    }
                    if (property.areaSqm > 0) {
                        IconTextChip(Icons.Default.SquareFoot, "${property.areaSqm} m²")
                    }
                    IconTextChip(Icons.Default.Bolt, "Luku")
                }
            }
        }
    }
}

@Composable
private fun ExpressPropertyCard(
    property: Property,
    lang: AppLanguage,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(240.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DopSurfaceCard),
        border = BorderStroke(1.dp, DopBorderSubtle)
    ) {
        Column {
            Box(modifier = Modifier.height(120.dp).fillMaxWidth()) {
                AsyncImage(
                    model = property.images.firstOrNull() ?: "",
                    contentDescription = property.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                DopExpressBadge(modifier = Modifier.padding(6.dp))
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = property.title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${property.ward}, ${property.region}",
                    fontSize = 10.sp,
                    color = DopTextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = DoPStrings.tzs(property.priceTzs) + " /mwezi",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DopNavyPrimary
                )
            }
        }
    }
}

@Composable
private fun IconTextChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = DopTextSecondary, modifier = Modifier.size(13.dp))
        Spacer(modifier = Modifier.width(3.dp))
        Text(text = text, fontSize = 11.sp, color = DopTextSecondary)
    }
}

// ============================================================
// 2. EXPLORE & FILTER SCREEN
// ============================================================
@Composable
fun CustomerExploreScreen(
    lang: AppLanguage,
    onSelectProperty: (Property) -> Unit
) {
    val properties by PropertyRepository.properties.collectAsState()
    val savedIds by PropertyRepository.savedPropertyIds.collectAsState()

    var selectedRegion by remember { mutableStateOf("Zote") }
    var selectedType by remember { mutableStateOf("Zote") }
    var showOnlyExpress by remember { mutableStateOf(false) }

    val currentMarket by MarketService.currentMarket.collectAsState()
    
    // Support filters for relevant Dar areas (Districts)
    val regions = if (currentMarket == Region.DAR_ES_SALAAM) {
        listOf("Zote", "Kinondoni", "Ilala", "Temeke", "Ubungo", "Kigamboni")
    } else {
        listOf("Zote")
    }

    val filteredList = properties.filter { prop ->
        // Only show properties in the current active market
        val inMarket = prop.region.contains(currentMarket.displayName, ignoreCase = true) || prop.district.contains(currentMarket.displayName, ignoreCase = true) || currentMarket == Region.DAR_ES_SALAAM && prop.region.contains("Dar", ignoreCase = true)
        
        val matchesFilter = selectedRegion == "Zote" || prop.district.contains(selectedRegion, ignoreCase = true) || prop.ward.contains(selectedRegion, ignoreCase = true)
        
        inMarket && matchesFilter && (!showOnlyExpress || prop.isDopExpress)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DopNeutralPearl)
            .padding(16.dp)
    ) {
        Text(
            text = "Tafuta Mali nchini Tanzania",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = DopNavyPrimary
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Region Filter Pills
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(regions) { reg ->
                val isSel = reg == selectedRegion
                FilterChip(
                    selected = isSel,
                    onClick = { selectedRegion = reg },
                    label = { Text(reg, fontSize = 11.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Express Toggle & Count
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredList.size} Mali zimepatikana",
                fontSize = 12.sp,
                color = DopTextSecondary,
                fontWeight = FontWeight.Medium
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("DoP Express pekee", fontSize = 11.sp, color = DopNavyPrimary)
                Spacer(modifier = Modifier.width(6.dp))
                Switch(
                    checked = showOnlyExpress,
                    onCheckedChange = { showOnlyExpress = it },
                    modifier = Modifier.testTag("express_filter_switch")
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Results List
        LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            items(filteredList) { prop ->
                PropertyCard(
                    property = prop,
                    lang = lang,
                    isSaved = savedIds.contains(prop.id),
                    onSaveToggle = { PropertyRepository.toggleSave(prop.id) },
                    onClick = { onSelectProperty(prop) },
                    onVirtualTourClick = { onSelectProperty(prop) },
                    onLiveViewingClick = { onSelectProperty(prop) }
                )
            }
        }
    }
}

// ============================================================
// 3. SAVED / BOOKMARKS SCREEN
// ============================================================
@Composable
fun CustomerSavedScreen(
    lang: AppLanguage,
    onSelectProperty: (Property) -> Unit
) {
    val properties by PropertyRepository.properties.collectAsState()
    val savedIds by PropertyRepository.savedPropertyIds.collectAsState()
    val savedList = properties.filter { savedIds.contains(it.id) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DopNeutralPearl)
            .padding(16.dp)
    ) {
        Text(
            text = "Mali Zilizohifadhiwa (${savedList.size})",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = DopNavyPrimary
        )
        Text(
            text = "Orodha yako ya nyumba na viwanja unavyovipenda.",
            fontSize = 12.sp,
            color = DopTextSecondary
        )
        Spacer(modifier = Modifier.height(14.dp))

        if (savedList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = DopTextMuted, modifier = Modifier.size(54.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Bado haujahifadhi nyumba yoyote.", color = DopTextSecondary, fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                items(savedList) { prop ->
                    PropertyCard(
                        property = prop,
                        lang = lang,
                        isSaved = true,
                        onSaveToggle = { PropertyRepository.toggleSave(prop.id) },
                        onClick = { onSelectProperty(prop) },
                        onVirtualTourClick = { onSelectProperty(prop) },
                        onLiveViewingClick = { onSelectProperty(prop) }
                    )
                }
            }
        }
    }
}

// ============================================================
// 4. VISITS / BOOKINGS SCREEN WITH STATE MACHINE PROGRESS
// ============================================================
@Composable
fun CustomerVisitsScreen(
    lang: AppLanguage,
    onOpenLiveTour: (Property) -> Unit,
    onOpenChat: (ViewingBooking) -> Unit = {}
) {
    val bookings by PropertyRepository.viewingBookings.collectAsState()
    val properties by PropertyRepository.properties.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DopNeutralPearl)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Miadi na Ziara za Nyumba (Visits)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DopNavyPrimary
            )
            Text(
                text = "Ufuatiliaji wa moja kwa moja wa mwongozo (DoP Guide) na ziara zako.",
                fontSize = 12.sp,
                color = DopTextSecondary
            )
        }

        items(bookings) { booking ->
            DopBentoCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = booking.bookingCode,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = DopNavyPrimary
                    )
                    DopBadge(
                        text = booking.status.name,
                        icon = Icons.Default.DirectionsWalk,
                        color = DopTrustGreen,
                        backgroundColor = DopTrustGreenContainer
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = booking.propertyTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = "Eneo: ${booking.propertyLocation}", fontSize = 12.sp, color = DopTextSecondary)
                Text(text = "Tarehe: ${booking.scheduledDate}", fontSize = 12.sp, color = DopOchre, fontWeight = FontWeight.Medium)
                Text(text = "Sehemu ya Kukutana: ${booking.meetingPoint}", fontSize = 11.sp, color = DopTextMuted)

                // State Machine Progress Indicator
                Spacer(modifier = Modifier.height(12.dp))
                ViewingProgressBar(status = booking.status)

                // Guide Dispatch details & Direct Chat
                if (booking.guideName != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(DopTrustGreen),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = booking.guideName.take(2).uppercase(),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("DoP Guide Aliyepangiwa:", fontSize = 10.sp, color = DopTextSecondary)
                                        Text(booking.guideName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(booking.guidePhone ?: "", fontSize = 11.sp, color = DopTrustGreen)
                                    }
                                }
                                Icon(Icons.Default.Phone, contentDescription = "Call Guide", tint = DopTrustGreen)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Direct Chat Button for Extra Conversation
                            Button(
                                onClick = { onOpenChat(booking) },
                                colors = ButtonDefaults.buttonColors(containerColor = DopNavyPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("customer_chat_guide_${booking.bookingCode}")
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Chat na Guide (Mazungumzo ya Ziada)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ViewingProgressBar(status: ViewingStatus) {
    val stepIndex = when (status) {
        ViewingStatus.REQUESTED -> 1
        ViewingStatus.GUIDE_ASSIGNED -> 2
        ViewingStatus.GUIDE_ON_THE_WAY -> 3
        ViewingStatus.GUIDE_ARRIVED -> 4
        ViewingStatus.VIEWING_IN_PROGRESS -> 5
        ViewingStatus.COMPLETED -> 6
        else -> 2
    }

    Column {
        LinearProgressIndicator(
            progress = { stepIndex / 6f },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = if (stepIndex == 6) DopTrustGreen else DopOchre,
            trackColor = DopBorderSubtle
        )
        Spacer(modifier = Modifier.height(4.dp))
        val stepLabel = when (stepIndex) {
            1 -> "1/6 Ombi Limetumwa (Requested)"
            2 -> "2/6 Guide Amepangiwa (Guide Assigned)"
            3 -> "3/6 Guide Yupo Njia Panda (On the Way)"
            4 -> "4/6 Guide Amefika Eneo la Nyumba (Arrived)"
            5 -> "5/6 Ukaguzi Unaendelea (Viewing In Progress)"
            6 -> "6/6 Ziara Imekamilika (Completed)"
            else -> "Inaendelea"
        }
        Text(text = stepLabel, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = DopNavyPrimary)
    }
}

// ============================================================
// 5. ACCOUNT SCREEN & ROLE SWITCHER
// ============================================================
@Composable
fun CustomerAccountScreen(
    lang: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onRoleChange: (UserRole) -> Unit,
    onViewAgreements: () -> Unit
) {
    val currentUser by AuthManager.currentUser.collectAsState()
    val isConnected by SyncManager.isConnected.collectAsState()

    var showRoleDialog by remember { mutableStateOf(false) }
    var showOwnerOnboardingDialog by remember { mutableStateOf(false) }
    var showGuideApplicationDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DopNeutralPearl)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Profile Card
        item {
            val user = currentUser
            val currentMode = user?.activeMode ?: UserRole.CUSTOMER
            val roleTokens = DopRoleColors.forRole(currentMode)

            DopBentoCard(backgroundColor = DopNavyPrimary) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(roleTokens.roleAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (user?.name ?: "DoP").take(2).uppercase(),
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = user?.name ?: "Mtumiaji", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                        Text(text = user?.email ?: "", fontSize = 11.sp, color = DopNeutralPearl.copy(alpha = 0.7f))
                        Text(text = user?.phone ?: "", fontSize = 11.sp, color = DopOchreLight)
                    }
                    DopBadge(currentMode.name, null, roleTokens.roleAccent, roleTokens.roleBadgeBackground)
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                Spacer(modifier = Modifier.height(10.dp))

                // Capabilities Status Badges
                Text("Uwezo wa Akaunti Yako (Capabilities):", fontSize = 11.sp, color = DopNeutralPearl.copy(alpha = 0.8f))
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Customer capability (always active)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DopCustomerBlueContainer,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Customer", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DopCustomerBlueDark)
                            Text("ACTIVE", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = DopCustomerBlue)
                        }
                    }

                    // Owner capability
                    val hasOwner = user?.canAccessOwner == true
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (hasOwner) DopOwnerGreenContainer else Color.White.copy(alpha = 0.1f),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (!hasOwner) showOwnerOnboardingDialog = true
                            }
                    ) {
                        Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Owner", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (hasOwner) DopOwnerGreenDark else Color.White)
                            Text(if (hasOwner) "ACTIVE" else "+ WASHA", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = if (hasOwner) DopOwnerGreen else DopOchreLight)
                        }
                    }

                    // Guide capability
                    val guideStatus = user?.guideCapability ?: GuideCapabilityStatus.NOT_APPLIED
                    val isGuideApproved = guideStatus == GuideCapabilityStatus.APPROVED
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isGuideApproved) DopGuideAmberContainer else Color.White.copy(alpha = 0.1f),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (guideStatus == GuideCapabilityStatus.NOT_APPLIED) showGuideApplicationDialog = true
                            }
                    ) {
                        Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Guide", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isGuideApproved) DopGuideAmberDark else Color.White)
                            val guideLabel = when (guideStatus) {
                                GuideCapabilityStatus.APPROVED -> "ACTIVE"
                                GuideCapabilityStatus.SUBMITTED -> "REVIEW"
                                GuideCapabilityStatus.SUSPENDED -> "SUSPENDED"
                                GuideCapabilityStatus.REJECTED -> "REJECTED"
                                else -> "+ OMBA"
                            }
                            Text(guideLabel, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = if (isGuideApproved) DopGuideAmberDark else DopOchreLight)
                        }
                    }
                }
            }
        }

        // Under Review Banner (if Guide capability is pending admin review)
        if (currentUser?.guideCapability == GuideCapabilityStatus.SUBMITTED) {
            item {
                GuidePendingReviewCard(
                    application = currentUser?.guideApplication,
                    onSimulateApprove = {
                        currentUser?.id?.let { com.example.core.account.AccountManager.adminApproveGuide(it) }
                    },
                    onSimulateReject = {
                        currentUser?.id?.let { com.example.core.account.AccountManager.adminRejectGuide(it) }
                    }
                )
            }
        }

        // Quick Capability Switcher Trigger (Customer, Owner, Guide)
        item {
            val user = currentUser
            val currentMode = user?.activeMode ?: UserRole.CUSTOMER
            val roleTokens = DopRoleColors.forRole(currentMode)

            DopBentoCard(
                backgroundColor = roleTokens.roleAccentContainer,
                borderColor = roleTokens.roleBorder,
                onClick = { showRoleDialog = true }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = roleTokens.roleAccent)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Badilisha Mwonekano / Switch Capability", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = roleTokens.roleAccent)
                            Text("Mwonekano wa Sasa: ${currentMode.name} (Bofya kubadili au kuongeza uwezo)", fontSize = 11.sp, color = DopTextSecondary)
                        }
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = roleTokens.roleAccent)
                }
            }
        }

        // Tenancy Agreement Hub Link
        item {
            DopBentoCard(onClick = onViewAgreements) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Article, contentDescription = null, tint = DopNavyPrimary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Mikataba ya Upangaji (Tenancy Agreements)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Kagua makubaliano na kodi ya DoP ya 50%", fontSize = 11.sp, color = DopTextSecondary)
                        }
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = DopTextSecondary)
                }
            }
        }

        // Language Settings
        item {
            DopBentoCard {
                Text(text = "Lugha ya Mfumo (Language)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { onLanguageChange(AppLanguage.SWAHILI) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (lang == AppLanguage.SWAHILI) DopNavyPrimary else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Kiswahili", color = if (lang == AppLanguage.SWAHILI) Color.White else DopTextPrimary)
                    }
                    Button(
                        onClick = { onLanguageChange(AppLanguage.ENGLISH) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (lang == AppLanguage.ENGLISH) DopNavyPrimary else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("English", color = if (lang == AppLanguage.ENGLISH) Color.White else DopTextPrimary)
                    }
                }
            }
        }

        // App Theme Selector (Mandhari Nyingi za Programu - 10 Presets)
        item {
            val currentThemePreset by ThemeManager.currentPreset.collectAsState()
            var isThemeExpanded by remember { mutableStateOf(true) }

            DopBentoCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(currentThemePreset.primaryColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(currentThemePreset.emoji, fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("🎨 Mandhari ya Programu (Themes)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(
                                text = "Ya Sasa: ${currentThemePreset.nameSwahili}",
                                fontSize = 11.sp,
                                color = DopTextSecondary
                            )
                        }
                    }
                    TextButton(
                        onClick = { isThemeExpanded = !isThemeExpanded },
                        modifier = Modifier.testTag("toggle_themes_btn")
                    ) {
                        Text(
                            text = if (isThemeExpanded) "Funga ▲" else "Badilisha (${AppThemePreset.values().size}) ▼",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = DopOchre
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Active Theme Quick Palette preview
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Rangi Kuu:", fontSize = 11.sp, color = DopTextMuted)
                    Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(currentThemePreset.primaryColor))
                    Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(currentThemePreset.secondaryColor))
                    Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(currentThemePreset.accentColor))
                    Spacer(modifier = Modifier.weight(1f))
                    DopBadge(
                        text = if (currentThemePreset.isDark) "DARK AMOLED" else "LIGHT MODE",
                        icon = if (currentThemePreset.isDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                        color = currentThemePreset.primaryColor,
                        backgroundColor = currentThemePreset.surfaceVariantColor
                    )
                }

                // Expanded list of all 10 themes with click to change
                if (isThemeExpanded) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Chagua mandhari unayopenda (Gusa kubadilisha mfumo mzima):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DopNavyPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        AppThemePreset.values().forEach { preset ->
                            val isSelected = preset == currentThemePreset
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) preset.surfaceVariantColor else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                border = if (isSelected) BorderStroke(2.dp, preset.primaryColor) else BorderStroke(0.5.dp, DopBorderSubtle),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { ThemeManager.setPreset(preset) }
                                    .testTag("theme_preset_${preset.id}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(preset.emoji, fontSize = 24.sp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = preset.nameSwahili,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = if (isSelected) preset.primaryColor else DopTextPrimary
                                            )
                                            if (preset.isDark) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("🌙 Dark", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = DopOchre)
                                            }
                                        }
                                        Text(
                                            text = preset.descriptionSwahili,
                                            fontSize = 10.sp,
                                            color = DopTextSecondary,
                                            lineHeight = 14.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(preset.primaryColor))
                                            Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(preset.secondaryColor))
                                            Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(preset.accentColor))
                                            Text(preset.nameEnglish, fontSize = 9.sp, color = DopTextMuted)
                                        }
                                    }

                                    if (isSelected) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = "Selected",
                                            tint = preset.primaryColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Realtime Sync Status & Network Simulation
        item {
            DopBentoCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Muunganisho wa Moja kwa Moja (Live Sync)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(if (isConnected) "Connected: WebSockets/SSE Active" else "Disconnected: Offline Mode Active", fontSize = 11.sp, color = DopTextSecondary)
                    }
                    Switch(
                        checked = isConnected,
                        onCheckedChange = { SyncManager.toggleConnectionState() },
                        modifier = Modifier.testTag("sync_toggle_switch")
                    )
                }
            }
        }

        // Platform & Legal Info
        item {
            DopBentoCard {
                Text("Kuhusu Dalalion Pocket (DoP)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Dalalion Pocket ni mfumo rasmi wa kusimamia soko la nyumba na viwanja nchini Tanzania. Toleo 1.0. Hati Miliki © 2026 Dalalion Pocket Tanzania.",
                    fontSize = 11.sp,
                    color = DopTextSecondary,
                    lineHeight = 16.sp
                )
            }
        }

        // Logout Button
        item {
            OutlinedButton(
                onClick = { AuthManager.logout() },
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, DopError),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DopError),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("logout_button")
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null, tint = DopError)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ondoka Kwenye Akaunti (Log Out)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }

    if (showRoleDialog) {
        val userSession = currentUser ?: AuthManager.mockUsers[UserRole.CUSTOMER]!!
        CapabilityRoleSwitcherDialog(
            currentUser = userSession,
            onDismiss = { showRoleDialog = false },
            onSelectMode = { role ->
                com.example.core.account.AccountManager.switchActiveMode(role)
                onRoleChange(role)
                showRoleDialog = false
            },
            onOpenOwnerOnboarding = {
                showRoleDialog = false
                showOwnerOnboardingDialog = true
            },
            onOpenGuideApplication = {
                showRoleDialog = false
                showGuideApplicationDialog = true
            }
        )
    }

    if (showOwnerOnboardingDialog) {
        OwnerOnboardingDialog(
            onDismiss = { showOwnerOnboardingDialog = false },
            onSuccessActivated = {
                showOwnerOnboardingDialog = false
            }
        )
    }

    if (showGuideApplicationDialog) {
        GuideApplicationDialog(
            onDismiss = { showGuideApplicationDialog = false },
            onSuccessSubmitted = {
                showGuideApplicationDialog = false
            }
        )
    }
}

// ============================================================
// 6. PROPERTY DETAILS SCREEN WITH PHYSICAL BOOKING & APPLY MODALS
// ============================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyDetailsScreen(
    property: Property,
    lang: AppLanguage,
    onBack: () -> Unit,
    onOpen360Tour: () -> Unit,
    onOpenLiveViewing: () -> Unit,
    onAgreementView: (TenancyAgreement) -> Unit,
    onOwnerManage: () -> Unit = onBack,
    onOwnerViewings: () -> Unit = onBack,
    onGuideStartViewing: () -> Unit = onBack
) {
    val currentUserSession by AuthManager.currentUser.collectAsState()
    val activeRole = currentUserSession?.activeMode ?: UserRole.CUSTOMER
    val savedIds by PropertyRepository.savedPropertyIds.collectAsState()
    val isSaved = savedIds.contains(property.id)

    var showBookingModal by remember { mutableStateOf(false) }
    var showApplicationModal by remember { mutableStateOf(false) }
    var showSuccessSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    // Booking modal states
    var selectedViewingType by remember { mutableStateOf(ViewingType.DOP_ASSISTED_VISIT) }
    var bookingDate by remember { mutableStateOf("Kesho (10:00 AM)") }
    var bookingTimeSlot by remember { mutableStateOf("10:00 AM - 11:00 AM") }
    var meetingPoint by remember { mutableStateOf("Shoppers Plaza Masaki Entrance") }
    var bookingNotes by remember { mutableStateOf("") }
    var pendingAssistedBooking by remember { mutableStateOf<ViewingBooking?>(null) }

    // Rental application states
    var applicantMoveInDate by remember { mutableStateOf("01 Oktoba 2026") }
    var applicantDurationMonths by remember { mutableIntStateOf(6) }
    var applicantOccupants by remember { mutableIntStateOf(2) }
    var applicantOccupation by remember { mutableStateOf("Software Engineer") }
    var applicantIncome by remember { mutableStateOf("3500000") }
    var showTitleDeedPayment by remember { mutableStateOf(false) }
    var titleDeedVerified by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            val barColor = when (activeRole) {
                UserRole.OWNER -> DopOwnerGreenDark
                UserRole.GUIDE -> DopGuideAmberDark
                else -> DopNavyPrimary
            }
            val barTitle = when (activeRole) {
                UserRole.OWNER -> "Mali Yangu • ${property.id}"
                UserRole.GUIDE -> "Kazi ya Ziara • ${property.id}"
                else -> property.id
            }
            TopAppBar(
                title = {
                    Text(
                        text = barTitle,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    when (activeRole) {
                        UserRole.OWNER -> {
                            IconButton(onClick = onOwnerManage) {
                                Icon(Icons.Default.Edit, contentDescription = "Manage", tint = Color.White)
                            }
                        }
                        UserRole.GUIDE -> {
                            IconButton(onClick = {
                                snackbarMessage = "Ripoti imetumwa kwa kituo cha dharura na uratibu cha DoP (Safety Desk)."
                                showSuccessSnackbar = true
                            }) {
                                Icon(Icons.Default.ReportProblem, contentDescription = "Report Issue", tint = Color.White)
                            }
                        }
                        else -> {
                            IconButton(onClick = { PropertyRepository.toggleSave(property.id) }) {
                                Icon(
                                    imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Save",
                                    tint = if (isSaved) DopError else Color.White
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = barColor)
            )
        },
        bottomBar = {
            Surface(
                color = DopSurfaceCard,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, DopBorderSubtle)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    when (activeRole) {
                        UserRole.OWNER -> {
                            OutlinedButton(
                                onClick = onOwnerManage,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .testTag("owner_manage_btn")
                            ) {
                                Icon(Icons.Default.Tune, contentDescription = null, tint = DopOwnerGreenDark)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Dhibiti Mali", fontSize = 12.sp, color = DopOwnerGreenDark, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = onOwnerViewings,
                                colors = ButtonDefaults.buttonColors(containerColor = DopOwnerGreenDark),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .testTag("owner_viewings_btn")
                            ) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Maombi ya Ziara", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                        UserRole.GUIDE -> {
                            OutlinedButton(
                                onClick = {
                                    snackbarMessage = "Mwelekeo wa kufika eneo la kukutana (Masaki) umefunguliwa."
                                    showSuccessSnackbar = true
                                },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .testTag("guide_directions_btn")
                            ) {
                                Icon(Icons.Default.Navigation, contentDescription = null, tint = DopGuideAmberDark)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Elekea Eneo", fontSize = 12.sp, color = DopGuideAmberDark, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    snackbarMessage = "Ziara imeanzishwa rasmi. Mteja yupo eneo la tukio."
                                    showSuccessSnackbar = true
                                    onGuideStartViewing()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DopGuideAmberDark),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .testTag("guide_start_viewing_btn")
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Anza Ziara", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                        else -> {
                            OutlinedButton(
                                onClick = { showBookingModal = true },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .testTag("book_viewing_btn")
                            ) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = DopNavyPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(DoPStrings.bookViewing(lang), fontSize = 12.sp, color = DopNavyPrimary, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { showApplicationModal = true },
                                colors = ButtonDefaults.buttonColors(containerColor = DopOchre),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .testTag("apply_rent_btn")
                            ) {
                                Icon(Icons.Default.Handshake, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(DoPStrings.applyToRent(lang), fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(DopNeutralPearl)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Photo Gallery Carousel
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    AsyncImage(
                        model = property.images.firstOrNull() ?: "",
                        contentDescription = property.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                    ) {
                        if (property.isDopExpress) {
                            DopExpressBadge()
                        }
                    }
                }
            }

            // Role-Specific Action Banner (Owner Console / Guide Mission)
            if (activeRole == UserRole.OWNER) {
                item {
                    DopBentoCard(
                        borderColor = DopOwnerGreenDark.copy(alpha = 0.4f),
                        backgroundColor = DopOwnerGreenContainer.copy(alpha = 0.35f)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = DopOwnerGreenDark)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Dhibiti Mali Yako (Owner Console)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DopOwnerGreenDark)
                            }
                            DopBadge(text = "Mwenye Mali", color = DopOwnerGreenDark, backgroundColor = DopOwnerGreenContainer)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Mali hii imeorodheshwa sokoni. Unaweza kubadili bei, kusasisha picha za 360, au kusimamia maombi ya wapangaji.", fontSize = 12.sp, color = DopTextSecondary)

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = onOwnerManage,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = DopOwnerGreenDark, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Hariri Bei / Data", fontSize = 11.sp, color = DopOwnerGreenDark)
                            }
                            Button(
                                onClick = onOwnerViewings,
                                colors = ButtonDefaults.buttonColors(containerColor = DopOwnerGreenDark),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Visibility, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ziara (3 Mpya)", fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }
                }
            } else if (activeRole == UserRole.GUIDE) {
                item {
                    DopBentoCard(
                        borderColor = DopGuideAmberDark.copy(alpha = 0.4f),
                        backgroundColor = DopGuideAmberContainer.copy(alpha = 0.35f)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Badge, contentDescription = null, tint = DopGuideAmberDark)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Maelekezo ya Ziara (Guide Brief)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DopGuideAmberDark)
                            }
                            DopBadge(text = "TSh 2,500", color = DopGuideAmberDark, backgroundColor = DopGuideAmberContainer)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("• Kituo cha Kukutana: Masaki Shoppers Plaza\n• Mteja: Juma Bakari (0712 345 678)\n• Funguo: Getini kwa mlinzi wa zamu\n• Kanuni: Hakikisha mteja anathibitisha QR code kabla ya kuondoka.", fontSize = 12.sp, color = DopTextSecondary, lineHeight = 18.sp)

                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = {
                                snackbarMessage = "Ripoti ya dharura imepokewa na kituo cha usalama cha DoP."
                                showSuccessSnackbar = true
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.ReportProblem, contentDescription = null, tint = DopError, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Ripoti Tatizo la Eneo / Mteja", fontSize = 11.sp, color = DopError)
                        }
                    }
                }
            }

            // Title, Price & Verification Badges
            item {
                DopBentoCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DopPriceDisplay(
                            priceTzs = property.priceTzs,
                            transactionType = property.transactionType,
                            lang = lang
                        )
                        DopBadge(
                            text = property.verificationStatus.name,
                            icon = Icons.Default.Verified,
                            color = DopTrustGreen,
                            backgroundColor = DopTrustGreenContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = property.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = DopNavyPrimary
                    )
                    Text(
                        text = "${property.ward}, ${property.district}, ${property.region}",
                        fontSize = 13.sp,
                        color = DopTextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    // Verification Badges Row
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        property.verificationBadges.forEach { b ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = "✓ $b",
                                    fontSize = 10.sp,
                                    color = DopTextSecondary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Virtual Tour 3-Tier Access Card
            item {
                DopBentoCard(backgroundColor = DopNavyPrimary) {
                    Text(
                        text = "DoP Virtual Viewing (3 Tiers)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Tazama nyumba hii kwa uhalisia mtandaoni:",
                        fontSize = 11.sp,
                        color = DopNeutralPearl.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Tier 2: 360 Tour
                        Button(
                            onClick = onOpen360Tour,
                            colors = ButtonDefaults.buttonColors(containerColor = DopOchre),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).testTag("details_360_btn")
                        ) {
                            Icon(Icons.Default.Explore, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ziara 360°", fontSize = 11.sp)
                        }

                        // Tier 3: Live Viewing
                        Button(
                            onClick = onOpenLiveViewing,
                            colors = ButtonDefaults.buttonColors(containerColor = DopError),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).testTag("details_live_btn")
                        ) {
                            Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Moja kwa Moja", fontSize = 11.sp)
                        }
                    }
                }
            }

            // Specifications & Amenities
            item {
                DopBentoCard {
                    Text("Sifa za Nyumba (Specifications)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SpecItem("Vyumba", "${property.bedrooms}")
                        SpecItem("Bafu", "${property.bathrooms}")
                        SpecItem("Eneo", "${property.areaSqm} m²")
                        SpecItem("Amana", "${property.depositMonths} mwezi")
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Huduma Zilizopo (Amenities)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    property.amenities.forEach { am ->
                        Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = DopTrustGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(am, fontSize = 12.sp, color = DopTextPrimary)
                        }
                    }
                }
            }

            // Description
            item {
                DopBentoCard {
                    Text("Maelezo ya Kina (Description)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = property.description,
                        fontSize = 13.sp,
                        color = DopTextSecondary,
                        lineHeight = 20.sp
                    )
                }
            }

            // Ardhi Registry Title Deed Verification Card (Live Ministry Integration)
            item {
                DopBentoCard(
                    backgroundColor = DopSurfaceCard,
                    borderColor = if (titleDeedVerified) DopTrustGreen else DopBorderSubtle
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = if (titleDeedVerified) DopTrustGreen else DopNavyPrimary, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Uhakiki wa Hati Miliki (Wizara ya Ardhi)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DopNavyPrimary)
                                Text("Sajili Kuu ya Ardhi, Nyumba na Makazi (Ardhi / ILMIS)", fontSize = 10.sp, color = DopTextSecondary)
                            }
                        }
                        if (titleDeedVerified) {
                            DopBadge("HATI IMETHIBITISHWA", Icons.Default.CheckCircle, DopTrustGreen, DopTrustGreenContainer)
                        } else {
                            DopBadge("ADA: TSH 20,000", Icons.Default.Payments, DopOchre, DopOchreContainer)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    val titleDeedNumber = "HAT-TZA-${property.id.takeLast(6)}"
                    Text(
                        text = if (titleDeedVerified) 
                            "Uhakiki umekamilika: Hati No. $titleDeedNumber imethibitishwa rasmi na Wizara ya Ardhi. Mmiliki: ${property.ownerName}. Haina mgogoro wowote wa kisheria wala zuio la kimahakama."
                            else "Huduma ya ukaguzi wa kisheria wa mipaka, hati miliki, na namba ya kiwanja/nyumba kutoka Mfumo Mkuu wa Wizara ya Ardhi (ILMIS).",
                        fontSize = 11.sp,
                        color = DopTextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    if (titleDeedVerified) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DopTrustGreenContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.TaskAlt, contentDescription = null, tint = DopTrustGreen, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Hati Halali: $titleDeedNumber", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DopTrustGreen)
                                    Text("Namba ya Kitalu: Block 14 • Plot 82 MASAKI", fontSize = 10.sp, color = DopTextPrimary)
                                }
                            }
                        }
                    } else {
                        Button(
                            onClick = { showTitleDeedPayment = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DopNavyPrimary),
                            modifier = Modifier.fillMaxWidth().height(44.dp).testTag("title_deed_verify_btn")
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Hakiki Hati Miliki Sasa (TSh 20,000)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            // Location Privacy Card
            item {
                DopBentoCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Eneo na Faragha", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        LocationPrivacyChip(property.locationPrivacy)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = when (property.locationPrivacy) {
                            LocationPrivacy.EXACT -> "Eneo kamili linaonyeshwa kwa uwazi kwenye ramani."
                            LocationPrivacy.APPROXIMATE -> "Eneo hili limewekwa kwa makadirio (~500m) kulinda usalama na faragha ya mwenye nyumba. Utakutana na DoP Guide kituo maalum."
                            LocationPrivacy.HIDDEN -> "Eneo limelindwa kikamilifu hadi utakapolipa au kuthibitisha ziara na DoP Guide."
                        },
                        fontSize = 11.sp,
                        color = DopTextSecondary
                    )
                }
            }

            // Owner & Contact Protocol
            item {
                DopBentoCard {
                    Text("Mwenye Nyumba (Verified Landlord)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(property.ownerName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text("Simu: ${property.ownerPhone} (Namba inalindwa kuzuia upotevu wa faragha)", fontSize = 11.sp, color = DopTextMuted)
                }
            }
        }
    }

    // Modal: Book Physical Viewing (Self Visit vs DoP Assisted Visit)
    if (showBookingModal) {
        AlertDialog(
            onDismissRequest = { showBookingModal = false },
            title = {
                Text(
                    text = "Weka Miadi ya Kuona (Physical Viewing)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Chagua aina ya ziara:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    // Option 1: Self Visit (TSh 0)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedViewingType = ViewingType.SELF_VISIT },
                        shape = RoundedCornerShape(10.dp),
                        color = if (selectedViewingType == ViewingType.SELF_VISIT) DopOchreContainer else DopNeutralPearl,
                        border = BorderStroke(1.dp, if (selectedViewingType == ViewingType.SELF_VISIT) DopOchre else DopBorderSubtle)
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = selectedViewingType == ViewingType.SELF_VISIT, onClick = { selectedViewingType = ViewingType.SELF_VISIT })
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text("Kutembelea Mwenyewe (Self Visit)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Ada: TSh 0 (Bure)", fontSize = 11.sp, color = DopTrustGreen, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    // Option 2: DoP Assisted Visit (TSh 5,000)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedViewingType = ViewingType.DOP_ASSISTED_VISIT },
                        shape = RoundedCornerShape(10.dp),
                        color = if (selectedViewingType == ViewingType.DOP_ASSISTED_VISIT) DopTrustGreenContainer else DopNeutralPearl,
                        border = BorderStroke(1.dp, if (selectedViewingType == ViewingType.DOP_ASSISTED_VISIT) DopTrustGreen else DopBorderSubtle)
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = selectedViewingType == ViewingType.DOP_ASSISTED_VISIT, onClick = { selectedViewingType = ViewingType.DOP_ASSISTED_VISIT })
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text("Ziara na DoP Guide (Assisted Visit)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Ada: TSh 5,000 (TSh 2,500 Guide / TSh 2,500 DoP)", fontSize = 11.sp, color = DopTrustGreen, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = bookingDate,
                        onValueChange = { bookingDate = it },
                        label = { Text("Tarehe & Saa") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = meetingPoint,
                        onValueChange = { meetingPoint = it },
                        label = { Text("Sehemu ya Kukutana (Meeting Point)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = bookingNotes,
                        onValueChange = { bookingNotes = it },
                        label = { Text("Maelezo ya ziada (Hiari)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val booking = PropertyRepository.bookViewing(
                            property = property,
                            customerId = "USR-TZA-0081",
                            customerName = "Juma Bakari",
                            type = selectedViewingType,
                            date = bookingDate,
                            timeSlot = bookingTimeSlot,
                            meetingPoint = meetingPoint,
                            notes = bookingNotes
                        )
                        showBookingModal = false
                        if (selectedViewingType == ViewingType.DOP_ASSISTED_VISIT) {
                            pendingAssistedBooking = booking
                        } else {
                            snackbarMessage = "Miadi yako ya bure (${booking.bookingCode}) imethibitishwa!"
                            showSuccessSnackbar = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DopTrustGreen),
                    modifier = Modifier.testTag("confirm_booking_btn")
                ) {
                    Text("Thibitisha Miadi")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBookingModal = false }) {
                    Text("Ghairi")
                }
            }
        )
    }

    // Assisted Visit Escrow Payment Sheet
    pendingAssistedBooking?.let { booking ->
        PaymentCheckoutSheet(
            title = "Malipo ya DoP Guide (Assisted Visit)",
            subtitle = "Miadi: ${booking.bookingCode} • ${booking.propertyTitle}",
            amountTzs = 5000L,
            source = RevenueSource.VIEWING_FEE,
            onDismiss = {
                pendingAssistedBooking = null
                snackbarMessage = "Miadi ya ${booking.bookingCode} imehifadhiwa. Malipo ya Guide yatalipwa kabla ya kuanza ziara."
                showSuccessSnackbar = true
            },
            onPaymentSuccess = { tx ->
                pendingAssistedBooking = null
                snackbarMessage = "Hongera! Malipo ya Guide (TSh 5,000) yamethibitishwa. Guide wako atawasiliana nawe kufika kituoni. Muamala: ${tx.referenceNo}"
                showSuccessSnackbar = true
            }
        )
    }

    // Modal: Apply to Rent
    if (showApplicationModal) {
        AlertDialog(
            onDismissRequest = { showApplicationModal = false },
            title = {
                Text(
                    text = "Omba Kupanga (Rental Application)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Tuma maelezo yako rasmi kwa mwenye nyumba:",
                        fontSize = 12.sp,
                        color = DopTextSecondary
                    )

                    OutlinedTextField(
                        value = applicantMoveInDate,
                        onValueChange = { applicantMoveInDate = it },
                        label = { Text("Tarehe ya Kuingia") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = applicantDurationMonths.toString(),
                        onValueChange = { applicantDurationMonths = it.toIntOrNull() ?: 6 },
                        label = { Text("Muda wa Kupanga (Miezi)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = applicantOccupation,
                        onValueChange = { applicantOccupation = it },
                        label = { Text("Kazi / Taaluma Yako") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = applicantIncome,
                        onValueChange = { applicantIncome = it },
                        label = { Text("Kipato cha Mwezi (TZS)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val app = PropertyRepository.submitRentalApplication(
                            property = property,
                            applicantName = "Juma Bakari",
                            applicantPhone = "+255 754 123 456",
                            moveInDate = applicantMoveInDate,
                            durationMonths = applicantDurationMonths,
                            occupants = applicantOccupants,
                            occupation = applicantOccupation,
                            incomeTzs = applicantIncome.toLongOrNull() ?: 3500000L,
                            notes = "Nipo tayari kufuata taratibu zote za DoP."
                        )
                        showApplicationModal = false
                        snackbarMessage = "Ombi lako limetumwa kwa mwenye nyumba!"
                        showSuccessSnackbar = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DopOchre),
                    modifier = Modifier.testTag("confirm_apply_btn")
                ) {
                    Text("Wasilisha Ombi")
                }
            },
            dismissButton = {
                TextButton(onClick = { showApplicationModal = false }) {
                    Text("Funga")
                }
            }
        )
    }

    if (showSuccessSnackbar) {
        AlertDialog(
            onDismissRequest = { showSuccessSnackbar = false },
            title = { Text("Imefanikiwa! / Success") },
            text = { Text(snackbarMessage) },
            confirmButton = {
                Button(onClick = { showSuccessSnackbar = false }) {
                    Text("Sawa")
                }
            }
        )
    }

    // Ardhi Registry Title Deed Verification Payment Sheet
    if (showTitleDeedPayment) {
        val titleDeedNumber = "HAT-TZA-${property.id.takeLast(6)}"
        PaymentCheckoutSheet(
            title = "Uhakiki wa Hati Miliki (Wizara ya Ardhi)",
            subtitle = "Mali: ${property.title} • Hati: $titleDeedNumber",
            amountTzs = 20000L,
            source = RevenueSource.TITLE_DEED_SEARCH,
            onDismiss = { showTitleDeedPayment = false },
            onPaymentSuccess = { tx ->
                showTitleDeedPayment = false
                titleDeedVerified = true
                snackbarMessage = "Uhakiki umefaulu! Hati No. $titleDeedNumber imethibitishwa kutoka ILMIS. Muamala: ${tx.referenceNo}"
                showSuccessSnackbar = true
            }
        )
    }
}

@Composable
private fun SpecItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DopNavyPrimary)
        Text(text = label, fontSize = 11.sp, color = DopTextSecondary)
    }
}
