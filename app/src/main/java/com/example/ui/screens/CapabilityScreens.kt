package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.core.auth.AuthManager
import com.example.data.model.*
import com.example.ui.components.DopBadge
import com.example.ui.components.DopBentoCard
import com.example.ui.theme.*

// ============================================================
// 1. OWNER ONBOARDING FLOW (SIMPLE & STREAMLINED)
// ============================================================
@Composable
fun OwnerOnboardingDialog(
    onDismiss: () -> Unit,
    onSuccessActivated: () -> Unit
) {
    var step by remember { mutableIntStateOf(1) }
    val totalSteps = 6

    // Form state
    var listingReason by remember { mutableStateOf("Upangaji wa Nyumba (Rental Income)") }
    var ownerType by remember { mutableStateOf(OwnerType.INDIVIDUAL) }
    var displayName by remember { mutableStateOf("") }
    var businessName by remember { mutableStateOf("") }
    var taxIdOrNin by remember { mutableStateOf("") }
    var contactPhone by remember { mutableStateOf("") }
    var selectedRegion by remember { mutableStateOf("Dar es Salaam") }
    var selectedDistricts by remember { mutableStateOf(setOf("Kinondoni", "Ilala")) }
    var acceptedOwnerTerms by remember { mutableStateOf(false) }

    val currentUser by AuthManager.currentUser.collectAsState()

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            if (displayName.isBlank()) displayName = user.name
            if (contactPhone.isBlank()) contactPhone = user.phone
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            color = DopNeutralPearl,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Top Header with Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(DopOwnerGreenContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.HomeWork, contentDescription = null, tint = DopOwnerGreen, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Washa Uwezo wa Umiliki", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DopNavyPrimary)
                            Text("Hatua ya $step kati ya $totalSteps • Owner Capability", fontSize = 11.sp, color = DopOwnerGreen)
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Funga", tint = DopTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Progress Bar (Green Owner Accent)
                LinearProgressIndicator(
                    progress = { step.toFloat() / totalSteps.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = DopOwnerGreen,
                    trackColor = DopOwnerGreenContainer
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Step Contents
                Box(modifier = Modifier.weight(1f)) {
                    when (step) {
                        1 -> StepListingReason(
                            selectedReason = listingReason,
                            onSelect = { listingReason = it }
                        )
                        2 -> StepOwnerType(
                            selectedType = ownerType,
                            onSelect = { ownerType = it }
                        )
                        3 -> StepOwnerDetails(
                            ownerType = ownerType,
                            displayName = displayName,
                            onDisplayNameChange = { displayName = it },
                            businessName = businessName,
                            onBusinessNameChange = { businessName = it },
                            taxIdOrNin = taxIdOrNin,
                            onTaxIdOrNinChange = { taxIdOrNin = it },
                            contactPhone = contactPhone,
                            onContactPhoneChange = { contactPhone = it }
                        )
                        4 -> StepOperatingLocations(
                            selectedRegion = selectedRegion,
                            onRegionSelect = { selectedRegion = it },
                            selectedDistricts = selectedDistricts,
                            onToggleDistrict = { district ->
                                selectedDistricts = if (selectedDistricts.contains(district)) {
                                    if (selectedDistricts.size > 1) selectedDistricts - district else selectedDistricts
                                } else {
                                    selectedDistricts + district
                                }
                            }
                        )
                        5 -> StepVerificationNotice()
                        6 -> StepOwnerReviewAndSubmit(
                            ownerType = ownerType,
                            displayName = displayName,
                            businessName = businessName,
                            contactPhone = contactPhone,
                            selectedRegion = selectedRegion,
                            selectedDistricts = selectedDistricts,
                            listingReason = listingReason,
                            acceptedTerms = acceptedOwnerTerms,
                            onToggleTerms = { acceptedOwnerTerms = it }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Navigation Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (step > 1) {
                        OutlinedButton(
                            onClick = { step-- },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("← Nyuma")
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    Button(
                        onClick = {
                            if (step < totalSteps) {
                                step++
                            } else {
                                // Activate Owner Capability
                                val profile = OwnerProfile(
                                    ownerType = ownerType,
                                    displayName = displayName.ifBlank { currentUser?.name ?: "Mwenye Nyumba" },
                                    businessName = businessName,
                                    taxIdOrNin = taxIdOrNin,
                                    operatingRegions = listOf(selectedRegion),
                                    operatingDistricts = selectedDistricts.toList(),
                                    primaryReason = listingReason,
                                    contactPhone = contactPhone.ifBlank { currentUser?.phone ?: "" }
                                )
                                com.example.core.account.AccountManager.activateOwnerCapability(profile)
                                onSuccessActivated()
                            }
                        },
                        enabled = if (step == 6) acceptedOwnerTerms else true,
                        colors = ButtonDefaults.buttonColors(containerColor = DopOwnerGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag(if (step == totalSteps) "activate_owner_confirm_btn" else "owner_onboarding_next_btn")
                    ) {
                        Text(
                            text = if (step < totalSteps) "Endelea →" else "Washa Uwezo wa Mmiliki",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StepListingReason(selectedReason: String, onSelect: (String) -> Unit) {
    val reasons = listOf(
        Pair("Upangaji wa Nyumba (Rental Income)", "Nina vyumba, apartimenti au nyumba ya kupangisha kila mwezi"),
        Pair("Uuzaji wa Majengo (Property Sale)", "Ninauza nyumba au jengo zima kwa wanunuzi wa uhakika"),
        Pair("Kupangisha Fremu & Maduka (Commercial)", "Nina fremu, ofisi au maduka kwenye maeneo ya biashara"),
        Pair("Uuzaji wa Ardhi & Viwanja (Plots)", "Nina viwanja vilivyopimwa vyenye hati au mikataba safi")
    )
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text("Sababu ya Kuorodhesha Mali", fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = DopNavyPrimary)
        Text("Chagua dhumuni lako kuu la kujiunga kama mwenye mali", fontSize = 12.sp, color = DopTextSecondary)
        Spacer(modifier = Modifier.height(16.dp))

        reasons.forEach { (title, desc) ->
            val isSelected = selectedReason == title
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { onSelect(title) },
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) DopOwnerGreenContainer else Color.White,
                border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) DopOwnerGreen else DopBorderSubtle)
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = isSelected, onClick = { onSelect(title) })
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = if (isSelected) DopOwnerGreenDark else DopTextPrimary)
                        Text(desc, fontSize = 11.sp, color = DopTextSecondary)
                    }
                }
            }
        }
    }
}

@Composable
private fun StepOwnerType(selectedType: OwnerType, onSelect: (OwnerType) -> Unit) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text("Aina ya Mmiliki wa Mali", fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = DopNavyPrimary)
        Text("Taarifa hii inasaidia kuweka uainishaji sahihi wa kisheria", fontSize = 12.sp, color = DopTextSecondary)
        Spacer(modifier = Modifier.height(16.dp))

        OwnerType.values().forEach { type ->
            val isSelected = selectedType == type
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { onSelect(type) },
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) DopOwnerGreenContainer else Color.White,
                border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) DopOwnerGreen else DopBorderSubtle)
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = isSelected, onClick = { onSelect(type) })
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(type.titleSw, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = if (isSelected) DopOwnerGreenDark else DopTextPrimary)
                        Text(type.titleEn, fontSize = 11.sp, color = DopTextSecondary)
                    }
                }
            }
        }
    }
}

