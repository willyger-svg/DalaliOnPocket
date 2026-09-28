package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.core.localization.DoPStrings
import com.example.core.market.MarketConfig
import com.example.core.market.Region
import com.example.data.model.*
import com.example.data.repository.PropertyRepository
import com.example.ui.components.DopBadge
import com.example.ui.components.DopBentoCard
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Complete 13-Step Owner Property Creation Studio.
 * Includes Draft Autosave, Property Media Studio, Private Verification Documents,
 * Dynamic Classification, and 50% First Month Commission Rule.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerPropertyStudioScreen(
    initialDraft: PropertyDraft? = null,
    onDismiss: () -> Unit,
    onFinished: (Property) -> Unit
) {
    val context = LocalContext.current
    val savedDraftFlow by PropertyRepository.ownerDraft.collectAsState()

    // Initialize draft from existing repository draft, or provided draft, or blank
    var draft by remember {
        mutableStateOf(
            initialDraft ?: savedDraftFlow ?: PropertyDraft()
        )
    }

    var showDiscardConfirm by remember { mutableStateOf(false) }
    var autosaveNotice by remember { mutableStateOf("Imehifadhiwa kiotomatiki") }

    // Auto-save on draft changes
    LaunchedEffect(draft) {
        PropertyRepository.saveDraft(draft)
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        autosaveNotice = "Imehifadhiwa saa ${sdf.format(Date(draft.lastSavedTimestamp))}"
    }

    val totalSteps = 13
    val step = draft.currentStep

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Studio ya Mali (Hatua $step/$totalSteps)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = DopOchre
                            ) {
                                Text(
                                    text = "${draft.completenessScore}%",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = DopNavyPrimary,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = autosaveNotice,
                            fontSize = 10.sp,
                            color = DopOchreLight
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("studio_close_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                },
                actions = {
                    TextButton(
                        onClick = { showDiscardConfirm = true },
                        modifier = Modifier.testTag("studio_discard_btn")
                    ) {
                        Text("Tupa Rasimu", color = DopError.copy(alpha = 0.9f), fontSize = 11.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DopNavyPrimary)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(DopNeutralPearl)
        ) {
            // Linear Progress Indicator
            LinearProgressIndicator(
                progress = { step / totalSteps.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp),
                color = DopOchre,
                trackColor = DopBorderSubtle
            )

            // Step Content Scrollable Body
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    when (step) {
                        1 -> StudioStep1Classification(
                            category = draft.category,
                            propertyType = draft.propertyType,
                            onUpdate = { cat, pt -> draft = draft.copy(category = cat, propertyType = pt) }
                        )
                        2 -> StudioStep2Transaction(
                            transactionType = draft.transactionType,
                            onSelect = { draft = draft.copy(transactionType = it) }
                        )
                        3 -> StudioStep3Basics(
                            draft = draft,
                            onUpdate = { draft = it }
                        )
                        4 -> StudioStep4DynamicFeatures(
                            draft = draft,
                            onUpdate = { draft = it }
                        )
                        5 -> StudioStep5Amenities(
                            amenities = draft.amenities,
                            customAmenities = draft.customAmenities,
                            onUpdateAmenities = { updated, custom ->
                                draft = draft.copy(amenities = updated, customAmenities = custom)
                            }
                        )
                        6 -> StudioStep6LocationPrivacy(
                            draft = draft,
                            onUpdate = { draft = it }
                        )
                        7 -> StudioStep7MediaStudio(
                            draft = draft,
                            onUpdate = { draft = it }
                        )
                        8 -> StudioStep8VerificationDocuments(
                            documents = draft.verificationDocuments,
                            onUpdate = { draft = draft.copy(verificationDocuments = it) }
                        )
                        9 -> StudioStep9PricingAndCommission(
                            draft = draft,
                            onUpdate = { draft = it }
                        )
                        10 -> StudioStep10Availability(
                            availability = draft.availabilityStatus,
                            moveInDate = draft.availableFromDate,
                            onUpdate = { avail, date ->
                                draft = draft.copy(availabilityStatus = avail, availableFromDate = date)
                            }
                        )
                        11 -> StudioStep11ViewingAndCaretaker(
                            draft = draft,
                            onUpdate = { draft = it }
                        )
                        12 -> StudioStep12VirtualViewingSettings(
                            draft = draft,
                            onUpdate = { draft = it }
                        )
                        13 -> StudioStep13ReviewAndSubmit(
                            draft = draft,
                            onSubmit = {
                                val newProp = PropertyRepository.submitDraft(draft)
                                Toast.makeText(context, "Mali yako imewasilishwa kwa ukaguzi wa DoP!", Toast.LENGTH_LONG).show()
                                onFinished(newProp)
                            }
                        )
                    }
                }
            }

            // Bottom Navigation Controls
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (step > 1) {
                        OutlinedButton(
                            onClick = { draft = draft.copy(currentStep = step - 1) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("studio_prev_step_btn")
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Hatua Iliyopita")
                        }
                    } else {
                        OutlinedButton(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Hifadhi na Uondoke")
                        }
                    }

                    if (step < totalSteps) {
                        Button(
                            onClick = { draft = draft.copy(currentStep = step + 1) },
                            colors = ButtonDefaults.buttonColors(containerColor = DopNavyPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("studio_next_step_btn")
                        ) {
                            Text("Endelea / Next")
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    } else {
                        Button(
                            onClick = {
                                val newProp = PropertyRepository.submitDraft(draft)
                                Toast.makeText(context, "Mali yako imewasilishwa kwa ukaguzi wa DoP!", Toast.LENGTH_LONG).show()
                                onFinished(newProp)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DopTrustGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("studio_final_submit_btn")
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Wasilisha Tangazo (Submit)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Discard Draft Dialog
    if (showDiscardConfirm) {
        AlertDialog(
            onDismissRequest = { showDiscardConfirm = false },
            title = { Text("Tupa Rasimu Hii?", fontWeight = FontWeight.Bold) },
            text = { Text("Mabadiliko yote uliyofanya kwenye mali hii yatafutwa kabisa.") },
            confirmButton = {
                Button(
                    onClick = {
                        PropertyRepository.discardDraft()
                        showDiscardConfirm = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DopError)
                ) {
                    Text("Ndio, Futa Rasimu")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardConfirm = false }) {
                    Text("Ghairi")
                }
            }
        )
    }
}

// ----------------------------------------------------------------------------
// STEP 1: CATEGORY & CLASSIFICATION
// ----------------------------------------------------------------------------
@Composable
fun StudioStep1Classification(
    category: PropertyCategory,
    propertyType: PropertyType,
    onUpdate: (PropertyCategory, PropertyType) -> Unit
) {
    DopBentoCard {
        Text("Hatua 1: Aina Kuu ya Mali (Category & Type)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DopNavyPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Chagua kundi kuu la mali unayotaka kuorodhesha kwenye DoP Tanzania.", fontSize = 12.sp, color = DopTextSecondary)

        Spacer(modifier = Modifier.height(14.dp))

        // Categories
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PropertyCategory.entries.forEach { cat ->
                val isSelected = cat == category
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) DopNavyPrimary else DopNeutralPearl,
                    border = BorderStroke(1.dp, if (isSelected) DopNavyPrimary else DopBorderSubtle),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            val defaultType = when (cat) {
                                PropertyCategory.RESIDENTIAL -> PropertyType.APARTMENT
                                PropertyCategory.COMMERCIAL -> PropertyType.SHOP
                                PropertyCategory.LAND -> PropertyType.RESIDENTIAL_LAND
                            }
                            onUpdate(cat, defaultType)
                        }
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = when (cat) {
                                PropertyCategory.RESIDENTIAL -> Icons.Default.Home
                                PropertyCategory.COMMERCIAL -> Icons.Default.Storefront
                                PropertyCategory.LAND -> Icons.Default.Landscape
                            },
                            contentDescription = null,
                            tint = if (isSelected) DopOchreLight else DopNavyPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = cat.name.take(4),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else DopNavyPrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = DopBorderSubtle)
        Spacer(modifier = Modifier.height(14.dp))

        Text("Aina Mahsusi ya ${category.name}:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DopNavyPrimary)
        Spacer(modifier = Modifier.height(8.dp))

        val subTypes = PropertyType.entries.filter { it.category == category }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            subTypes.forEach { pt ->
                val isChosen = pt == propertyType
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isChosen) DopOchreContainer else Color.White,
                    border = BorderStroke(1.5.dp, if (isChosen) DopOchre else DopBorderSubtle.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onUpdate(category, pt) }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isChosen,
                            onClick = { onUpdate(category, pt) },
                            colors = RadioButtonDefaults.colors(selectedColor = DopOchre)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(pt.titleSw, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(pt.titleEn, fontSize = 11.sp, color = DopTextSecondary)
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------
// STEP 2: TRANSACTION TYPE
// ----------------------------------------------------------------------------
@Composable
fun StudioStep2Transaction(
    transactionType: TransactionType,
    onSelect: (TransactionType) -> Unit
) {
    DopBentoCard {
        Text("Hatua 2: Aina ya Muamala (Transaction Type)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DopNavyPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Unataka kupangisha au kuuza mali hii?", fontSize = 12.sp, color = DopTextSecondary)

        Spacer(modifier = Modifier.height(16.dp))

        TransactionType.entries.forEach { type ->
            val isSelected = transactionType == type
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) DopOchreContainer else Color.White,
                border = BorderStroke(1.5.dp, if (isSelected) DopOchre else DopBorderSubtle),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clickable { onSelect(type) }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onSelect(type) },
                        colors = RadioButtonDefaults.colors(selectedColor = DopOchre)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(type.titleSw, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(type.titleEn, fontSize = 11.sp, color = DopTextSecondary)
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------
// STEP 3: PROPERTY BASICS
// ----------------------------------------------------------------------------
@Composable
fun StudioStep3Basics(
    draft: PropertyDraft,
    onUpdate: (PropertyDraft) -> Unit
) {
    DopBentoCard {
        Text("Hatua 3: Taarifa za Msingi (Property Basics)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DopNavyPrimary)
        Spacer(modifier = Modifier.height(12.dp))

        // Title
        Text("Kichwa cha Tangazo (Property Title) *", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = draft.title,
            onValueChange = { onUpdate(draft.copy(title = it)) },
            placeholder = { Text("Mf. Masaki Sunset Executive 3-Bedroom Apartment") },
            modifier = Modifier.fillMaxWidth().testTag("studio_input_title"),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tagline
        Text("Muhtasari wa Kuvutia (Tagline / Hook)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = draft.tagline,
            onValueChange = { onUpdate(draft.copy(tagline = it)) },
            placeholder = { Text("Mf. Mtazamo wa Bahari, Balcony Kubwa, AC Kila Chumba") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Description
        Text("Maelezo ya Kina (Detailed Description) *", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = draft.description,
            onValueChange = { onUpdate(draft.copy(description = it)) },
            placeholder = { Text("Eleza mazingira, ubora wa maji, umeme wa LUKU, usalama wa uzio...") },
            modifier = Modifier.fillMaxWidth().height(110.dp).testTag("studio_input_desc"),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Condition & Furnished
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Hali ya Nyumba", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = draft.propertyCondition,
                    onValueChange = { onUpdate(draft.copy(propertyCondition = it)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Samani (Furnished)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = draft.furnishedStatus,
                    onValueChange = { onUpdate(draft.copy(furnishedStatus = it)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Size SQM
        Text("Ukubwa wa Eneo (Area SQM)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = draft.areaSqm.toString(),
            onValueChange = { onUpdate(draft.copy(areaSqm = it.toIntOrNull() ?: 100)) },
            trailingIcon = { Text("sqm", fontSize = 11.sp, color = DopTextSecondary, modifier = Modifier.padding(end = 8.dp)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )
    }
}

// ----------------------------------------------------------------------------
// STEP 4: DYNAMIC ROOMS & FEATURES
// ----------------------------------------------------------------------------
@Composable
fun StudioStep4DynamicFeatures(
    draft: PropertyDraft,
    onUpdate: (PropertyDraft) -> Unit
) {
    DopBentoCard {
        Text("Hatua 4: Vipengele Kulingana na Mali (${draft.category.name})", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DopNavyPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Maswali haya yanabadilika kutegemeana na kama ni makazi, biashara au ardhi.", fontSize = 11.sp, color = DopTextSecondary)

        Spacer(modifier = Modifier.height(16.dp))

        when (draft.category) {
            PropertyCategory.RESIDENTIAL -> {
                CounterRow("Vyumba vya Kulala (Bedrooms)", draft.bedrooms) { onUpdate(draft.copy(bedrooms = it)) }
                Spacer(modifier = Modifier.height(10.dp))
                CounterRow("Vyoo / Bafu (Bathrooms)", draft.bathrooms) { onUpdate(draft.copy(bathrooms = it)) }
                Spacer(modifier = Modifier.height(10.dp))
                CounterRow("Sebule (Living Rooms)", draft.livingRooms) { onUpdate(draft.copy(livingRooms = it)) }
                Spacer(modifier = Modifier.height(10.dp))
                CounterRow("Jiko (Kitchens)", draft.kitchens) { onUpdate(draft.copy(kitchens = it)) }
                Spacer(modifier = Modifier.height(10.dp))
                CounterRow("Nafasi za Magari (Parking)", draft.parkingSpaces) { onUpdate(draft.copy(parkingSpaces = it)) }
            }
            PropertyCategory.COMMERCIAL -> {
                CounterRow("Idadi ya Nafasi / Vyumba vya Ofisi", draft.bedrooms) { onUpdate(draft.copy(bedrooms = it)) }
                Spacer(modifier = Modifier.height(10.dp))
                CounterRow("Vyoo vya Wafanyakazi / Wateja", draft.bathrooms) { onUpdate(draft.copy(bathrooms = it)) }
                Spacer(modifier = Modifier.height(10.dp))
                CounterRow("Nafasi za Maegesho ya Magari", draft.parkingSpaces) { onUpdate(draft.copy(parkingSpaces = it)) }
            }
            PropertyCategory.LAND -> {
                Text("Hali ya Barabara ya Kufika (Road Access):", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = draft.roadAccess,
                    onValueChange = { onUpdate(draft.copy(roadAccess = it)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text("Mandhari ya Kiwanja (Topography):", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = draft.topography,
                    onValueChange = { onUpdate(draft.copy(topography = it)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = draft.isFenced, onCheckedChange = { onUpdate(draft.copy(isFenced = it)) })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Kiwanja kimezungushiwa uzio (Wall / Fence)", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun CounterRow(label: String, count: Int, onValueChange: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { if (count > 0) onValueChange(count - 1) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Default.RemoveCircleOutline, contentDescription = null, tint = DopNavyPrimary)
            }
            Text("$count", fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(horizontal = 8.dp))
            IconButton(
                onClick = { onValueChange(count + 1) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = DopOchre)
            }
        }
    }
}

// ----------------------------------------------------------------------------
// STEP 5: AMENITIES & UTILITIES
// ----------------------------------------------------------------------------
@Composable
fun StudioStep5Amenities(
    amenities: List<String>,
    customAmenities: List<String>,
    onUpdateAmenities: (List<String>, List<String>) -> Unit
) {
    var newCustomInput by remember { mutableStateOf("") }

    val presetAmenities = listOf(
        "Maji ya DAWASA 24/7",
        "LUKU ya Kujitegemea",
        "Uzio na Geti Salama",
        "Air Conditioning (AC)",
        "Standby Generator",
        "Ulinzi wa Masaa 24 (Security Guard)",
        "Maegesho ya Magari (Paved Parking)",
        "Mtandao wa WiFi Ready",
        "Swimming Pool",
        "Bustani ya Kupumzika (Garden / Lawn)"
    )

    DopBentoCard {
        Text("Hatua 5: Huduma za Msingi (Amenities & Utilities)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DopNavyPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Weka alama kwenye huduma zinazopatikana kwenye nyumba hii.", fontSize = 12.sp, color = DopTextSecondary)

        Spacer(modifier = Modifier.height(14.dp))

        presetAmenities.forEach { item ->
            val isChecked = amenities.contains(item)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val updated = if (isChecked) amenities - item else amenities + item
                        onUpdateAmenities(updated, customAmenities)
                    }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { checked ->
                        val updated = if (checked) amenities + item else amenities - item
                        onUpdateAmenities(updated, customAmenities)
                    },
                    colors = CheckboxDefaults.colors(checkedColor = DopOchre)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(item, fontSize = 13.sp)
            }
        }

        if (customAmenities.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text("Huduma Zilizoongezwa:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                customAmenities.forEach { custom ->
                    InputChip(
                        selected = true,
                        onClick = { onUpdateAmenities(amenities, customAmenities - custom) },
                        label = { Text(custom, fontSize = 11.sp) },
                        trailingIcon = { Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp)) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Add custom amenity
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newCustomInput,
                onValueChange = { newCustomInput = it },
                placeholder = { Text("Ongeza huduma nyingine...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (newCustomInput.isNotBlank()) {
                        onUpdateAmenities(amenities, customAmenities + newCustomInput.trim())
                        newCustomInput = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DopNavyPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("+")
            }
        }
    }
}

// ----------------------------------------------------------------------------
// STEP 6: LOCATION & PRIVACY
// ----------------------------------------------------------------------------
@Composable
fun StudioStep6LocationPrivacy(
    draft: PropertyDraft,
    onUpdate: (PropertyDraft) -> Unit
) {
    DopBentoCard {
        Text("Hatua 6: Mahali Mali Ilipo na Faragha (Location & Privacy)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DopNavyPrimary)
        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Mkoa (Region) *", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = draft.region.ifEmpty { "Dar es Salaam" },
                    onValueChange = { onUpdate(draft.copy(region = it)) },
                    modifier = Modifier.fillMaxWidth().testTag("studio_input_region"),
                    shape = RoundedCornerShape(10.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Wilaya (District) *", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = draft.district,
                    onValueChange = { onUpdate(draft.copy(district = it)) },
                    modifier = Modifier.fillMaxWidth().testTag("studio_input_district"),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Kata (Ward) *", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = draft.ward,
                    onValueChange = { onUpdate(draft.copy(ward = it)) },
                    modifier = Modifier.fillMaxWidth().testTag("studio_input_ward"),
                    shape = RoundedCornerShape(10.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Mtaa (Street)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = draft.street,
                    onValueChange = { onUpdate(draft.copy(street = it)) },
                    placeholder = { Text("Mf. Chole Road") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text("Alama Kuu ya Jirani (Landmark)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = draft.landmark,
            onValueChange = { onUpdate(draft.copy(landmark = it)) },
            placeholder = { Text("Mf. Nyuma ya Shoppers Plaza, Mita 100 kutoka lami") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = DopBorderSubtle)
        Spacer(modifier = Modifier.height(14.dp))

        Text("Mipangilio ya Faragha ya Mahali (Location Privacy):", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DopNavyPrimary)
        Spacer(modifier = Modifier.height(8.dp))

        LocationPrivacy.entries.forEach { lp ->
            val isSelected = draft.locationPrivacy == lp
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) DopOchreContainer else Color.White,
                border = BorderStroke(1.5.dp, if (isSelected) DopOchre else DopBorderSubtle.copy(alpha = 0.6f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onUpdate(draft.copy(locationPrivacy = lp)) }
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onUpdate(draft.copy(locationPrivacy = lp)) },
                        colors = RadioButtonDefaults.colors(selectedColor = DopOchre)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = when (lp) {
                                LocationPrivacy.APPROXIMATE -> "Approximate (Inalindwa ndani ya mita 500)"
                                LocationPrivacy.EXACT -> "Exact (Anwani Kamili na Ramani ya Moja kwa Moja)"
                                LocationPrivacy.HIDDEN -> "Hidden (Wilaya na Kata Pekee hadi Mteja Athibitishe)"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------
// STEP 7: DEDICATED PROPERTY MEDIA STUDIO
// ----------------------------------------------------------------------------
@Composable
fun StudioStep7MediaStudio(
    draft: PropertyDraft,
    onUpdate: (PropertyDraft) -> Unit
) {
    val context = LocalContext.current

    val samplePhotos = listOf(
        Pair("https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?auto=format&fit=crop&w=800&q=80", "Sebule & Balcony"),
        Pair("https://images.unsplash.com/photo-1512917774080-9991f1c4c750?auto=format&fit=crop&w=800&q=80", "Master Bedroom"),
        Pair("https://images.unsplash.com/photo-1556911220-e15b29be8c8f?auto=format&fit=crop&w=800&q=80", "Jiko la Kisasa"),
        Pair("https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=800&q=80", "Nje & Parking Compound")
    )

    DopBentoCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Hatua 7: Studio ya Picha na Video", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DopNavyPrimary)
                Text("${draft.mediaItems.size} Media Zimepakiwa • 1080p HD", fontSize = 11.sp, color = DopTrustGreen)
            }
            DopBadge("MEDIA STUDIO", Icons.Default.CameraAlt, DopOchre, DopNavyElevated)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Action: Add Sample High-Res Photo
        Button(
            onClick = {
                val nextSample = samplePhotos[(draft.mediaItems.size) % samplePhotos.size]
                val newItem = PropertyMediaItem(
                    remoteUrl = nextSample.first,
                    caption = nextSample.second,
                    isCover = draft.mediaItems.none { it.isCover }
                )
                onUpdate(draft.copy(mediaItems = draft.mediaItems + newItem))
                Toast.makeText(context, "Picha ya '${nextSample.second}' imeongezwa!", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = DopOchre),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().testTag("studio_add_photo_btn")
        ) {
            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = DopNavyPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Ongeza Picha ya Ubora wa Juu (HD Photo)", color = DopNavyPrimary, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Gallery List
        if (draft.mediaItems.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(draft.mediaItems) { item ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(if (item.isCover) 2.dp else 1.dp, if (item.isCover) DopOchre else DopBorderSubtle),
                        modifier = Modifier.width(160.dp)
                    ) {
                        Column {
                            Box(modifier = Modifier.fillMaxWidth().height(100.dp)) {
                                AsyncImage(
                                    model = item.remoteUrl,
                                    contentDescription = item.caption,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                if (item.isCover) {
                                    Surface(
                                        shape = RoundedCornerShape(bottomEnd = 8.dp),
                                        color = DopOchre,
                                        modifier = Modifier.align(Alignment.TopStart)
                                    ) {
                                        Text("Picha Kuu", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = DopNavyPrimary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                            }
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(item.caption.ifBlank { "Picha ya Nyumba" }, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    if (!item.isCover) {
                                        Text(
                                            text = "Weka Kuu",
                                            fontSize = 10.sp,
                                            color = DopOchre,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.clickable {
                                                val updated = draft.mediaItems.map { it.copy(isCover = it.id == item.id) }
                                                onUpdate(draft.copy(mediaItems = updated))
                                            }
                                        )
                                    } else {
                                        Text("Kuu", fontSize = 10.sp, color = DopTrustGreen, fontWeight = FontWeight.Bold)
                                    }
                                    Text(
                                        text = "Futa",
                                        fontSize = 10.sp,
                                        color = DopError,
                                        modifier = Modifier.clickable {
                                            val updated = draft.mediaItems.filter { it.id != item.id }
                                            onUpdate(draft.copy(mediaItems = updated))
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = DopBorderSubtle)
        Spacer(modifier = Modifier.height(14.dp))

        // Virtual Tour Room Recorder Section
        Text("DoP Guided Virtual Tour Walkthrough:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DopNavyPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Rekodi ziara ya vyumba kwa utaratibu wa mwongozo wa DoP.", fontSize = 11.sp, color = DopTextSecondary)

        Spacer(modifier = Modifier.height(8.dp))

        val defaultTourRooms = listOf("Sebule & Corridor", "Jiko & Dining", "Master Bedroom", "Uani & Balcony")
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            defaultTourRooms.forEach { room ->
                val hasSection = draft.virtualTourSections.any { it.roomName == room }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (hasSection) DopTrustGreen.copy(alpha = 0.1f) else DopNeutralPearl,
                    border = BorderStroke(1.dp, if (hasSection) DopTrustGreen else DopBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (hasSection) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (hasSection) DopTrustGreen else DopTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(room, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        TextButton(
                            onClick = {
                                if (hasSection) {
                                    val updated = draft.virtualTourSections.filter { it.roomName != room }
                                    onUpdate(draft.copy(virtualTourSections = updated))
                                } else {
                                    val newSec = VirtualTourSection(
                                        roomName = room,
                                        mediaUrl = "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?auto=format&fit=crop&w=800&q=80",
                                        durationSeconds = 15
                                    )
                                    onUpdate(draft.copy(virtualTourSections = draft.virtualTourSections + newSec))
                                }
                            }
                        ) {
                            Text(if (hasSection) "Ondoa" else "+ Rekodi", fontSize = 11.sp, color = if (hasSection) DopError else DopNavyPrimary)
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------
// STEP 8: PRIVATE VERIFICATION DOCUMENTS
// ----------------------------------------------------------------------------
@Composable
fun StudioStep8VerificationDocuments(
    documents: List<PropertyVerificationDocument>,
    onUpdate: (List<PropertyVerificationDocument>) -> Unit
) {
    val context = LocalContext.current

    val requiredDocs = listOf(
        "Hati ya Ardhi / Barua ya Ofa (Title Deed / Offer Letter)",
        "Barua ya Serikali ya Mtaa (Local Gov Chairperson Letter)",
        "Kitambulisho cha Taifa NIDA cha Mwenye Mali (National ID)",
        "Mkataba wa Uwakilishi wa Dalali (Broker Authorization - kama unamsimamia)"
    )

    DopBentoCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Hatua 8: Nyaraka za Siri za Uhakiki", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DopNavyPrimary)
                Text("Hutumiwa na wanasheria wa DoP tu • Hazionekani hadharani", fontSize = 11.sp, color = DopTrustGreen)
            }
            DopBadge("STRICTLY PRIVATE", Icons.Default.Lock, DopTrustGreenLight, DopNavyElevated)
        }

        Spacer(modifier = Modifier.height(14.dp))

        requiredDocs.forEach { docType ->
            val docNamePrefix = docType.take(20)
            val existing = documents.find { it.documentType.startsWith(docNamePrefix) }
            val isUploaded = existing != null

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isUploaded) DopTrustGreen.copy(alpha = 0.08f) else Color.White,
                border = BorderStroke(1.dp, if (isUploaded) DopTrustGreen else DopBorderSubtle),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(docType, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(
                            text = if (isUploaded) "✓ Nyaraka imepakiwa (PDF/Image)" else "Bado haijapakiwa",
                            fontSize = 10.sp,
                            color = if (isUploaded) DopTrustGreen else DopTextSecondary
                        )
                    }

                    if (isUploaded) {
                        TextButton(
                            onClick = { onUpdate(documents.filter { it.id != existing?.id }) }
                        ) {
                            Text("Ondoa", color = DopError, fontSize = 11.sp)
                        }
                    } else {
                        Button(
                            onClick = {
                                val newDoc = PropertyVerificationDocument(
                                    documentType = docType,
                                    documentName = "${docNamePrefix.trim()}_SCAN.pdf",
                                    fileUri = "content://dop/docs/${UUID.randomUUID()}"
                                )
                                onUpdate(documents + newDoc)
                                Toast.makeText(context, "Nyaraka ya '$docNamePrefix' imepakiwa!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DopNavyPrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("Pakia / Upload", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------
// STEP 9: PRICING & 50% COMMISSION RULE
// ----------------------------------------------------------------------------
@Composable
fun StudioStep9PricingAndCommission(
    draft: PropertyDraft,
    onUpdate: (PropertyDraft) -> Unit
) {
    val firstMonthRent = draft.priceTzs
    val dopFee = firstMonthRent / 2 // Strictly 50% first month rent

    DopBentoCard {
        Text("Hatua 9: Bei na Sheria ya Ada ya DoP (Pricing & Fee)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DopNavyPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Hakuna ada inayolipwa sasa hivi. Ada inalipwa baada ya mpangaji kusaini mkataba.", fontSize = 11.sp, color = DopTextSecondary)

        Spacer(modifier = Modifier.height(14.dp))

        // Price Input
        Text("Bei ya Kodi kwa Mwezi (TZS) *", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = draft.priceTzs.toString(),
            onValueChange = { onUpdate(draft.copy(priceTzs = it.toLongOrNull() ?: 500000L)) },
            leadingIcon = { Text("TSh", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DopOchre, modifier = Modifier.padding(start = 12.dp)) },
            modifier = Modifier.fillMaxWidth().testTag("studio_input_price"),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Security Deposit
        Text("Amana ya Dhamana (Security Deposit Months)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = draft.depositMonths.toString(),
            onValueChange = { onUpdate(draft.copy(depositMonths = it.toIntOrNull() ?: 1)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Mandatory DoP 50% Platform Fee Banner
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = DopOchreContainer,
            border = BorderStroke(1.dp, DopOchre),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = DopOchre, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Kanuni ya Ada ya Jukwaa la DoP (50% Commission Rule)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = DopNavyPrimary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ada ya DoP ni nusu (50%) ya kodi ya mwezi wa kwanza pekee.",
                    fontSize = 11.sp,
                    color = DopNavyPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Kodi ya Mwezi wa 1:", fontSize = 11.sp, color = DopTextSecondary)
                    Text(DoPStrings.tzs(firstMonthRent), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Ada ya DoP (50%):", fontSize = 11.sp, color = DopOchre, fontWeight = FontWeight.Bold)
                    Text(DoPStrings.tzs(dopFee), fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = DopOchre)
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------
// STEP 10: AVAILABILITY & MOVE-IN
// ----------------------------------------------------------------------------
@Composable
fun StudioStep10Availability(
    availability: AvailabilityStatus,
    moveInDate: String,
    onUpdate: (AvailabilityStatus, String) -> Unit
) {
    DopBentoCard {
        Text("Hatua 10: Upatikanaji na Tarehe ya Kuhamia", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DopNavyPrimary)
        Spacer(modifier = Modifier.height(14.dp))

        val selectableStatuses = listOf(
            AvailabilityStatus.AVAILABLE,
            AvailabilityStatus.RESERVED,
            AvailabilityStatus.RENTED,
            AvailabilityStatus.UNAVAILABLE
        )

        selectableStatuses.forEach { status ->
            val isSelected = availability == status
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) DopOchreContainer else Color.White,
                border = BorderStroke(1.dp, if (isSelected) DopOchre else DopBorderSubtle),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onUpdate(status, moveInDate) }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onUpdate(status, moveInDate) },
                        colors = RadioButtonDefaults.colors(selectedColor = DopOchre)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (status) {
                            AvailabilityStatus.AVAILABLE -> "Ipo Wazi Sasa Hivi (Available Immediately)"
                            AvailabilityStatus.RESERVED -> "Imewekewa Nafasi (Reserved)"
                            AvailabilityStatus.TRANSACTION_PENDING -> "Muamala Unaendelea (Pending)"
                            AvailabilityStatus.RENTED -> "Ina Mpangaji Sasa (Rented)"
                            AvailabilityStatus.SOLD -> "Imeuzwa (Sold)"
                            AvailabilityStatus.LEASED -> "Imekodishwa (Leased)"
                            AvailabilityStatus.UNAVAILABLE -> "Haipatikani kwa Sasa (Unavailable)"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("Tarehe Maalum ya Kuanza Kupangishwa:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = moveInDate,
            onValueChange = { onUpdate(availability, it) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )
    }
}

// ----------------------------------------------------------------------------
// STEP 11: VIEWING SETTINGS & PRIVATE CARETAKER
// ----------------------------------------------------------------------------
@Composable
fun StudioStep11ViewingAndCaretaker(
    draft: PropertyDraft,
    onUpdate: (PropertyDraft) -> Unit
) {
    DopBentoCard {
        Text("Hatua 11: Mipangilio ya Ziara na Mtunzaji wa Siri", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DopNavyPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Taarifa za mtunzaji na maelekezo ya geti hupewa DoP Guide aliyethibitishwa tu.", fontSize = 11.sp, color = DopTextSecondary)

        Spacer(modifier = Modifier.height(14.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = draft.selfVisitEnabled, onCheckedChange = { onUpdate(draft.copy(selfVisitEnabled = it)) })
            Spacer(modifier = Modifier.width(6.dp))
            Text("Ruhusu Ziara ya Kuona Mwenyewe (Self Visit: TSh 0)", fontSize = 12.sp)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = draft.assistedVisitEnabled, onCheckedChange = { onUpdate(draft.copy(assistedVisitEnabled = it)) })
            Spacer(modifier = Modifier.width(6.dp))
            Text("Ruhusu Ziara ya DoP Guide (Assisted Visit: TSh 5,000)", fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))
        Divider(color = DopBorderSubtle)
        Spacer(modifier = Modifier.height(12.dp))

        Text("Taarifa za Siri za Mtunzaji / Mlinzi (Caretaker):", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DopNavyPrimary)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = draft.caretakerName,
            onValueChange = { onUpdate(draft.copy(caretakerName = it)) },
            label = { Text("Jina la Mlinzi / Mtunzaji wa Funguo") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = draft.caretakerPhone,
            onValueChange = { onUpdate(draft.copy(caretakerPhone = it)) },
            label = { Text("Nambari ya Simu ya Mtunzaji") },
            placeholder = { Text("+255 7...") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = draft.privateAccessNotes,
            onValueChange = { onUpdate(draft.copy(privateAccessNotes = it)) },
            label = { Text("Maelekezo ya Siri ya Geti / Funguo") },
            placeholder = { Text("Mf. Piga kengele ya geti la fedha au funguo zipo kwa Mzee Bakari") },
            modifier = Modifier.fillMaxWidth().height(80.dp),
            shape = RoundedCornerShape(10.dp)
        )
    }
}

// ----------------------------------------------------------------------------
// STEP 12: VIRTUAL VIEWING & EXPRESS
// ----------------------------------------------------------------------------
@Composable
fun StudioStep12VirtualViewingSettings(
    draft: PropertyDraft,
    onUpdate: (PropertyDraft) -> Unit
) {
    DopBentoCard {
        Text("Hatua 12: DoP Express & Virtual Viewing", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DopNavyPrimary)
        Spacer(modifier = Modifier.height(14.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(checked = draft.virtualViewEnabled, onCheckedChange = { onUpdate(draft.copy(virtualViewEnabled = it)) })
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text("Live Virtual Viewing kwa Video Call", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("DoP Guides wanaweza kumtembeza mteja kwa njia ya simu ya video.", fontSize = 11.sp, color = DopTextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(checked = draft.isDopExpress, onCheckedChange = { onUpdate(draft.copy(isDopExpress = it)) })
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text("DoP Express Priority Listing", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Inapata alama ya EXPRESS na inapewa kipaumbele cha ziara ndani ya saa 2.", fontSize = 11.sp, color = DopTextSecondary)
            }
        }
    }
}

// ----------------------------------------------------------------------------
// STEP 13: REVIEW & SUBMIT
// ----------------------------------------------------------------------------
@Composable
fun StudioStep13ReviewAndSubmit(
    draft: PropertyDraft,
    onSubmit: () -> Unit
) {
    val score = draft.completenessScore

    DopBentoCard {
        Text("Hatua 13: Uhakiki wa Mwisho (Review & Submission)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DopNavyPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Kagua taarifa zote kabla ya kuwasilisha kwa timu ya ukaguzi ya DoP.", fontSize = 11.sp, color = DopTextSecondary)

        Spacer(modifier = Modifier.height(14.dp))

        // Completeness Score Card
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = DopNavyElevated,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(DopOchre),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$score%",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DopNavyPrimary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (score >= 80) "Alama Nzuri Sana ya Ubora!" else "Mali Inakaribia Kukamilika",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                    Text(
                        text = "${draft.mediaItems.size} picha • Nyaraka ${draft.verificationDocuments.size} • 50% DoP Commission",
                        fontSize = 11.sp,
                        color = DopOchreLight
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Institutional Checklist
        Text("Orodha ya Ukaguzi wa Kisheria (Institutional Checklist):", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(6.dp))

        ChecklistLine("Kichwa & Maelezo ya Nyumba", draft.title.isNotBlank())
        ChecklistLine("Eneo na Wilaya (${draft.ward}, ${draft.district})", draft.ward.isNotBlank() && draft.district.isNotBlank())
        ChecklistLine("Bei ya Kodi (${DoPStrings.tzs(draft.priceTzs)})", draft.priceTzs > 0)
        ChecklistLine("Picha za Ubora wa Juu (${draft.mediaItems.size} zimepakiwa)", draft.mediaItems.isNotEmpty())
        ChecklistLine("Nyaraka za Umiliki zimepakiwa (${draft.verificationDocuments.size})", draft.verificationDocuments.isNotEmpty())

        Spacer(modifier = Modifier.height(16.dp))

        // Live Feed Preview Card
        Text("Muonekano wa Tangazo Lako kwenye Programu:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(6.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = BorderStroke(1.dp, DopBorderSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(draft.title.ifBlank { "Mali ya Kisasa DoP" }, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    DopBadge("REVIEW PENDING", Icons.Default.Schedule, DopOchre, DopNavyElevated)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("${draft.ward}, ${draft.district} • ${DoPStrings.tzs(draft.priceTzs)}/mwezi", fontSize = 11.sp, color = DopTextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Nyaraka na maelekezo ya geti yamelindwa na hayataonekana kwa umma.", fontSize = 10.sp, color = DopTrustGreen)
            }
        }
    }
}

@Composable
private fun ChecklistLine(text: String, isComplete: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isComplete) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isComplete) DopTrustGreen else DopTextSecondary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 12.sp, color = if (isComplete) DopNavyPrimary else DopTextSecondary)
    }
}
