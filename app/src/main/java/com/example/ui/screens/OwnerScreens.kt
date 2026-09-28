package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.core.auth.AuthManager
import com.example.core.localization.AppLanguage
import com.example.core.localization.DoPStrings
import com.example.data.model.*
import com.example.data.repository.PropertyRepository
import com.example.data.repository.PropertyShortsRepository
import com.example.ui.components.DopBadge
import com.example.ui.components.DopBentoCard
import com.example.ui.components.PaymentCheckoutSheet
import com.example.ui.theme.*

enum class OwnerPropertyTab(val titleSw: String) {
    ALL("Zote"),
    DRAFTS("Rasimu"),
    UNDER_REVIEW("Ukaguzi (Review)"),
    PUBLISHED("Zilizochapishwa"),
    PAUSED("Zilizosimamishwa"),
    NEEDS_REVISION("Marekebisho"),
    ARCHIVED("Kumbukumbu")
}

@Composable
fun OwnerDashboardScreen(
    lang: AppLanguage,
    onOpenWizard: () -> Unit,
    onOpenProperties: () -> Unit = {},
    onOpenShorts: () -> Unit = {},
    onOpenViewings: () -> Unit = {},
    onViewAgreement: (TenancyAgreement) -> Unit
) {
    val properties by PropertyRepository.properties.collectAsState()
    val applications by PropertyRepository.rentalApplications.collectAsState()
    val agreements by PropertyRepository.tenancyAgreements.collectAsState()
    val ownerDraft by PropertyRepository.ownerDraft.collectAsState()
    val shorts by PropertyShortsRepository.shorts.collectAsState()
    val currentUser by AuthManager.currentUser.collectAsState()
    val context = LocalContext.current

    var boostingProperty by remember { mutableStateOf<Property?>(null) }
    var showPostShortDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DopNeutralPearl)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Owner Header Card
        item {
            DopBentoCard(backgroundColor = DopNavyPrimary) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Dashibodi ya Mwenye Nyumba",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${currentUser?.name ?: "Mwenye Nyumba"} • Aliyethibitishwa",
                            fontSize = 12.sp,
                            color = DopOchreLight
                        )
                    }
                    DopBadge("VERIFIED OWNER", Icons.Default.Verified, DopTrustGreenLight, DopNavyElevated)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OwnerStatItem("Mali Zako", "${properties.size}", DopOchreLight)
                    OwnerStatItem("Maombi Mapya", "${applications.size}", DopTrustGreenLight)
                    OwnerStatItem("Mikataba", "${agreements.size}", Color.White)
                }
            }
        }

        // Active Draft Resume Banner (if owner has an in-progress draft)
        if (ownerDraft != null) {
            item {
                ownerDraft?.let { draft ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("owner_draft_resume_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DopOchreContainer),
                        border = BorderStroke(1.5.dp, DopOchre)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.EditNote, contentDescription = null, tint = DopOchre, modifier = Modifier.size(22.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Rasimu ya Mali Inayoendelea",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = DopNavyPrimary
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = DopOchre
                                ) {
                                    Text(
                                        text = "${draft.completenessScore}%",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = DopNavyPrimary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = draft.title.ifBlank { "Mali Isiyo na Kichwa (Hatua ${draft.currentStep}/13)" },
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = DopNavyPrimary
                            )
                            Text(
                                text = "Umehifadhiwa kiotomatiki • Hatua ya sasa: ${draft.currentStep} kati ya 13",
                                fontSize = 11.sp,
                                color = DopTextSecondary
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = onOpenWizard,
                                    colors = ButtonDefaults.buttonColors(containerColor = DopNavyPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("resume_draft_btn")
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Endelea na Rasimu", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = { PropertyRepository.discardDraft() },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DopError),
                                    border = BorderStroke(1.dp, DopError.copy(alpha = 0.5f))
                                ) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Futa", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Owner Quick Action Hub
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Action 1: Add Property Studio
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onOpenWizard() }
                            .testTag("owner_quick_add_prop"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = DopOchre),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(DopNavyPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AddHomeWork, contentDescription = null, tint = DopOchre, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "+ Nyumba Mpya",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DopNavyPrimary
                            )
                            Text(
                                text = "Studio ya Hatua 13",
                                fontSize = 10.sp,
                                color = DopNavyPrimary.copy(alpha = 0.8f)
                            )
                        }
                    }

                    // Action 2: Manage Shorts Reels
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onOpenProperties() }
                            .testTag("owner_quick_shorts_studio"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = DopNavyElevated),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(DopOchre),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Shorts Zangu",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                DopBadge("${shorts.size}", color = DopOchre, backgroundColor = DopOchreContainer)
                            }
                            Text(
                                text = "Dhibiti Video Reels",
                                fontSize = 10.sp,
                                color = DopNeutralPearl.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Action 3: Portfolio
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onOpenProperties() }
                            .testTag("owner_quick_portfolio"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(DopNavyPrimary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.HomeWork, contentDescription = null, tint = DopNavyPrimary, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Mali Zangu", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DopNavyPrimary)
                                Text("${properties.size} Nyumba", fontSize = 10.sp, color = DopTextSecondary)
                            }
                        }
                    }

                    // Action 4: Viewings
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onOpenViewings() }
                            .testTag("owner_quick_viewings"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(DopTrustGreenContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.EventAvailable, contentDescription = null, tint = DopTrustGreen, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Ziara & Miadi", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DopNavyPrimary)
                                Text("Ukaguzi mtaani", fontSize = 10.sp, color = DopTextSecondary)
                            }
                        }
                    }
                }
            }
        }

        // Shorts Reels Spotlight & Upload Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("owner_shorts_spotlight_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DopNavyPrimary),
                border = BorderStroke(1.dp, DopOchre.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
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
                                    .background(DopOchre),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.VideoCall, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Shorts Video Feed (Reels)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "Wapangaji wanatazama video fupi kila siku",
                                    fontSize = 11.sp,
                                    color = DopOchreLight
                                )
                            }
                        }
                        DopBadge("LIVE", Icons.Default.FiberManualRecord, DopTrustGreenLight, DopNavyElevated)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val totalShortViews = shorts.sumOf { it.viewsCount }
                    val totalShortLikes = shorts.sumOf { it.likesCount }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = DopNavyElevated,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Reels Hewani", fontSize = 10.sp, color = DopNeutralPearl.copy(alpha = 0.7f))
                                Text("${shorts.size}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Watazamaji", fontSize = 10.sp, color = DopNeutralPearl.copy(alpha = 0.7f))
                                Text(String.format("%,d", totalShortViews), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DopOchreLight)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Vipendwa", fontSize = 10.sp, color = DopNeutralPearl.copy(alpha = 0.7f))
                                Text(String.format("%,d", totalShortLikes), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DopTrustGreenLight)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showPostShortDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = DopOchre),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.2f).testTag("owner_upload_short_spotlight_btn")
                        ) {
                            Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(15.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+ Pandisha Short Mpya", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onOpenShorts,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("owner_watch_shorts_feed_btn")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Tazama Feed", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Rental Applications Section (Review, Approve, Decline)
        item {
            Text(
                text = "Maombi ya Wapangaji (Rental Applications)",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = DopNavyPrimary
            )
        }

        items(applications) { app ->
            DopBentoCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = app.applicantName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = app.occupation, fontSize = 11.sp, color = DopTextSecondary)
                    }
                    DopBadge(
                        text = app.status.name,
                        icon = if (app.status == ApplicationStatus.APPROVED) Icons.Default.Check else Icons.Default.HourglassEmpty,
                        color = if (app.status == ApplicationStatus.APPROVED) DopTrustGreen else DopOchre,
                        backgroundColor = if (app.status == ApplicationStatus.APPROVED) DopTrustGreenContainer else DopOchreContainer
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Nyumba: ${app.propertyTitle}", fontSize = 12.sp, color = DopNavyPrimary, fontWeight = FontWeight.Medium)
                Text(text = "Muda: Miezi ${app.rentalDurationMonths} • Kuingia: ${app.moveInDate}", fontSize = 11.sp, color = DopTextSecondary)
                Text(text = "Kipato: TSh ${String.format("%,d", app.monthlyIncomeTzs)} / mwezi", fontSize = 11.sp, color = DopTrustGreen, fontWeight = FontWeight.SemiBold)
                if (app.applicantNotes.isNotBlank()) {
                    Text(text = "\"${app.applicantNotes}\"", fontSize = 11.sp, color = DopTextMuted, modifier = Modifier.padding(top = 4.dp))
                }

                if (app.status == ApplicationStatus.SUBMITTED) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { PropertyRepository.reviewApplication(app.id, true) },
                            colors = ButtonDefaults.buttonColors(containerColor = DopTrustGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("approve_app_${app.id}")
                        ) {
                            Text("Idhinisha (Approve)", fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = { PropertyRepository.reviewApplication(app.id, false) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("decline_app_${app.id}")
                        ) {
                            Text("Kataa (Decline)", fontSize = 11.sp, color = DopError)
                        }
                    }
                }
            }
        }

        // Active Tenancy Agreements
        item {
            Text(
                text = "Mikataba ya Upangaji (Tenancy Agreements)",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = DopNavyPrimary
            )
        }

        items(agreements) { agr ->
            DopBentoCard(onClick = { onViewAgreement(agr) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = agr.agreementCode, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DopNavyPrimary)
                        Text(text = "Mpangaji: ${agr.tenantName}", fontSize = 12.sp, color = DopTextSecondary)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = DopTextSecondary)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Kodi: ${DoPStrings.tzs(agr.monthlyRentTzs)}/mwezi • Jumla: ${DoPStrings.tzs(agr.totalInitialPayableTzs)}",
                    fontSize = 11.sp,
                    color = DopTrustGreen,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                DopBadge(
                    text = agr.status.name,
                    color = if (agr.status == TenancyAgreementStatus.LOCKED) DopTrustGreen else DopOchre,
                    backgroundColor = if (agr.status == TenancyAgreementStatus.LOCKED) DopTrustGreenContainer else DopOchreContainer
                )
            }
        }

        // Portfolio Overview Hub Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("owner_portfolio_hub_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Usimamizi wa Nyumba & Shorts", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DopNavyPrimary)
                            Text("Dhibiti orodha ya nyumba zako, sitisha au ongeza shorts", fontSize = 11.sp, color = DopTextSecondary)
                        }
                        DopBadge("${properties.size} Mali", Icons.Default.HomeWork, DopNavyPrimary, DopNavyPrimary.copy(alpha = 0.1f))
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val publishedCount = properties.count { it.listingStatus == ListingStatus.PUBLISHED }
                    val underReviewCount = properties.count { it.listingStatus == ListingStatus.SUBMITTED }
                    val pausedCount = properties.count { it.listingStatus == ListingStatus.PAUSED }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = DopNeutralPearl,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Hewani", fontSize = 10.sp, color = DopTextSecondary)
                                Text("$publishedCount", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DopTrustGreen)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Inakaguliwa", fontSize = 10.sp, color = DopTextSecondary)
                                Text("$underReviewCount", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DopOchre)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Zimesitishwa", fontSize = 10.sp, color = DopTextSecondary)
                                Text("$pausedCount", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DopTextMuted)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onOpenProperties,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DopNavyPrimary),
                        modifier = Modifier.fillMaxWidth().testTag("owner_manage_portfolio_btn")
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Fungua Sehemu ya Mali & Shorts Zangu", fontSize = 12.sp)
                    }
                }
            }
        }
    }

    // Listing Boost Payment Sheet
    boostingProperty?.let { prop ->
        PaymentCheckoutSheet(
            title = "Kupandisha Hadhi Nyumba (Listing Boost)",
            subtitle = "Mali: ${prop.title} (${prop.id})",
            amountTzs = 15000L,
            source = RevenueSource.LISTING_BOOST,
            onDismiss = { boostingProperty = null },
            onPaymentSuccess = { tx ->
                PropertyRepository.boostListing(
                    propertyId = prop.id,
                    tier = ListingBoostTier.SILVER,
                    payerName = currentUser?.name ?: "Mwenye Nyumba",
                    payerPhone = tx.payerPhone,
                    method = tx.method
                )
                boostingProperty = null
                Toast.makeText(
                    context,
                    "Hongera! Mali yako sasa ipo daraja la juu (SILVER BOOST). Muamala: ${tx.referenceNo}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    if (showPostShortDialog) {
        PostPropertyShortDialog(
            properties = properties,
            ownerName = currentUser?.name ?: "Mwenye Nyumba",
            ownerPhone = currentUser?.phone ?: "+255 784 567 890",
            onDismiss = { showPostShortDialog = false },
            onPost = { newShort ->
                PropertyShortsRepository.postShort(newShort)
                showPostShortDialog = false
                Toast.makeText(
                    context,
                    "Short Video ya '${newShort.propertyTitle}' imechapishwa hewani! Wateja wataiona sasa.",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }
}

@Composable
private fun MetricBadgeItem(icon: androidx.compose.ui.graphics.vector.ImageVector, count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = DopNavyPrimary, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text(count, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DopNavyPrimary)
        }
        Text(label, fontSize = 9.sp, color = DopTextSecondary)
    }
}

@Composable
private fun OwnerStatItem(title: String, value: String, valueColor: Color) {
    Column {
        Text(text = title, fontSize = 11.sp, color = DopNeutralPearl.copy(alpha = 0.7f))
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

// ============================================================
// OWNER PROPERTY CREATION STUDIO WIZARD DELEGATE
// ============================================================
@Composable
fun OwnerPropertyWizardScreen(
    onDismiss: () -> Unit,
    onFinished: () -> Unit
) {
    OwnerPropertyStudioScreen(
        onDismiss = onDismiss,
        onFinished = { onFinished() }
    )
}

// ============================================================
// OWNER SHORT VIDEO CREATOR MODAL / DIALOG
// ============================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostPropertyShortDialog(
    properties: List<Property>,
    ownerName: String,
    ownerPhone: String,
    preSelectedPropertyId: String? = null,
    onDismiss: () -> Unit,
    onPost: (PropertyShort) -> Unit
) {
    val initialIdx = remember(properties, preSelectedPropertyId) {
        if (preSelectedPropertyId != null) {
            val idx = properties.indexOfFirst { it.id == preSelectedPropertyId }
            if (idx >= 0) idx else 0
        } else 0
    }
    var selectedPropertyIndex by remember { mutableIntStateOf(initialIdx) }
    val chosenProperty = properties.getOrNull(selectedPropertyIndex) ?: properties.firstOrNull()

    var shortTitle by remember {
        mutableStateOf(
            if (chosenProperty != null) "Ziara ya Ndani - ${chosenProperty.title}" else "Ziara ya Ndani ya Nyumba"
        )
    }
    var description by remember {
        mutableStateOf("Muonekano halisi wa sebule, vyumba na mazingira ya nje. Karibu ukague na upange leo!")
    }
    var customTags by remember { mutableStateOf("#DarEsSalaam #NyumbaZaKupanga #DoPShorts") }

    val scenePresets = listOf(
        Pair(
            "Sebule & Balcony Tour",
            "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?auto=format&fit=crop&w=800&q=80"
        ),
        Pair(
            "Master Bedroom & Bafu",
            "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=800&q=80"
        ),
        Pair(
            "Jiko la Kisasa & Makabati",
            "https://images.unsplash.com/photo-1556911220-e15b29be8c8f?auto=format&fit=crop&w=800&q=80"
        ),
        Pair(
            "Bustani, Fensi & Parking",
            "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=800&q=80"
        )
    )
    var selectedSceneIndex by remember { mutableIntStateOf(0) }

    val availableHighlights = listOf("Sebule Kubwa", "Vyumba Master", "Tiles Mpya", "Maji DAWASA", "AC Kila Chumba", "Ulinzi na Fensi")
    val selectedHighlights = remember { mutableStateListOf("Sebule Kubwa", "Maji DAWASA", "Tiles Mpya") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(DopOchre),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.VideoCall, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Pandisha Short Video (Reel)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Wapangaji wataitazama kwenye feed ya Shorts", fontSize = 11.sp, color = DopTextSecondary)
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. Select Property
                item {
                    Text("1. Chagua Nyumba Yako:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DopNavyPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        properties.take(4).forEachIndexed { idx, prop ->
                            val isSelected = selectedPropertyIndex == idx
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) DopOchreContainer else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (isSelected) BorderStroke(1.5.dp, DopOchre) else null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedPropertyIndex = idx
                                        shortTitle = "Ziara ya Ndani - ${prop.title}"
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = {
                                            selectedPropertyIndex = idx
                                            shortTitle = "Ziara ya Ndani - ${prop.title}"
                                        },
                                        colors = RadioButtonDefaults.colors(selectedColor = DopOchre)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(prop.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                        Text("${prop.ward}, ${prop.district} • TSh ${String.format("%,d", prop.priceTzs)}/mwezi", fontSize = 11.sp, color = DopTextSecondary)
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. Video Title
                item {
                    Text("2. Kichwa cha Short Video (Title):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DopNavyPrimary)
                    OutlinedTextField(
                        value = shortTitle,
                        onValueChange = { shortTitle = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("post_short_title_input"),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // 3. Preset Walkthrough Scene
                item {
                    Text("3. Chagua Sehemu Iliyorekodiwa (Scene Preset):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DopNavyPrimary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        scenePresets.forEachIndexed { sIdx, (sceneName, _) ->
                            val isChosen = selectedSceneIndex == sIdx
                            FilterChip(
                                selected = isChosen,
                                onClick = { selectedSceneIndex = sIdx },
                                label = { Text(sceneName, fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = DopOchre,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                // 4. Description
                item {
                    Text("4. Maelezo Mafupi (Description):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DopNavyPrimary)
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("post_short_desc_input"),
                        shape = RoundedCornerShape(10.dp),
                        maxLines = 3
                    )
                }

                // 5. Room Highlights
                item {
                    Text("5. Vipengele vya Chumba (Highlights):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DopNavyPrimary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        availableHighlights.forEach { highlight ->
                            val isChecked = selectedHighlights.contains(highlight)
                            FilterChip(
                                selected = isChecked,
                                onClick = {
                                    if (isChecked) selectedHighlights.remove(highlight) else selectedHighlights.add(highlight)
                                },
                                label = { Text(highlight, fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = DopTrustGreen,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                // 6. Tags
                item {
                    Text("6. Hashtags (#):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DopNavyPrimary)
                    OutlinedTextField(
                        value = customTags,
                        onValueChange = { customTags = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val property = chosenProperty ?: properties.firstOrNull()
                    val propertyId = property?.id ?: "PROP-GEN-001"
                    val propertyTitle = property?.title ?: "Nyumba ya Kisasa"
                    val location = if (property != null) "${property.ward}, ${property.district}" else "Dar es Salaam"
                    val priceDisplay = if (property != null) "TZS ${String.format("%,d", property.priceTzs)} / mwezi" else "TZS 1,000,000 / mwezi"
                    val chosenThumb = scenePresets[selectedSceneIndex].second

                    val tagsList = customTags.split(" ").filter { it.isNotBlank() }

                    val newShort = PropertyShort(
                        propertyId = propertyId,
                        propertyTitle = propertyTitle,
                        ownerName = ownerName,
                        ownerPhone = ownerPhone,
                        ownerAvatarUrl = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=200&q=80",
                        location = location,
                        priceDisplay = priceDisplay,
                        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                        thumbnailUrl = chosenThumb,
                        description = description.ifBlank { "Tazama video fupi ya nyumba hii!" },
                        roomHighlights = selectedHighlights.toList(),
                        tags = if (tagsList.isNotEmpty()) tagsList else listOf("#Nyumba", "#DoPShorts"),
                        likesCount = 1,
                        isLiked = false,
                        viewsCount = 1,
                        commentsCount = 0,
                        createdAt = "Hivi punde"
                    )

                    onPost(newShort)
                },
                colors = ButtonDefaults.buttonColors(containerColor = DopOchre),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("post_short_submit_btn")
            ) {
                Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Chapisha Short Video (Publish)")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Ghairi")
            }
        }
    )
}


// ============================================================
// OWNER SHORT COMMENTS & INQUIRIES DIALOG
// ============================================================
@Composable
fun OwnerShortCommentsDialog(
    short: PropertyShort,
    onDismiss: () -> Unit,
    onSendReply: (String) -> Unit
) {
    var replyText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(DopOchreContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = DopOchre, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Maswali ya Wapangaji (Inquiries)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DopNavyPrimary)
                    Text(short.propertyTitle, fontSize = 11.sp, color = DopTextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 380.dp)
            ) {
                if (short.comments.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.QuestionAnswer, contentDescription = null, tint = DopTextMuted, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Bado hakuna maswali kwenye video hii.", fontSize = 12.sp, color = DopTextSecondary)
                            Text("Maswali ya wateja yatatokea hapa.", fontSize = 11.sp, color = DopTextMuted)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(short.comments) { comment ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (comment.userRole.contains("Mwenye") || comment.userRole.contains("Owner")) DopOchreContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(comment.userName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DopNavyPrimary)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = if (comment.userRole.contains("Mwenye")) DopOchre else DopNavyPrimary.copy(alpha = 0.1f)
                                            ) {
                                                Text(
                                                    comment.userRole,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (comment.userRole.contains("Mwenye")) Color.White else DopNavyPrimary,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                        Text(comment.timeAgo, fontSize = 10.sp, color = DopTextMuted)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(comment.comment, fontSize = 12.sp, color = DopNavyPrimary)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Reply input box
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = replyText,
                        onValueChange = { replyText = it },
                        placeholder = { Text("Jibu kama Mmiliki...", fontSize = 12.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("owner_reply_input"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = {
                            if (replyText.isNotBlank()) {
                                onSendReply(replyText.trim())
                                replyText = ""
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = DopNavyPrimary, contentColor = Color.White),
                        modifier = Modifier.testTag("owner_send_reply_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", modifier = Modifier.size(16.dp))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Funga (Close)")
            }
        }
    )
}

// ============================================================
// DELETE SHORT CONFIRMATION DIALOG
// ============================================================
@Composable
fun DeleteShortConfirmDialog(
    short: PropertyShort,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = DopError, modifier = Modifier.size(32.dp))
        },
        title = {
            Text("Futa Video ya Short?", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        },
        text = {
            Text(
                "Je, una uhakika unataka kufuta video fupi ya '${short.propertyTitle}'? Video hii haitaonekana tena kwenye feed ya wateja.",
                fontSize = 13.sp,
                color = DopTextSecondary
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirmDelete,
                colors = ButtonDefaults.buttonColors(containerColor = DopError),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("confirm_delete_short_btn")
            ) {
                Text("Ndio, Futa (Delete)")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Ghairi")
            }
        }
    )
}

// ============================================================
// OWNER PROPERTIES & SHORTS REELS MANAGEMENT HUB
// ============================================================
@Composable
fun OwnerPropertiesScreen(
    lang: AppLanguage,
    onViewProperty: (Property) -> Unit,
    onAddProperty: () -> Unit,
    onOpenShortsFeed: () -> Unit = {}
) {
    val properties by PropertyRepository.properties.collectAsState()
    val shorts by PropertyShortsRepository.shorts.collectAsState()
    val currentUser by AuthManager.currentUser.collectAsState()
    val context = LocalContext.current

    var activeTab by remember { mutableIntStateOf(0) } // 0 = Mali Zangu (Portfolio), 1 = Shorts Zangu (Manage Video Reels)
    var selectedFilter by remember { mutableStateOf(OwnerPropertyTab.ALL) }
    var boostingProperty by remember { mutableStateOf<Property?>(null) }
    var postShortProperty by remember { mutableStateOf<Property?>(null) }
    var showPostShortDialog by remember { mutableStateOf(false) }
    var shortForComments by remember { mutableStateOf<PropertyShort?>(null) }
    var shortToDelete by remember { mutableStateOf<PropertyShort?>(null) }

    val myProperties = remember(properties, currentUser) {
        val filtered = properties.filter { prop ->
            prop.ownerId == currentUser?.id || prop.ownerId == "OWN-882" || prop.ownerId == "OWN-TZA-0882"
        }
        if (filtered.isEmpty()) properties else filtered
    }

    val myShorts = remember(shorts, myProperties, currentUser) {
        val propIds = myProperties.map { it.id }.toSet()
        val ownerName = currentUser?.name ?: "Eng. Grace Ndesamburo"
        shorts.filter { short ->
            short.propertyId in propIds || short.ownerName.contains(ownerName, ignoreCase = true) || short.ownerName.contains("Grace", ignoreCase = true)
        }.ifEmpty { shorts }
    }

    val filteredProperties = remember(myProperties, selectedFilter) {
        when (selectedFilter) {
            OwnerPropertyTab.ALL -> myProperties
            OwnerPropertyTab.DRAFTS -> myProperties
            OwnerPropertyTab.UNDER_REVIEW -> myProperties.filter { it.listingStatus == ListingStatus.SUBMITTED }
            OwnerPropertyTab.PUBLISHED -> myProperties.filter { it.listingStatus == ListingStatus.PUBLISHED }
            OwnerPropertyTab.PAUSED -> myProperties.filter { it.listingStatus == ListingStatus.PAUSED }
            OwnerPropertyTab.NEEDS_REVISION -> myProperties.filter { it.verificationStatus == VerificationStatus.REJECTED }
            OwnerPropertyTab.ARCHIVED -> myProperties.filter { it.listingStatus == ListingStatus.ARCHIVED }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DopNeutralPearl)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Sehemu ya Mmiliki (Owner Studio)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DopNavyPrimary
                    )
                    Text(
                        text = "Dhibiti nyumba zako na video fupi za matangazo",
                        fontSize = 11.sp,
                        color = DopTextSecondary
                    )
                }
                if (activeTab == 0) {
                    Button(
                        onClick = onAddProperty,
                        colors = ButtonDefaults.buttonColors(containerColor = DopNavyPrimary),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("owner_add_prop_top_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ongeza Nyumba", fontSize = 11.sp)
                    }
                } else {
                    Button(
                        onClick = { showPostShortDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = DopOchre),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("owner_add_short_top_btn")
                    ) {
                        Icon(Icons.Default.VideoCall, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Pandisha Short", fontSize = 11.sp, color = Color.White)
                    }
                }
            }
        }

        // Segmented Tab Switcher (Mali Zangu vs Shorts Zangu)
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = DopNavyPrimary.copy(alpha = 0.08f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val tabTitles = listOf(
                        Pair("Mali Zangu (${myProperties.size})", Icons.Default.HomeWork),
                        Pair("Shorts Reels (${myShorts.size})", Icons.Default.VideoLibrary)
                    )
                    tabTitles.forEachIndexed { idx, (title, icon) ->
                        val isSelected = activeTab == idx
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) DopNavyPrimary else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { activeTab = idx }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (isSelected) Color.White else DopNavyPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else DopNavyPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // TAB 1: MALI ZANGU (PROPERTIES PORTFOLIO)
        if (activeTab == 0) {
            // Filter Pills
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    val tabs = listOf(
                        OwnerPropertyTab.ALL,
                        OwnerPropertyTab.PUBLISHED,
                        OwnerPropertyTab.UNDER_REVIEW,
                        OwnerPropertyTab.PAUSED
                    )
                    tabs.forEach { tab ->
                        val count = when (tab) {
                            OwnerPropertyTab.ALL -> myProperties.size
                            OwnerPropertyTab.PUBLISHED -> myProperties.count { it.listingStatus == ListingStatus.PUBLISHED }
                            OwnerPropertyTab.UNDER_REVIEW -> myProperties.count { it.listingStatus == ListingStatus.SUBMITTED }
                            OwnerPropertyTab.PAUSED -> myProperties.count { it.listingStatus == ListingStatus.PAUSED }
                            else -> 0
                        }
                        val isSelected = selectedFilter == tab
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilter = tab },
                            label = { Text("${tab.titleSw} ($count)", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DopNavyPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            if (filteredProperties.isEmpty()) {
                item {
                    DopBentoCard {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 30.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.HomeWork, contentDescription = null, tint = DopTextSecondary, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Hakuna mali zilizopatikana katika kundi hili.", fontSize = 13.sp, color = DopNavyPrimary, fontWeight = FontWeight.Bold)
                            Text("Bofya 'Ongeza Nyumba' kuanza kupangisha.", fontSize = 11.sp, color = DopTextSecondary)
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(onClick = onAddProperty, colors = ButtonDefaults.buttonColors(containerColor = DopNavyPrimary)) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Ongeza Nyumba ya Kwanza")
                            }
                        }
                    }
                }
            } else {
                items(filteredProperties) { prop ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("owner_property_card_${prop.id}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Image & Badges
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            ) {
                                AsyncImage(
                                    model = prop.images.firstOrNull() ?: "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?auto=format&fit=crop&w=800&q=80",
                                    contentDescription = prop.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f))
                                            )
                                        )
                                )
                                // Top Badges
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = when (prop.listingStatus) {
                                            ListingStatus.PUBLISHED -> DopTrustGreen
                                            ListingStatus.PAUSED -> DopTextSecondary
                                            ListingStatus.SUBMITTED -> DopOchre
                                            else -> DopNavyPrimary
                                        }
                                    ) {
                                        Text(
                                            text = when (prop.listingStatus) {
                                                ListingStatus.PUBLISHED -> "HEWANI (PUBLISHED)"
                                                ListingStatus.PAUSED -> "IMESITISHWA (PAUSED)"
                                                ListingStatus.SUBMITTED -> "INAKAGULIWA"
                                                else -> prop.listingStatus.name
                                            },
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }

                                    if (prop.verificationStatus == VerificationStatus.VERIFIED) {
                                        DopBadge("VERIFIED", Icons.Default.Verified, DopTrustGreen, Color.White)
                                    }
                                }

                                // Bottom Row inside Image
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color.Black.copy(alpha = 0.7f)
                                    ) {
                                        Text(
                                            text = prop.propertyType.name,
                                            fontSize = 10.sp,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    val hasLinkedShort = shorts.any { it.propertyId == prop.id }
                                    if (hasLinkedShort) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = DopOchre
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.White, modifier = Modifier.size(11.dp))
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text("Short Reel Active", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Title and Price
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = prop.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = DopNavyPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = DopOchre, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "${prop.ward}, ${prop.district}, Dar es Salaam",
                                            fontSize = 11.sp,
                                            color = DopTextSecondary
                                        )
                                    }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = DoPStrings.tzs(prop.priceTzs),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = DopNavyPrimary
                                    )
                                    Text(text = "/ mwezi", fontSize = 10.sp, color = DopTextMuted)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Features Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text("🛏️ ${prop.bedrooms} Vyumba", fontSize = 11.sp, color = DopNavyPrimary)
                                Text("🚿 ${prop.bathrooms} Mabafu", fontSize = 11.sp, color = DopNavyPrimary)
                                Text("📐 ${prop.areaSqm} m²", fontSize = 11.sp, color = DopNavyPrimary)
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = DopNeutralPearl)
                            Spacer(modifier = Modifier.height(10.dp))

                            // Action Buttons Grid
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // View Details
                                OutlinedButton(
                                    onClick = { onViewProperty(prop) },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).height(38.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DopNavyPrimary)
                                ) {
                                    Text("Tazama (View)", fontSize = 11.sp)
                                }

                                // Upload / Link Short
                                Button(
                                    onClick = {
                                        postShortProperty = prop
                                        showPostShortDialog = true
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = DopOchre),
                                    modifier = Modifier.weight(1.1f).height(38.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp)
                                ) {
                                    Icon(Icons.Default.VideoCall, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Short Reel", fontSize = 11.sp, color = Color.White)
                                }

                                // Pause / Resume
                                OutlinedButton(
                                    onClick = {
                                        PropertyRepository.toggleListingPause(prop.id)
                                        val isNowPaused = prop.listingStatus == ListingStatus.PUBLISHED
                                        Toast.makeText(
                                            context,
                                            if (isNowPaused) "Mali ya '${prop.title}' imesitishwa kwa muda." else "Mali ya '${prop.title}' imewashwa hewani tena!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).height(38.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp)
                                ) {
                                    val isPaused = prop.listingStatus == ListingStatus.PAUSED
                                    Icon(
                                        if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                        contentDescription = null,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(if (isPaused) "Washa" else "Sitisha", fontSize = 11.sp)
                                }

                                // Boost Button
                                IconButton(
                                    onClick = { boostingProperty = prop },
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = if (prop.isBoosted) DopTrustGreenContainer else DopOchreContainer
                                    ),
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Bolt,
                                        contentDescription = "Boost",
                                        tint = if (prop.isBoosted) DopTrustGreen else DopOchre,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // TAB 2: SHORTS ZANGU (MANAGE VIDEO REELS)
            // Analytics Bento Overview
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DopNavyPrimary)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Usimamizi wa Reels (Shorts)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Tathmini ya video zako za mali Dar es Salaam", fontSize = 11.sp, color = DopOchreLight)
                            }
                            DopBadge("SHORTS STUDIO", Icons.Default.VideoLibrary, DopOchreLight, DopNavyElevated)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        val totalViews = myShorts.sumOf { it.viewsCount }
                        val totalLikes = myShorts.sumOf { it.likesCount }
                        val totalComments = myShorts.sumOf { it.commentsCount }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Reels Hewani", fontSize = 10.sp, color = DopNeutralPearl.copy(alpha = 0.7f))
                                Text("${myShorts.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Column {
                                Text("Watazamaji (Views)", fontSize = 10.sp, color = DopNeutralPearl.copy(alpha = 0.7f))
                                Text(String.format("%,d", totalViews), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DopOchreLight)
                            }
                            Column {
                                Text("Vipendwa (Likes)", fontSize = 10.sp, color = DopNeutralPearl.copy(alpha = 0.7f))
                                Text(String.format("%,d", totalLikes), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DopTrustGreenLight)
                            }
                            Column {
                                Text("Maswali (Inquiries)", fontSize = 10.sp, color = DopNeutralPearl.copy(alpha = 0.7f))
                                Text(String.format("%,d", totalComments), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    postShortProperty = null
                                    showPostShortDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DopOchre),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).testTag("owner_post_new_short_btn")
                            ) {
                                Icon(Icons.Default.VideoCall, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Pandisha Reel Mpya", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = onOpenShortsFeed,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Tazama Feed", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Pro Tip Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DopOchreContainer.copy(alpha = 0.4f)),
                    border = BorderStroke(1.dp, DopOchre.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = DopOchre, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Kidokezo: Video za sekunde 15–30 zikionyesha sebule, jiko na choo cha master zinaongeza maombi ya kupanga kwa 75% Dar es Salaam!",
                            fontSize = 11.sp,
                            color = DopNavyPrimary,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // List of Owner's Shorts
            if (myShorts.isEmpty()) {
                item {
                    DopBentoCard {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 30.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = DopTextSecondary, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Bado haujapandisha Short Video yoyote.", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DopNavyPrimary)
                            Text("Rekodi video fupi ya chumba au sebule ili kuvutia wapangaji haraka.", fontSize = 11.sp, color = DopTextSecondary)
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { showPostShortDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = DopOchre)
                            ) {
                                Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Pandisha Video ya Kwanza")
                            }
                        }
                    }
                }
            } else {
                items(myShorts) { short ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("owner_short_item_${short.id}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Thumbnail Preview with Play overlay
                            Box(
                                modifier = Modifier
                                    .size(width = 90.dp, height = 120.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { onOpenShortsFeed() }
                            ) {
                                AsyncImage(
                                    model = short.thumbnailUrl,
                                    contentDescription = short.propertyTitle,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.35f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.PlayCircleFilled,
                                        contentDescription = "Play",
                                        tint = Color.White,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color.Black.copy(alpha = 0.7f),
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(4.dp)
                                ) {
                                    Text(
                                        "0:30",
                                        fontSize = 9.sp,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Details & Metrics
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = short.propertyTitle,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = DopNavyPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${short.location} • ${short.priceDisplay}",
                                    fontSize = 11.sp,
                                    color = DopTextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = short.description,
                                    fontSize = 11.sp,
                                    color = DopTextMuted,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Live Metrics
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Visibility, contentDescription = null, tint = DopNavyPrimary, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("${short.viewsCount}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DopNavyPrimary)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Favorite, contentDescription = null, tint = DopError, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("${short.likesCount}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DopNavyPrimary)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = DopOchre, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("${short.commentsCount}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DopNavyPrimary)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Action Buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    // View Comments / Inquiries
                                    Button(
                                        onClick = { shortForComments = short },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = DopNavyPrimary),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.weight(1f).height(32.dp).testTag("owner_short_comments_${short.id}")
                                    ) {
                                        Icon(Icons.Default.QuestionAnswer, contentDescription = null, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Maswali (${short.commentsCount})", fontSize = 10.sp)
                                    }

                                    // Preview
                                    OutlinedButton(
                                        onClick = onOpenShortsFeed,
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("Preview", fontSize = 10.sp)
                                    }

                                    // Delete
                                    IconButton(
                                        onClick = { shortToDelete = short },
                                        modifier = Modifier.size(32.dp).testTag("owner_delete_short_${short.id}")
                                    ) {
                                        Icon(Icons.Default.DeleteOutline, contentDescription = "Futa", tint = DopError, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Boost Sheet
    boostingProperty?.let { prop ->
        PaymentCheckoutSheet(
            title = "Kupandisha Hadhi Nyumba (Listing Boost)",
            subtitle = "Mali: ${prop.title} (${prop.id})",
            amountTzs = 15000L,
            source = RevenueSource.LISTING_BOOST,
            onDismiss = { boostingProperty = null },
            onPaymentSuccess = { tx ->
                PropertyRepository.boostListing(
                    propertyId = prop.id,
                    tier = ListingBoostTier.SILVER,
                    payerName = currentUser?.name ?: "Mwenye Nyumba",
                    payerPhone = tx.payerPhone,
                    method = tx.method
                )
                boostingProperty = null
                Toast.makeText(
                    context,
                    "Hongera! Mali yako sasa ipo daraja la juu (SILVER BOOST). Muamala: ${tx.referenceNo}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    // Post Short Dialog
    if (showPostShortDialog) {
        PostPropertyShortDialog(
            properties = myProperties,
            ownerName = currentUser?.name ?: "Mwenye Nyumba",
            ownerPhone = currentUser?.phone ?: "+255 784 567 890",
            preSelectedPropertyId = postShortProperty?.id,
            onDismiss = {
                showPostShortDialog = false
                postShortProperty = null
            },
            onPost = { newShort ->
                PropertyShortsRepository.postShort(newShort)
                showPostShortDialog = false
                postShortProperty = null
                Toast.makeText(
                    context,
                    "Hongera! Short Video ya '${newShort.propertyTitle}' imechapishwa hewani! Wateja wataiona kwenye feed sasa.",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    // Short Comments / Inquiries Dialog
    shortForComments?.let { short ->
        OwnerShortCommentsDialog(
            short = short,
            onDismiss = { shortForComments = null },
            onSendReply = { reply ->
                PropertyShortsRepository.addComment(
                    shortId = short.id,
                    userName = currentUser?.name ?: "Eng. Grace Ndesamburo",
                    userRole = "Mwenye Nyumba (Owner)",
                    text = reply
                )
                Toast.makeText(context, "Jibu lako limetumwa!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Delete Short Confirmation
    shortToDelete?.let { short ->
        DeleteShortConfirmDialog(
            short = short,
            onDismiss = { shortToDelete = null },
            onConfirmDelete = {
                PropertyShortsRepository.deleteShort(short.id)
                shortToDelete = null
                Toast.makeText(context, "Short Video imefutwa!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

// ============================================================
// OWNER VIEWINGS (INCOMING VISITS & TOUR REQUESTS)
// ============================================================
@Composable
fun OwnerViewingsScreen(
    lang: AppLanguage,
    onViewProperty: (Property) -> Unit = {},
    onViewRequest: ((ViewingBooking) -> Unit)? = null
) {
    val bookings by PropertyRepository.viewingBookings.collectAsState()
    val properties by PropertyRepository.properties.collectAsState()
    val currentUser by AuthManager.currentUser.collectAsState()
    val context = LocalContext.current

    var selectedStatusTab by remember { mutableIntStateOf(0) } // 0 = Zote, 1 = Zinazosubiri, 2 = Zilizothibitishwa, 3 = Zilizokamilika

    val myPropertyIds = remember(properties, currentUser) {
        val owned = properties.filter {
            it.ownerId == currentUser?.id || it.ownerId == "OWN-882" || it.ownerId == "OWN-TZA-0882"
        }
        (if (owned.isEmpty()) properties else owned).map { it.id }.toSet()
    }

    val myBookings = remember(bookings, myPropertyIds) {
        bookings.filter { it.propertyId in myPropertyIds || it.propertyId.contains("DOP") }
    }

    val pendingCount = remember(myBookings) {
        myBookings.count { it.status == ViewingStatus.REQUESTED || it.status == ViewingStatus.PENDING_OWNER_CONFIRMATION }
    }
    val confirmedCount = remember(myBookings) {
        myBookings.count { it.status == ViewingStatus.CONFIRMED || it.status == ViewingStatus.GUIDE_ASSIGNED || it.status == ViewingStatus.GUIDE_ACCEPTED }
    }

    val filteredBookings = remember(myBookings, selectedStatusTab) {
        when (selectedStatusTab) {
            0 -> myBookings
            1 -> myBookings.filter { it.status == ViewingStatus.REQUESTED || it.status == ViewingStatus.PENDING_OWNER_CONFIRMATION }
            2 -> myBookings.filter { it.status == ViewingStatus.CONFIRMED || it.status == ViewingStatus.GUIDE_ASSIGNED || it.status == ViewingStatus.GUIDE_ACCEPTED }
            3 -> myBookings.filter { it.status == ViewingStatus.COMPLETED }
            else -> myBookings
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DopNeutralPearl)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header
        item {
            Column {
                Text(
                    text = "Maombi ya Ukaguzi wa Nyumba (Viewings)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DopNavyPrimary
                )
                Text(
                    text = "Thibitisha ratiba za wateja na miongozo ya DoP Guides mtaani",
                    fontSize = 11.sp,
                    color = DopTextSecondary
                )
            }
        }

        // Summary KPI Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DopNavyPrimary)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Jumla ya Maombi", fontSize = 11.sp, color = DopNeutralPearl.copy(alpha = 0.7f))
                        Text("${myBookings.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Column {
                        Text("Zinazosubiri", fontSize = 11.sp, color = DopNeutralPearl.copy(alpha = 0.7f))
                        Text("$pendingCount", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DopOchreLight)
                    }
                    Column {
                        Text("Zilizothibitishwa", fontSize = 11.sp, color = DopNeutralPearl.copy(alpha = 0.7f))
                        Text("$confirmedCount", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DopTrustGreenLight)
                    }
                }
            }
        }

        // Filter Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val tabs = listOf("Zote (${myBookings.size})", "Zinazosubiri ($pendingCount)", "Zilizothibitishwa ($confirmedCount)", "Zilizokamilika")
                tabs.forEachIndexed { idx, title ->
                    val isSelected = selectedStatusTab == idx
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedStatusTab = idx },
                        label = { Text(title, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DopNavyPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        if (filteredBookings.isEmpty()) {
            item {
                DopBentoCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.EventAvailable, contentDescription = null, tint = DopTextSecondary, modifier = Modifier.size(44.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Hakuna maombi ya kutazama katika kundi hili.", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DopNavyPrimary)
                        Text("Maombi mapya ya wateja yatatokea hapa papo hapo.", fontSize = 11.sp, color = DopTextSecondary)
                    }
                }
            }
        } else {
            items(filteredBookings) { booking ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("owner_viewing_card_${booking.bookingCode}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Customer & Status Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(DopNavyPrimary.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = DopNavyPrimary, modifier = Modifier.size(22.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(booking.customerName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DopNavyPrimary)
                                    Text(booking.customerPhone, fontSize = 11.sp, color = DopTextSecondary)
                                }
                            }

                            val isConfirmed = booking.status == ViewingStatus.CONFIRMED || booking.status == ViewingStatus.GUIDE_ASSIGNED || booking.status == ViewingStatus.GUIDE_ACCEPTED
                            val isCompleted = booking.status == ViewingStatus.COMPLETED
                            DopBadge(
                                text = when (booking.status) {
                                    ViewingStatus.REQUESTED, ViewingStatus.PENDING_OWNER_CONFIRMATION -> "INASUBIRI RIDHAA"
                                    ViewingStatus.CONFIRMED -> "IMETHIBITISHWA"
                                    ViewingStatus.GUIDE_ASSIGNED, ViewingStatus.GUIDE_ACCEPTED -> "GUIDE AMEKABIDHIWA"
                                    ViewingStatus.COMPLETED -> "IMEKAMILIKA"
                                    else -> booking.status.name
                                },
                                icon = if (isCompleted) Icons.Default.CheckCircle else if (isConfirmed) Icons.Default.EventAvailable else Icons.Default.HourglassEmpty,
                                color = if (isCompleted) DopTrustGreen else if (isConfirmed) DopNavyPrimary else DopOchre,
                                backgroundColor = if (isCompleted) DopTrustGreenContainer else if (isConfirmed) DopNavyPrimary.copy(alpha = 0.1f) else DopOchreContainer
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Property Details
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DopNeutralPearl,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.HomeWork, contentDescription = null, tint = DopNavyPrimary, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(booking.propertyTitle, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DopNavyPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text("Nambari ya Miadi: ${booking.bookingCode}", fontSize = 10.sp, color = DopTextSecondary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Date & Time
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = DopOchre, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Tarehe: ${booking.scheduledDate} • Saa: ${booking.scheduledTimeSlot}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DopNavyPrimary
                            )
                        }

                        // Guide Assigned Tag
                        booking.guideName?.let { guide ->
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DirectionsWalk, contentDescription = null, tint = DopTrustGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "DoP Guide: $guide (Kiongozi wa Mtaani)",
                                    fontSize = 11.sp,
                                    color = DopTrustGreen,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Customer Notes
                        if (booking.customerNotes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Ujumbe wa Mteja: \"${booking.customerNotes}\"",
                                fontSize = 11.sp,
                                color = DopTextMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = DopNeutralPearl)
                        Spacer(modifier = Modifier.height(10.dp))

                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val isPending = booking.status == ViewingStatus.REQUESTED || booking.status == ViewingStatus.PENDING_OWNER_CONFIRMATION
                            if (isPending) {
                                Button(
                                    onClick = {
                                        PropertyRepository.advanceViewingStatus(booking.bookingCode, ViewingStatus.CONFIRMED)
                                        Toast.makeText(context, "Miadi ya ${booking.customerName} imethibitishwa!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = DopTrustGreen),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1.3f).height(38.dp).testTag("confirm_viewing_${booking.bookingCode}")
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Thibitisha Miadi", fontSize = 11.sp)
                                }
                            } else if (booking.status == ViewingStatus.CONFIRMED || booking.status == ViewingStatus.GUIDE_ASSIGNED || booking.status == ViewingStatus.GUIDE_ACCEPTED) {
                                Button(
                                    onClick = {
                                        PropertyRepository.advanceViewingStatus(booking.bookingCode, ViewingStatus.COMPLETED)
                                        Toast.makeText(context, "Ziara imekamilika! Hongera.", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = DopNavyPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1.3f).height(38.dp).testTag("complete_viewing_${booking.bookingCode}")
                                ) {
                                    Icon(Icons.Default.TaskAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Weka Imekamilika", fontSize = 11.sp)
                                }
                            }

                            // Contact Tenant
                            OutlinedButton(
                                onClick = {
                                    Toast.makeText(context, "Wasiliana na ${booking.customerName}: ${booking.customerPhone}", Toast.LENGTH_LONG).show()
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).height(38.dp).testTag("call_viewing_${booking.bookingCode}")
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Piga Simu", fontSize = 11.sp)
                            }

                            // Cancel / Decline if pending
                            if (isPending) {
                                OutlinedButton(
                                    onClick = {
                                        PropertyRepository.advanceViewingStatus(booking.bookingCode, ViewingStatus.CANCELLED)
                                        Toast.makeText(context, "Miadi imeahirishwa.", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DopError),
                                    border = BorderStroke(1.dp, DopError.copy(alpha = 0.5f)),
                                    modifier = Modifier.height(38.dp).testTag("cancel_viewing_${booking.bookingCode}")
                                ) {
                                    Text("Ghairi", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