@Composable
private fun StepOwnerDetails(
    ownerType: OwnerType,
    displayName: String,
    onDisplayNameChange: (String) -> Unit,
    businessName: String,
    onBusinessNameChange: (String) -> Unit,
    taxIdOrNin: String,
    onTaxIdOrNinChange: (String) -> Unit,
    contactPhone: String,
    onContactPhoneChange: (String) -> Unit
) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text("Taarifa za Utambulisho", fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = DopNavyPrimary)
        Text("Weka taarifa za mawasiliano na uthibitisho wa umiliki", fontSize = 12.sp, color = DopTextSecondary)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Jina Kamili la Mmiliki", fontWeight = FontWeight.Bold, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = displayName,
            onValueChange = onDisplayNameChange,
            placeholder = { Text("k.m. Eng. Grace Ndesamburo") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )

        if (ownerType == OwnerType.BUSINESS || ownerType == OwnerType.PROPERTY_MANAGER) {
            Spacer(modifier = Modifier.height(12.dp))
            Text("Jina la Kampuni / Shirika", fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = businessName,
                onValueChange = onBusinessNameChange,
                placeholder = { Text("k.m. Victoria Real Estate Tz Ltd") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text("Namba ya NIDA au TIN (Siyo lazima sasa)", fontWeight = FontWeight.Bold, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = taxIdOrNin,
            onValueChange = onTaxIdOrNinChange,
            placeholder = { Text("NIN ya NIDA au TIN ya TRA") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))
        Text("Namba ya Simu ya Mawasiliano ya Mali", fontWeight = FontWeight.Bold, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = contactPhone,
            onValueChange = onContactPhoneChange,
            placeholder = { Text("0784 567 890") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )
    }
}

@Composable
private fun StepOperatingLocations(
    selectedRegion: String,
    onRegionSelect: (String) -> Unit,
    selectedDistricts: Set<String>,
    onToggleDistrict: (String) -> Unit
) {
    val regions = listOf("Dar es Salaam") // Initial launch focused on Dar es Salaam
    val darDistricts = listOf("Kinondoni", "Ilala", "Temeke", "Kigamboni", "Ubungo")

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text("Maeneo ya Uendeshaji", fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = DopNavyPrimary)
        Text("Chagua mikoa na wilaya ambapo mali zako zinapatikana", fontSize = 12.sp, color = DopTextSecondary)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Mkoa Mkuu:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            regions.take(3).forEach { region ->
                val isSelected = selectedRegion == region
                FilterChip(
                    selected = isSelected,
                    onClick = { onRegionSelect(region) },
                    label = { Text(region, fontSize = 11.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Wilaya Zinazohusika (Chagua moja au zaidi):", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(8.dp))

        darDistricts.forEach { district ->
            val isChecked = selectedDistricts.contains(district)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onToggleDistrict(district) }
                    .padding(vertical = 6.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = isChecked, onCheckedChange = { onToggleDistrict(district) })
                Spacer(modifier = Modifier.width(8.dp))
                Text(district, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun StepVerificationNotice() {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text("Maandalizi ya Uhakiki wa Mali", fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = DopNavyPrimary)
        Text("Viwango vya usalama vya Dalalion Pocket Tanzania", fontSize = 12.sp, color = DopTextSecondary)
        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = BorderStroke(1.dp, DopOwnerGreenBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = DopOwnerGreen, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Uhakiki Halisi wa Hati (Zero Fake)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DopOwnerGreenDark)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ili kulinda wapangaji na wamiliki, kila nyumba au kiwanja kinachowekwa kitapitia ukaguzi wa kimwili (physical verification) na ukaguzi wa uthibitisho wa umiliki (Hati ya Ardhi, Barua ya Mtaa, au Mkataba wa Uwakilishi) kabla ya kuwa hewani.",
                    fontSize = 11.sp,
                    color = DopTextSecondary,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "• Mkataba wa DoP unalinda 100% ya amana zako.\n• Unaweza kuongeza nyumba mara baada ya kuwasha uwezo huu.",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DopNavyPrimary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun StepOwnerReviewAndSubmit(
    ownerType: OwnerType,
    displayName: String,
    businessName: String,
    contactPhone: String,
    selectedRegion: String,
    selectedDistricts: Set<String>,
    listingReason: String,
    acceptedTerms: Boolean,
    onToggleTerms: (Boolean) -> Unit
) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text("Muhtasari na Makubaliano", fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = DopNavyPrimary)
        Text("Kagua taarifa kabla ya kuwasha uwezo wa umiliki", fontSize = 12.sp, color = DopTextSecondary)
        Spacer(modifier = Modifier.height(16.dp))

        DopBentoCard {
            Text("Muhtasari wa Uwezo wa Mmiliki", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DopOwnerGreenDark)
            Spacer(modifier = Modifier.height(8.dp))
            Text("• Jina la Mmiliki: $displayName", fontSize = 11.sp)
            if (businessName.isNotBlank()) Text("• Kampuni: $businessName", fontSize = 11.sp)
            Text("• Aina: ${ownerType.titleSw}", fontSize = 11.sp)
            Text("• Simu: $contactPhone", fontSize = 11.sp)
            Text("• Eneo: $selectedRegion (${selectedDistricts.joinToString(", ")})", fontSize = 11.sp)
            Text("• Kusudi: $listingReason", fontSize = 11.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = acceptedTerms, onCheckedChange = onToggleTerms)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Ninakubali Sera ya Usimamizi wa Mali na Kanuni za Kodi ya Upangaji ya DoP (50% ya mwezi mmoja pekee).",
                fontSize = 11.sp,
                color = DopTextSecondary,
                lineHeight = 15.sp
            )
        }
    }
}

// ============================================================
// 2. GUIDE APPLICATION FLOW (STRICT OPERATIONAL VETTING)
// ============================================================
@Composable
fun GuideApplicationDialog(
    onDismiss: () -> Unit,
    onSuccessSubmitted: () -> Unit
) {
    var step by remember { mutableIntStateOf(1) }
    val totalSteps = 10

    // Form states
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var nationalIdNin by remember { mutableStateOf("") }
    var operatingZones by remember { mutableStateOf(setOf("Kinondoni", "Sinza", "Mikocheni")) }
    var experienceYears by remember { mutableIntStateOf(2) }
    var transportMode by remember { mutableStateOf("Pikipiki / Bodaboda") }
    var languages by remember { mutableStateOf(setOf("Kiswahili", "Kiingereza")) }
    var availability by remember { mutableStateOf("Muda Wote (Full Time)") }
    var emergencyName by remember { mutableStateOf("") }
    var emergencyPhone by remember { mutableStateOf("") }
    var acceptedConduct by remember { mutableStateOf(false) }

    val currentUser by AuthManager.currentUser.collectAsState()

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            if (fullName.isBlank()) fullName = user.name
            if (phone.isBlank()) phone = user.phone
            if (email.isBlank()) email = user.email
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            color = DopNeutralPearl,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Top Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(DopGuideAmberContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.DirectionsWalk, contentDescription = null, tint = DopGuideAmber, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Maombi ya Kuwa DoP Guide", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DopNavyPrimary)
                            Text("Hatua ya $step kati ya $totalSteps • Operational Vetting", fontSize = 11.sp, color = DopGuideAmberDark)
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Funga", tint = DopTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Progress Bar (Amber Guide Accent)
                LinearProgressIndicator(
                    progress = { step.toFloat() / totalSteps.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = DopGuideAmber,
                    trackColor = DopGuideAmberContainer
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(modifier = Modifier.weight(1f)) {
                    when (step) {
                        1 -> GuideStepPersonalInfo(fullName, { fullName = it }, phone, { phone = it }, email, { email = it })
                        2 -> GuideStepIdentity(nationalIdNin, { nationalIdNin = it })
                        3 -> GuideStepZones(operatingZones) { zone ->
                            operatingZones = if (operatingZones.contains(zone)) operatingZones - zone else operatingZones + zone
                        }
                        4 -> GuideStepExperience(experienceYears, { experienceYears = it })
                        5 -> GuideStepTransport(transportMode, { transportMode = it })
                        6 -> GuideStepLanguages(languages) { lang ->
                            languages = if (languages.contains(lang)) languages - lang else languages + lang
                        }
                        7 -> GuideStepAvailability(availability, { availability = it })
                        8 -> GuideStepEmergency(emergencyName, { emergencyName = it }, emergencyPhone, { emergencyPhone = it })
                        9 -> GuideStepConduct(acceptedConduct, { acceptedConduct = it })
                        10 -> GuideStepReview(
                            fullName = fullName,
                            phone = phone,
                            nationalIdNin = nationalIdNin,
                            operatingZones = operatingZones,
                            experienceYears = experienceYears,
                            transportMode = transportMode,
                            availability = availability
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (step > 1) {
                        OutlinedButton(
                            onClick = { step-- },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("← Nyuma")
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    Button(
                        onClick = {
                            if (step < totalSteps) {
                                step++
                            } else {
                                val application = GuideApplication(
                                    fullName = fullName.ifBlank { currentUser?.name ?: "Mwombaji Guide" },
                                    phone = phone.ifBlank { currentUser?.phone ?: "" },
                                    email = email,
                                    nationalIdNin = nationalIdNin,
                                    operatingZones = operatingZones.toList(),
                                    experienceYears = experienceYears,
                                    transportMode = transportMode,
                                    languages = languages.toList(),
                                    availability = availability,
                                    emergencyContactName = emergencyName,
                                    emergencyContactPhone = emergencyPhone,
                                    acceptedCodeOfConduct = acceptedConduct,
                                    status = GuideCapabilityStatus.SUBMITTED
                                )
                                com.example.core.account.AccountManager.submitGuideApplication(application)
                                onSuccessSubmitted()
                            }
                        },
                        enabled = if (step == 9) acceptedConduct else true,
                        colors = ButtonDefaults.buttonColors(containerColor = DopGuideAmber),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag(if (step == totalSteps) "submit_guide_app_btn" else "guide_onboarding_next_btn")
                    ) {
                        Text(
                            text = if (step < totalSteps) "Endelea →" else "Tuma Maombi ya Udalali",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GuideStepPersonalInfo(name: String, onNameChange: (String) -> Unit, phone: String, onPhoneChange: (String) -> Unit, email: String, onEmailChange: (String) -> Unit) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text("1. Taarifa Binafsi", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = DopNavyPrimary)
        Text("Weka taarifa rasmi zinazothibitisha utambulisho wako", fontSize = 11.sp, color = DopTextSecondary)
        Spacer(modifier = Modifier.height(14.dp))
        Text("Jina Kamili (Kama lilivyo kwenye Kitambulisho)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(value = name, onValueChange = onNameChange, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
        Spacer(modifier = Modifier.height(10.dp))
        Text("Namba ya Simu", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(value = phone, onValueChange = onPhoneChange, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
        Spacer(modifier = Modifier.height(10.dp))
        Text("Barua Pepe (Email)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(value = email, onValueChange = onEmailChange, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
    }
}

@Composable
private fun GuideStepIdentity(nin: String, onNinChange: (String) -> Unit) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text("2. Utambulisho wa Kisheria (NIDA)", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = DopNavyPrimary)
        Text("DoP Guide anawakilisha mfumo rasmi uwandani, utambulisho thabiti unahitajika", fontSize = 11.sp, color = DopTextSecondary)
        Spacer(modifier = Modifier.height(14.dp))
        Text("Namba ya NIDA (NIN - Tarakimu 20)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = nin,
            onValueChange = onNinChange,
            placeholder = { Text("19880412-11101-00004-21") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Surface(color = DopGuideAmberContainer, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Security, contentDescription = null, tint = DopGuideAmberDark)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Taarifa zako zitasalia salama na kutumika kwa ukaguzi wa ulinzi pekee.", fontSize = 10.sp, color = DopGuideAmberDark)
            }
        }
    }
}

@Composable
private fun GuideStepZones(selectedZones: Set<String>, onToggleZone: (String) -> Unit) {
    val zones = listOf("Kinondoni", "Sinza", "Mikocheni", "Masaki & Oysterbay", "Kijitonyama", "Ubungo", "Ilala", "Kigamboni")
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text("3. Kanda Unazozijua Vizuri (Service Zones)", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = DopNavyPrimary)
        Text("Chagua mitaa ambapo una wepesi wa kuelekeza na kufanya ziara", fontSize = 11.sp, color = DopTextSecondary)
        Spacer(modifier = Modifier.height(14.dp))
        zones.forEach { zone ->
            val isChecked = selectedZones.contains(zone)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onToggleZone(zone) }
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = isChecked, onCheckedChange = { onToggleZone(zone) })
                Spacer(modifier = Modifier.width(8.dp))
                Text(zone, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun GuideStepExperience(years: Int, onYearsChange: (Int) -> Unit) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text("4. Uzoefu wa Udalali / Eneo", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = DopNavyPrimary)
        Text("Je, una uzoefu wa miaka mingapi katika kuelekeza nyumba na viwanja?", fontSize = 11.sp, color = DopTextSecondary)
        Spacer(modifier = Modifier.height(16.dp))
        listOf(1, 2, 3, 5, 8).forEach { yr ->
            val isSelected = years == yr
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onYearsChange(yr) },
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) DopGuideAmberContainer else Color.White,
                border = BorderStroke(1.dp, if (isSelected) DopGuideAmber else DopBorderSubtle)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = isSelected, onClick = { onYearsChange(yr) })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (yr == 1) "Mwaka 1 au chini ya hapo" else "Zaidi ya miaka $yr", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun GuideStepTransport(mode: String, onModeChange: (String) -> Unit) {
    val modes = listOf("Pikipiki / Bodaboda", "Gari Binafsi", "Baiskeli", "Usafiri wa Umma (Daladala / Mwendokasi)")
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text("5. Njia Kuu ya Usafiri", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = DopNavyPrimary)
        Text("Usafiri unaotumia kuwahi miadi ya wateja kwa wakati", fontSize = 11.sp, color = DopTextSecondary)
        Spacer(modifier = Modifier.height(16.dp))
        modes.forEach { item ->
            val isSelected = mode == item
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onModeChange(item) },
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) DopGuideAmberContainer else Color.White,
                border = BorderStroke(1.dp, if (isSelected) DopGuideAmber else DopBorderSubtle)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = isSelected, onClick = { onModeChange(item) })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(item, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun GuideStepLanguages(languages: Set<String>, onToggleLanguage: (String) -> Unit) {
    val allLangs = listOf("Kiswahili", "Kiingereza", "Kifaransa", "Kiarabu")
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text("6. Lugha za Mawasiliano", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = DopNavyPrimary)
        Text("Lugha unazoweza kuwasiliana nazo kwa ufasaha", fontSize = 11.sp, color = DopTextSecondary)
        Spacer(modifier = Modifier.height(16.dp))
        allLangs.forEach { l ->
            val isChecked = languages.contains(l)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onToggleLanguage(l) }
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = isChecked, onCheckedChange = { onToggleLanguage(l) })
                Spacer(modifier = Modifier.width(8.dp))
                Text(l, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun GuideStepAvailability(avail: String, onAvailChange: (String) -> Unit) {
    val options = listOf("Muda Wote (Full Time)", "Mwisho wa Wiki Tu (Weekends)", "Muda wa Ziada / Jioni")
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text("7. Upatikanaji wa Kazi", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = DopNavyPrimary)
        Text("Muda unaopatikana kwa ajili ya kuongoza ziara za wateja", fontSize = 11.sp, color = DopTextSecondary)
        Spacer(modifier = Modifier.height(16.dp))
        options.forEach { opt ->
            val isSelected = avail == opt
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onAvailChange(opt) },
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) DopGuideAmberContainer else Color.White,
                border = BorderStroke(1.dp, if (isSelected) DopGuideAmber else DopBorderSubtle)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = isSelected, onClick = { onAvailChange(opt) })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(opt, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun GuideStepEmergency(name: String, onNameChange: (String) -> Unit, phone: String, onPhoneChange: (String) -> Unit) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text("8. Mawasiliano ya Dharura (Emergency)", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = DopNavyPrimary)
        Text("Mtu wa karibu (ndugu au mlezi) anayeweza kuwasiliana naye ikibidi", fontSize = 11.sp, color = DopTextSecondary)
        Spacer(modifier = Modifier.height(14.dp))
        Text("Jina la Ndugu / Mlezi", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(value = name, onValueChange = onNameChange, placeholder = { Text("k.m. Juma Salum Kimbau") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
        Spacer(modifier = Modifier.height(10.dp))
        Text("Namba ya Simu ya Dharura", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(value = phone, onValueChange = onPhoneChange, placeholder = { Text("0712 345 678") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
    }
}

@Composable
private fun GuideStepConduct(accepted: Boolean, onToggle: (Boolean) -> Unit) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text("9. Kanuni za Maadili ya DoP Guide", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = DopNavyPrimary)
        Text("Miongozo ya uadilifu na hadhi ya mfumo rasmi wa Dalalion Pocket", fontSize = 11.sp, color = DopTextSecondary)
        Spacer(modifier = Modifier.height(14.dp))
        Surface(color = Color.White, shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, DopGuideAmberBorder)) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("1. Hakuna Kutoza Malipo ya Pembeni (Zero Hidden Fees)", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DopNavyPrimary)
                Text("Malipo yote ya ziara yanapitia M-Pesa / Tigo Pesa ya DoP. Dalali haruhusiwi kuomba pesa taslimu mtaani.", fontSize = 10.sp, color = DopTextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                Text("2. Picha na Maelezo Sahihi (Zero Distortion)", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DopNavyPrimary)
                Text("Nyumba inaonyeshwa vile ilivyo halisi bila kupotosha hali ya mtaa au umeme/maji.", fontSize = 10.sp, color = DopTextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                Text("3. Nidhamu na Ulinzi wa Wateja", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DopNavyPrimary)
                Text("Kufika kwa wakati kwenye eneo la makutano lililothibitishwa.", fontSize = 10.sp, color = DopTextSecondary)
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = accepted, onCheckedChange = onToggle)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Ninakubali na nitaheshimu Kanuni zote za Maadili ya Udalali ya DoP.", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun GuideStepReview(
    fullName: String,
    phone: String,
    nationalIdNin: String,
    operatingZones: Set<String>,
    experienceYears: Int,
    transportMode: String,
    availability: String
) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text("10. Mapitio ya Maombi ya Udalali", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = DopNavyPrimary)
        Text("Kagua taarifa kabla ya kuziwasilisha kwa timu ya usimamizi", fontSize = 11.sp, color = DopTextSecondary)
        Spacer(modifier = Modifier.height(14.dp))
        DopBentoCard {
            Text("Taarifa za Mwombaji", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DopGuideAmberDark)
            Spacer(modifier = Modifier.height(6.dp))
            Text("• Jina: $fullName", fontSize = 11.sp)
            Text("• Simu: $phone", fontSize = 11.sp)
            Text("• NIDA: ${nationalIdNin.ifBlank { "Imewasilishwa" }}", fontSize = 11.sp)
            Text("• Kanda: ${operatingZones.joinToString(", ")}", fontSize = 11.sp)
            Text("• Uzoefu: Miaka $experienceYears", fontSize = 11.sp)
            Text("• Usafiri: $transportMode", fontSize = 11.sp)
            Text("• Upatikanaji: $availability", fontSize = 11.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Surface(color = DopGuideAmberContainer, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.HourglassTop, contentDescription = null, tint = DopGuideAmberDark)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Baada ya kutuma, maombi yako yataingia kwenye mfumo wa ukaguzi (Pending Admin Review). Utaarifiwa pindi maombi yatakapoidhinishwa.",
                    fontSize = 11.sp,
                    color = DopGuideAmberDark,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

// ============================================================
// 3. GUIDE UNDER REVIEW & SIMULATION SCREEN
// ============================================================
@Composable
fun GuidePendingReviewCard(
    application: GuideApplication?,
    onSimulateApprove: () -> Unit,
    onSimulateReject: () -> Unit
) {
    DopBentoCard(
        backgroundColor = DopGuideAmberContainer,
        borderColor = DopGuideAmber
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.HourglassBottom, contentDescription = null, tint = DopGuideAmberDark)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Maombi ya DoP Guide Yanapitiwa", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DopGuideAmberDark)
                }
                DopBadge("UNDER REVIEW", Icons.Default.Pending, DopGuideAmberDark, Color.White)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Timu ya Uendeshaji na Ulinzi ya Dalalion Pocket inafanya ukaguzi wa vitambulisho, maeneo na kanuni za maadili. Kazi za uwandani na dashibodi ya Guide zitafunguliwa mara tu maombi yatakapoidhinishwa na Admin.",
                fontSize = 11.sp,
                color = DopTextPrimary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Admin Approval Simulation Box for testing & CUJ verification
            Surface(
                color = Color.White.copy(alpha = 0.85f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(0.5.dp, DopGuideAmber)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "🛠️ Mfumo wa Uidhinishaji (Admin Review Simulation):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = DopNavyPrimary
                    )
                    Text(
                        text = "Kama msimamizi au mpimaji, unaweza kujaribu mabadiliko ya idhini:",
                        fontSize = 10.sp,
                        color = DopTextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = onSimulateApprove,
                            colors = ButtonDefaults.buttonColors(containerColor = DopTrustGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("simulate_admin_approve_btn")
                        ) {
                            Text("✅ Idhinisha (Approve)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = onSimulateReject,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("simulate_admin_reject_btn")
                        ) {
                            Text("❌ Kataa (Reject)", fontSize = 11.sp, color = DopError)
                        }
                    }
                }
            }
        }
    }
}

// ============================================================
// 4. CAPABILITY-AWARE ROLE SWITCHER DIALOG
// ============================================================
@Composable
fun CapabilityRoleSwitcherDialog(
    currentUser: UserSession,
    onDismiss: () -> Unit,
    onSelectMode: (UserRole) -> Unit,
    onOpenOwnerOnboarding: () -> Unit,
    onOpenGuideApplication: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = DopNavyPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Badilisha Mwonekano (Active Mode)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Akaunti yako inaweza kushikilia uwezo tofauti kwa wakati mmoja. Chagua hali unayotaka kutumia sasa:",
                    fontSize = 11.sp,
                    color = DopTextSecondary
                )

                // 1. CUSTOMER MODE (Always active for every user)
                val isCustomerSelected = currentUser.activeMode == UserRole.CUSTOMER
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isCustomerSelected) DopCustomerBlueContainer else Color.White,
                    border = BorderStroke(if (isCustomerSelected) 2.dp else 1.dp, if (isCustomerSelected) DopCustomerBlue else DopBorderSubtle),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelectMode(UserRole.CUSTOMER)
                            onDismiss()
                        }
                        .testTag("mode_switch_customer")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = isCustomerSelected, onClick = {
                            onSelectMode(UserRole.CUSTOMER)
                            onDismiss()
                        })
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Mwonekano wa Mteja (Customer)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DopCustomerBlueDark)
                                Spacer(modifier = Modifier.width(6.dp))
                                DopBadge("ACTIVE", Icons.Default.Check, DopCustomerBlue, DopCustomerBlueContainer)
                            }
                            Text("Kutafuta nyumba, kuhifadhi na kupanga ziara", fontSize = 10.sp, color = DopTextSecondary)
                        }
                    }
                }

                // 2. OWNER MODE
                val isOwnerActive = currentUser.canAccessOwner
                val isOwnerSelected = currentUser.activeMode == UserRole.OWNER
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isOwnerSelected) DopOwnerGreenContainer else Color.White,
                    border = BorderStroke(if (isOwnerSelected) 2.dp else 1.dp, if (isOwnerSelected) DopOwnerGreen else DopBorderSubtle),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isOwnerActive) {
                                onSelectMode(UserRole.OWNER)
                                onDismiss()
                            } else {
                                onDismiss()
                                onOpenOwnerOnboarding()
                            }
                        }
                        .testTag("mode_switch_owner")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isOwnerActive) {
                            RadioButton(selected = isOwnerSelected, onClick = {
                                onSelectMode(UserRole.OWNER)
                                onDismiss()
                            })
                        } else {
                            Icon(Icons.Default.AddHomeWork, contentDescription = null, tint = DopOwnerGreen, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Mwonekano wa Mmiliki (Owner)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DopOwnerGreenDark)
                                Spacer(modifier = Modifier.width(6.dp))
                                if (isOwnerActive) {
                                    DopBadge("ACTIVE", Icons.Default.Check, DopOwnerGreen, DopOwnerGreenContainer)
                                } else {
                                    DopBadge("HAIJAWASHWA", null, DopTextMuted, DopNeutralPearl)
                                }
                            }
                            Text(
                                text = if (isOwnerActive) "Kuweka na kusimamia nyumba zako" else "Bofya hapa kuwasha uwezo wa mmiliki bila kufungua akaunti mpya",
                                fontSize = 10.sp,
                                color = if (isOwnerActive) DopTextSecondary else DopOwnerGreen
                            )
                        }
                    }
                }

                // 3. GUIDE MODE
                val isGuideApproved = currentUser.canAccessGuide
                val isGuideSelected = currentUser.activeMode == UserRole.GUIDE
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isGuideSelected) DopGuideAmberContainer else Color.White,
                    border = BorderStroke(if (isGuideSelected) 2.dp else 1.dp, if (isGuideSelected) DopGuideAmber else DopBorderSubtle),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isGuideApproved) {
                                onSelectMode(UserRole.GUIDE)
                                onDismiss()
                            } else if (currentUser.guideCapability == GuideCapabilityStatus.NOT_APPLIED) {
                                onDismiss()
                                onOpenGuideApplication()
                            }
                        }
                        .testTag("mode_switch_guide")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isGuideApproved) {
                            RadioButton(selected = isGuideSelected, onClick = {
                                onSelectMode(UserRole.GUIDE)
                                onDismiss()
                            })
                        } else {
                            Icon(Icons.Default.DirectionsWalk, contentDescription = null, tint = DopGuideAmber, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Mwonekano wa Dalali (DoP Guide)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DopGuideAmberDark)
                                Spacer(modifier = Modifier.width(6.dp))
                                when (currentUser.guideCapability) {
                                    GuideCapabilityStatus.APPROVED -> DopBadge("APPROVED", Icons.Default.Check, DopGuideAmberDark, DopGuideAmberContainer)
                                    GuideCapabilityStatus.SUBMITTED -> DopBadge("UNDER REVIEW", Icons.Default.HourglassTop, DopGuideAmberDark, DopGuideAmberContainer)
                                    GuideCapabilityStatus.REJECTED -> DopBadge("REJECTED", Icons.Default.Close, DopError, DopErrorContainer)
                                    GuideCapabilityStatus.SUSPENDED -> DopBadge("SUSPENDED", Icons.Default.Block, DopError, DopErrorContainer)
                                    else -> DopBadge("OMBA SASA", null, DopGuideAmberDark, DopGuideAmberContainer)
                                }
                            }
                            Text(
                                text = when (currentUser.guideCapability) {
                                    GuideCapabilityStatus.APPROVED -> "Kazi za ziara, ratiba na mapato ya udalali"
                                    GuideCapabilityStatus.SUBMITTED -> "Maombi yako yanakaguliwa na Admin"
                                    GuideCapabilityStatus.SUSPENDED -> "Uwezo umesimamishwa kwa muda"
                                    else -> "Tuma maombi ya kuwa DoP Guide rasmi wa mtaa"
                                },
                                fontSize = 10.sp,
                                color = DopTextSecondary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Funga", color = DopNavyPrimary)
            }
        }
    )
}
