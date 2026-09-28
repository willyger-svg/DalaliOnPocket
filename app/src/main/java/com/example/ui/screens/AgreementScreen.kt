package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.localization.AppLanguage
import com.example.core.localization.DoPStrings
import com.example.data.model.TenancyAgreement
import com.example.data.model.TenancyAgreementStatus
import com.example.data.model.UserRole
import com.example.data.model.RevenueSource
import com.example.data.model.WalletTransaction
import com.example.data.repository.PropertyRepository
import com.example.ui.components.DopBadge
import com.example.ui.components.DopBentoCard
import com.example.ui.components.PaymentCheckoutSheet
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenancyAgreementScreen(
    agreement: TenancyAgreement,
    currentUserRole: UserRole,
    lang: AppLanguage,
    onBack: () -> Unit
) {
    val currentAgreementState by PropertyRepository.tenancyAgreements.collectAsState()
    val liveAgreement = currentAgreementState.find { it.agreementCode == agreement.agreementCode } ?: agreement
    val transactions by PropertyRepository.transactions.collectAsState()

    val isLocked = liveAgreement.status == TenancyAgreementStatus.LOCKED || liveAgreement.lockedAt != null
    val customerAccepted = liveAgreement.customerAcceptedAt != null
    val ownerAccepted = liveAgreement.ownerAcceptedAt != null

    // Check if this agreement has completed payment
    val paidTransaction = transactions.firstOrNull { 
        it.description.contains(liveAgreement.agreementCode) || it.source == RevenueSource.TENANCY_COMMISSION 
    }
    val isPaid = paidTransaction != null

    var showPaymentSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Mkataba wa Upangaji / Tenancy Agreement",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = liveAgreement.agreementCode,
                            fontSize = 12.sp,
                            color = DopOchreLight
                        )
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(DopNeutralPearl)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Agreement Status Banner
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isLocked) DopTrustGreenContainer else DopOchreContainer,
                    border = BorderStroke(1.dp, if (isLocked) DopTrustGreen else DopOchre)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.PendingActions,
                            contentDescription = null,
                            tint = if (isLocked) DopTrustGreen else DopOchre,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isLocked) "MKATABA UMEFUNGWA (LOCKED & MUTUALLY ACCEPTED)" else "INASUBIRI MAKUBALIANO YA PANDE ZOTE",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isLocked) DopTrustGreen else DopOchre
                            )
                            Text(
                                text = if (isLocked) 
                                    "Pande zote mbili (Mpangaji na Mwenye Nyumba) wamethibitisha makubaliano haya. Hali: Inasubiri Malipo."
                                    else "Mpangaji na Mwenye Nyumba wanapaswa kubonyeza kukubali ili kufunga mkataba huu.",
                                fontSize = 11.sp,
                                color = DopTextSecondary
                            )
                        }
                    }
                }
            }

            // Parties Section
            item {
                DopBentoCard {
                    Text(
                        text = "Pande Zinazohusika (Parties to the Lease)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = DopNavyPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Mwenye Nyumba (Landlord)", fontSize = 11.sp, color = DopTextSecondary)
                            Text(liveAgreement.ownerName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            if (ownerAccepted) {
                                DopBadge("Amekubali / Accepted", Icons.Default.Check, DopTrustGreen, DopTrustGreenContainer)
                            } else {
                                DopBadge("Inasubiri / Pending", Icons.Default.HourglassEmpty, DopOchre, DopOchreContainer)
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Mpangaji (Tenant)", fontSize = 11.sp, color = DopTextSecondary)
                            Text(liveAgreement.tenantName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            if (customerAccepted) {
                                DopBadge("Amekubali / Accepted", Icons.Default.Check, DopTrustGreen, DopTrustGreenContainer)
                            } else {
                                DopBadge("Inasubiri / Pending", Icons.Default.HourglassEmpty, DopOchre, DopOchreContainer)
                            }
                        }
                    }
                }
            }

            // Critical Business Rule: Financial Breakdown Ledger
            item {
                DopBentoCard {
                    Text(
                        text = "Mchanganuo Rasmi wa Kifedha (Financial Ledger)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = DopNavyPrimary
                    )
                    Text(
                        text = "Kulingana na mwongozo wa DoP: Kodi ya jukwaa inatozwa 50% ya kodi ya mwezi wa kwanza PEKEE (one-time fee).",
                        fontSize = 11.sp,
                        color = DopTextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    LedgerRow("Kodi ya Mwezi (Monthly Rent)", DoPStrings.tzs(liveAgreement.monthlyRentTzs))
                    LedgerRow("Muda wa Kupanga (Lease Duration)", "${liveAgreement.durationMonths} miezi / months")
                    LedgerRow("Jumla ya Kodi ya Upangaji", DoPStrings.tzs(liveAgreement.monthlyRentTzs * liveAgreement.durationMonths))
                    LedgerRow("Amana ya Usalama (Security Deposit)", DoPStrings.tzs(liveAgreement.depositTzs))
                    
                    // Highlight 50% DoP fee
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DopBorderSubtle)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "DoP Platform Fee (50% ya mwezi wa kwanza)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = DopOchre
                            )
                            Text("50% × TSh 1,800,000 (Mara moja tu)", fontSize = 10.sp, color = DopTextMuted)
                        }
                        Text(
                            text = DoPStrings.tzs(liveAgreement.dopPlatformFeeTzs),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = DopOchre
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DopBorderSubtle)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Jumla Inayolipwa Awali (Total Initial Payable)",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = DopNavyPrimary
                        )
                        Text(
                            text = DoPStrings.tzs(liveAgreement.totalInitialPayableTzs),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = DopTrustGreen
                        )
                    }
                }
            }

            // Agreement Terms & Audit Metadata
            item {
                DopBentoCard {
                    Text(
                        text = "Vifungu na Uthibitisho wa Kisheria",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = DopNavyPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = liveAgreement.termsSummary,
                        fontSize = 12.sp,
                        color = DopTextSecondary,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Kumbukumbu ya Ukaguzi (Audit Metadata):", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("• Toleo la Mkataba (Version): ${liveAgreement.version}.0", fontSize = 10.sp, color = DopTextSecondary)
                            Text("• Tarehe ya Mpangaji: ${if (customerAccepted) "Imethibitishwa kielektroniki" else "Bado"}", fontSize = 10.sp, color = DopTextSecondary)
                            Text("• Tarehe ya Mwenye Nyumba: ${if (ownerAccepted) "Imethibitishwa kielektroniki" else "Bado"}", fontSize = 10.sp, color = DopTextSecondary)
                            Text("• Usalama: DoP Escrow Vault Protection Active", fontSize = 10.sp, color = DopTrustGreen)
                        }
                    }
                }
            }

            // Action Buttons for Mutual Acceptance
            item {
                if (!isLocked) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (!customerAccepted && currentUserRole == UserRole.CUSTOMER) {
                            Button(
                                onClick = {
                                    PropertyRepository.customerAcceptAgreement(liveAgreement.agreementCode)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DopNavyPrimary),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("customer_accept_button")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Nikiwa kama Mpangaji: Nakubali Mkataba Huu")
                            }
                        }

                        if (!ownerAccepted && currentUserRole == UserRole.OWNER) {
                            Button(
                                onClick = {
                                    PropertyRepository.ownerAcceptAgreement(liveAgreement.agreementCode)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DopOchre),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("owner_accept_button")
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Nikiwa kama Mwenye Nyumba: Nakubali na Kufunga Mkataba")
                            }
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = DopNavyPrimary,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Lock, contentDescription = null, tint = DopTrustGreenLight)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Mkataba Umefungwa Kisheria",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                    DopBadge("LOCKED", Icons.Default.Shield, DopTrustGreenLight, DopNavyElevated)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Pande zote mbili zimethibitisha makubaliano haya kidijitali chini ya sheria za upangishaji Tanzania.",
                                    fontSize = 11.sp,
                                    color = DopNeutralPearl.copy(alpha = 0.85f)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = DopSurfaceCard,
                            border = BorderStroke(1.dp, if (isPaid) DopTrustGreen else DopBorderSubtle),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.AccountBalanceWallet,
                                            contentDescription = null,
                                            tint = if (isPaid) DopTrustGreen else DopOchre,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isPaid) "Malipo Yamethibitishwa (Paid)" else "Malipo ya Kodi & Ada ya DoP",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = DopNavyPrimary
                                        )
                                    }
                                    if (isPaid) {
                                        DopBadge("IMELIPWA", Icons.Default.CheckCircle, DopTrustGreen, DopTrustGreenContainer)
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Jumla inayolipwa: ${DoPStrings.tzs(liveAgreement.totalInitialPayableTzs)} (Kodi ya Miezi ${liveAgreement.durationMonths} + Dhamana + Ada ya DoP 50% ya mwezi 1 pekee).",
                                    fontSize = 12.sp,
                                    color = DopTextPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                
                                if (isPaid && paidTransaction != null) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = DopTrustGreenContainer,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = DopTrustGreen, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Risiti Rasmi ya EFD & Malipo ya Simu", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DopTrustGreen)
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text("• Namba ya Kumbukumbu: ${paidTransaction.referenceNo}", fontSize = 10.sp, color = DopTextPrimary)
                                            Text("• Namba ya EFD: ${paidTransaction.efdReceiptCode}", fontSize = 10.sp, color = DopTextPrimary)
                                            Text("• Njia Iliyotumika: ${paidTransaction.method.title} (${paidTransaction.payerPhone})", fontSize = 10.sp, color = DopTextPrimary)
                                            Text("• Hali: Escrow Imefungwa & Mkataba Umewashwa", fontSize = 10.sp, color = DopTrustGreen, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                } else {
                                    Text(
                                        text = "Lipa papo hapo kupitia M-Pesa, Tigo Pesa, Airtel Money, Halopesa au benki (CRDB/NMB) kupitia USSD Push salama.",
                                        fontSize = 11.sp,
                                        color = DopTextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = { showPaymentSheet = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = DopTrustGreen),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .testTag("pay_tenancy_escrow_btn")
                                    ) {
                                        Icon(Icons.Default.Payments, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Fanya Malipo ya Awali Sasa", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showPaymentSheet) {
        PaymentCheckoutSheet(
            title = "Malipo ya Upangaji",
            subtitle = "Mkataba: ${liveAgreement.agreementCode} • ${liveAgreement.propertyTitle}",
            amountTzs = liveAgreement.totalInitialPayableTzs,
            source = RevenueSource.TENANCY_COMMISSION,
            onDismiss = { showPaymentSheet = false },
            onPaymentSuccess = { tx ->
                PropertyRepository.payTenancyAgreement(
                    agreementCode = liveAgreement.agreementCode,
                    payerPhone = tx.payerPhone,
                    method = tx.method
                )
                showPaymentSheet = false
            }
        )
    }
}

@Composable
private fun LedgerRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = DopTextSecondary)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
