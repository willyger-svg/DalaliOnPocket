package com.example.data.repository

import com.example.core.sync.SyncManager
import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object PropertyRepository {
    // Tanzanian Curated Properties
    private val initialProperties = listOf(
        Property(
            id = "DOP-TZA-DAR-000184",
            title = "Masaki Sunset Executive 3-Bedroom Apartment",
            description = "Ghorofa ya kisasa Masaki peninsula yenye muonekano wa bahari. Inajumuisha chumba kikuu cha kulala chenye bafu la kisasa (master en-suite), AC kwenye kila chumba, jiko la kisasa, standby jenereta, maji ya DAWASA 24/7, na ulinzi wa masaa 24.",
            priceTzs = 1800000,
            transactionType = TransactionType.RENT,
            propertyType = PropertyType.APARTMENT,
            region = "Dar es Salaam",
            district = "Kinondoni",
            ward = "Masaki",
            locationPrivacy = LocationPrivacy.APPROXIMATE,
            bedrooms = 3,
            bathrooms = 2,
            areaSqm = 185,
            isDopExpress = true,
            hasVirtualTour = true,
            hasLiveViewing = true,
            listingStatus = ListingStatus.PUBLISHED,
            verificationStatus = VerificationStatus.VERIFIED,
            availabilityStatus = AvailabilityStatus.AVAILABLE,
            verificationBadges = listOf("Identity Reviewed", "Property Reviewed", "Location Checked", "Docs Reviewed"),
            amenities = listOf("Ocean View", "Air Conditioning", "DAWASA Water", "Standby Generator", "Fenced Security", "Swimming Pool"),
            boostTier = ListingBoostTier.GOLD,
            isBoosted = true,
            images = listOf(
                "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=800&q=80"
            ),
            mediaItems = listOf(
                PropertyMediaItem(
                    remoteUrl = "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?auto=format&fit=crop&w=800&q=80",
                    caption = "Living Room & Ocean View Balcony",
                    isCover = true
                ),
                PropertyMediaItem(
                    remoteUrl = "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?auto=format&fit=crop&w=800&q=80",
                    caption = "Master Bedroom En-Suite"
                ),
                PropertyMediaItem(
                    type = PropertyMediaType.PROPERTY_SHORT,
                    remoteUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=800&q=80",
                    caption = "Quick Reel Walkthrough",
                    durationSeconds = 24
                )
            ),
            virtualTourSections = listOf(
                VirtualTourSection(roomName = "Entrance Gate & Security", mediaUrl = "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?auto=format&fit=crop&w=800&q=80", durationSeconds = 10),
                VirtualTourSection(roomName = "Living Room & Balcony", mediaUrl = "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=800&q=80", durationSeconds = 15),
                VirtualTourSection(roomName = "Fitted Granite Kitchen", mediaUrl = "https://images.unsplash.com/photo-1556911220-e15b29be8c8f?auto=format&fit=crop&w=800&q=80", durationSeconds = 12),
                VirtualTourSection(roomName = "Master Bedroom Suite", mediaUrl = "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?auto=format&fit=crop&w=800&q=80", durationSeconds = 14)
            ),
            virtualScenes = listOf(
                VirtualScene("scene_ext", "Exterior & Compound", "Entrance gate and paved parking compound.", 0xFF0A192F, "scene_living"),
                VirtualScene("scene_living", "Spacious Living Room", "Open plan lounge with ocean breeze balcony.", 0xFF112240, "scene_kitchen"),
                VirtualScene("scene_kitchen", "Modern Kitchen", "Fitted cabinets, granite counters, gas piping.", 0xFF1E3A5F, "scene_master"),
                VirtualScene("scene_master", "Master Bedroom En-Suite", "Walk-in closet and private master bathroom.", 0xFF233554, null)
            ),
            ownerName = "Mama Halima Mgaza",
            ownerPhone = "+255 713 999 888"
        ),
        Property(
            id = "DOP-TZA-DAR-000209",
            title = "Mikocheni B 4-Bedroom Standalone House with Garden",
            description = "Nyumba ya kifahari yenye uwanja mkubwa wa bustani (lush garden), uzio wa ukuta na umeme (electric fence), servant quarter ya vyumba viwili, na tanki la maji la lita 10,000.",
            priceTzs = 1200000,
            transactionType = TransactionType.RENT,
            propertyType = PropertyType.HOUSE,
            region = "Dar es Salaam",
            district = "Kinondoni",
            ward = "Mikocheni B",
            locationPrivacy = LocationPrivacy.APPROXIMATE,
            bedrooms = 4,
            bathrooms = 3,
            areaSqm = 320,
            isDopExpress = true,
            hasVirtualTour = true,
            hasLiveViewing = false,
            listingStatus = ListingStatus.PUBLISHED,
            verificationStatus = VerificationStatus.VERIFIED,
            amenities = listOf("Large Garden", "Electric Fence", "Servant Quarter", "DAWASA Water", "Luku Meter", "Carport"),
            images = listOf(
                "https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?auto=format&fit=crop&w=800&q=80"
            ),
            virtualScenes = listOf(
                VirtualScene("scene_gate", "Main Gate & Garden", "Well-maintained grass lawn and flower beds.", 0xFF0A192F, "scene_hall"),
                VirtualScene("scene_hall", "Dining & Living Area", "High ceilings with chandeliers.", 0xFF112240, null)
            ),
            ownerName = "Mhandisi Saidi Ndauka",
            ownerPhone = "+255 754 888 123"
        ),
        Property(
            id = "DOP-TZA-DAR-000341",
            title = "Sinza Mori Cozy 2-Bedroom Modern Apartment",
            description = "Nyumba safi na ya kisasa Sinza Mori karibu na barabara kuu ya Shekilango. Vyumba viwili (kimoja master), sebule kubwa, marumaru safi na LUKU ya kujitegemea.",
            priceTzs = 550000,
            transactionType = TransactionType.RENT,
            propertyType = PropertyType.APARTMENT,
            region = "Dar es Salaam",
            district = "Ubungo",
            ward = "Sinza",
            locationPrivacy = LocationPrivacy.EXACT,
            bedrooms = 2,
            bathrooms = 2,
            areaSqm = 95,
            isDopExpress = true,
            hasVirtualTour = true,
            hasLiveViewing = true,
            listingStatus = ListingStatus.PUBLISHED,
            verificationStatus = VerificationStatus.VERIFIED,
            amenities = listOf("Luku Yako", "Maji ya DAWASA", "Balcony", "Paved Compound", "Good Road Access"),
            images = listOf(
                "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=800&q=80"
            ),
            virtualScenes = listOf(
                VirtualScene("scene_living", "Living Area", "Tiled floors and ample natural light.", 0xFF0A192F, "scene_bed")
            ),
            ownerName = "Bw. Emmanuel Mushi",
            ownerPhone = "+255 784 112 334"
        ),
        Property(
            id = "DOP-TZA-DAR-000412",
            title = "Kariakoo Prime Commercial Retail Shop (Fremu)",
            description = "Fremu ya biashara mtaa wa Kongo / Nyamwezi. Inafaa sana kwa duka la nguo, vipodozi, simu au jumla. Ina roller shutter ya chuma na umeme binafsi.",
            priceTzs = 850000,
            transactionType = TransactionType.RENT,
            propertyType = PropertyType.SHOP,
            region = "Dar es Salaam",
            district = "Ilala",
            ward = "Kariakoo",
            locationPrivacy = LocationPrivacy.EXACT,
            bedrooms = 0,
            bathrooms = 1,
            areaSqm = 45,
            isDopExpress = false,
            hasVirtualTour = false,
            hasLiveViewing = false,
            listingStatus = ListingStatus.PUBLISHED,
            verificationStatus = VerificationStatus.VERIFIED,
            amenities = listOf("Prime Foot Traffic", "Iron Roller Shutter", "Private Luku", "Glass Showcase Area"),
            images = listOf(
                "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?auto=format&fit=crop&w=800&q=80"
            ),
            ownerName = "Hajjat Mariam Kivuyo",
            ownerPhone = "+255 655 400 900"
        ),
        Property(
            id = "DOP-TZA-ARU-000052",
            title = "Arusha Njiro Executive Villa with Mt. Meru Views",
            description = "Nyumba ya kifahari Njiro Hill yenye muonekano wa Mlima Meru. Bustani kubwa, fireplace sebuleni, na usalama wa juu.",
            priceTzs = 1500000,
            transactionType = TransactionType.RENT,
            propertyType = PropertyType.HOUSE,
            region = "Arusha",
            district = "Arusha Mjini",
            ward = "Njiro",
            locationPrivacy = LocationPrivacy.APPROXIMATE,
            bedrooms = 4,
            bathrooms = 3,
            areaSqm = 280,
            isDopExpress = false,
            hasVirtualTour = true,
            hasLiveViewing = true,
            listingStatus = ListingStatus.PUBLISHED,
            verificationStatus = VerificationStatus.VERIFIED,
            amenities = listOf("Mt. Meru View", "Fireplace", "Garden", "Solar Water Heating", "Borehole Water"),
            images = listOf(
                "https://images.unsplash.com/photo-1580587771525-78b9dba3b914?auto=format&fit=crop&w=800&q=80"
            ),
            ownerName = "Dkt. Wilson Mollel",
            ownerPhone = "+255 767 111 222"
        ),
        Property(
            id = "DOP-TZA-DAR-000991",
            title = "Kigamboni Commercial Plot (1,200 SQM) Near Ferry",
            description = "Kiwanja kizuri cha biashara au makazi Kigamboni, umbali wa dakika 8 kutoka darajani. Hati miliki safi (title deed available for review via DoP).",
            priceTzs = 85000000,
            transactionType = TransactionType.SALE,
            propertyType = PropertyType.COMMERCIAL_LAND,
            region = "Dar es Salaam",
            district = "Kigamboni",
            ward = "Tuangoma",
            locationPrivacy = LocationPrivacy.HIDDEN,
            bedrooms = 0,
            bathrooms = 0,
            areaSqm = 1200,
            isDopExpress = false,
            hasVirtualTour = false,
            hasLiveViewing = false,
            listingStatus = ListingStatus.PUBLISHED,
            verificationStatus = VerificationStatus.VERIFIED,
            verificationBadges = listOf("Identity Reviewed", "Docs Reviewed", "Location Checked"),
            amenities = listOf("Surveyed & Beacons Set", "Direct Tarmac Access", "Electricity Nearby", "Water Mains Available"),
            images = listOf(
                "https://images.unsplash.com/photo-1500382017468-9049fed747ef?auto=format&fit=crop&w=800&q=80"
            ),
            ownerName = "Mzee Selemani Mkandala",
            ownerPhone = "+255 712 334 455"
        )
    )

    private val _properties = MutableStateFlow(initialProperties)
    val properties: StateFlow<List<Property>> = _properties.asStateFlow()

    private val _savedPropertyIds = MutableStateFlow<Set<String>>(setOf("DOP-TZA-DAR-000184"))
    val savedPropertyIds: StateFlow<Set<String>> = _savedPropertyIds.asStateFlow()

    // Owner Draft Management
    private val _ownerDraft = MutableStateFlow<PropertyDraft?>(
        PropertyDraft(
            draftId = "DRAFT-TZA-9182",
            ownerId = "OWN-TZA-0882",
            lastSavedTimestamp = System.currentTimeMillis() - 120_000L, // 2 mins ago
            currentStep = 4,
            title = "Masaki Penthouse Luxury Residence",
            tagline = "Panoramic ocean view penthouse with private elevator",
            description = "Ghorofa ya kifahari yenye vyumba 3 vikubwa, jiko la kisasa la granite, na swimming pool.",
            priceTzs = 2500000L,
            region = "Dar es Salaam",
            district = "Kinondoni",
            ward = "Masaki",
            bedrooms = 3,
            bathrooms = 3,
            areaSqm = 220,
            mediaItems = listOf(
                PropertyMediaItem(
                    remoteUrl = "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?auto=format&fit=crop&w=800&q=80",
                    caption = "Living Room & Ocean Terrace",
                    isCover = true
                ),
                PropertyMediaItem(
                    remoteUrl = "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?auto=format&fit=crop&w=800&q=80",
                    caption = "Master Bedroom Suite"
                )
            )
        )
    )
    val ownerDraft: StateFlow<PropertyDraft?> = _ownerDraft.asStateFlow()

    fun saveDraft(draft: PropertyDraft) {
        _ownerDraft.value = draft.copy(lastSavedTimestamp = System.currentTimeMillis())
    }

    fun discardDraft() {
        _ownerDraft.value = null
    }

    fun submitDraft(draft: PropertyDraft): Property {
        val propertyId = "DOP-TZA-${draft.region.take(3).uppercase()}-${(1000..9999).random()}"
        val coverImage = draft.mediaItems.firstOrNull { it.isCover }?.remoteUrl
            ?: draft.mediaItems.firstOrNull()?.remoteUrl
            ?: "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?auto=format&fit=crop&w=800&q=80"
        
        val allImages = draft.mediaItems.filter { it.type == PropertyMediaType.PHOTO }.map { it.remoteUrl }.ifEmpty {
            listOf(coverImage)
        }

        val newProp = Property(
            id = propertyId,
            title = draft.title.ifBlank { "Mali ya Kisasa ${draft.ward}" },
            description = draft.description.ifBlank { "Mali iliyothibitishwa na kukaguliwa na timu ya DoP." },
            priceTzs = draft.priceTzs,
            transactionType = draft.transactionType,
            propertyType = draft.propertyType,
            region = draft.region,
            district = draft.district,
            ward = draft.ward,
            locationPrivacy = draft.locationPrivacy,
            bedrooms = draft.bedrooms,
            bathrooms = draft.bathrooms,
            areaSqm = draft.areaSqm,
            isDopExpress = draft.isDopExpress,
            hasVirtualTour = draft.virtualViewEnabled || draft.virtualTourSections.isNotEmpty(),
            hasLiveViewing = true,
            listingStatus = ListingStatus.SUBMITTED,
            verificationStatus = VerificationStatus.PENDING,
            availabilityStatus = draft.availabilityStatus,
            verificationBadges = listOf("Draft Converted", "Awaiting Inspection", "ID Under Review"),
            amenities = draft.amenities + draft.customAmenities,
            images = allImages,
            mediaItems = draft.mediaItems,
            virtualTourSections = draft.virtualTourSections,
            verificationDocuments = draft.verificationDocuments,
            ownerId = draft.ownerId,
            ownerName = "Eng. Grace Ndesamburo",
            ownerPhone = "+255 784 567 890",
            depositMonths = draft.depositMonths,
            privateCaretakerName = draft.caretakerName.ifBlank { null },
            privateCaretakerPhone = draft.caretakerPhone.ifBlank { null },
            privateAccessInstructions = draft.privateAccessNotes.ifBlank { null },
            createdAt = System.currentTimeMillis()
        )

        addPropertyByOwner(newProp)
        _ownerDraft.value = null // Cleared upon successful submission
        return newProp
    }

    // Viewing Bookings
    private val _viewingBookings = MutableStateFlow(
        listOf(
            ViewingBooking(
                bookingCode = "DOP-BK-8492",
                propertyId = "DOP-TZA-DAR-000184",
                propertyTitle = "Masaki Sunset Executive 3-Bedroom Apartment",
                propertyLocation = "Masaki, Kinondoni, Dar es Salaam",
                customerId = "USR-TZA-0081",
                customerName = "Juma Bakari",
                viewingType = ViewingType.DOP_ASSISTED_VISIT,
                scheduledDate = "Kesho / Tomorrow (10:00 AM)",
                scheduledTimeSlot = "10:00 AM - 11:00 AM",
                meetingPoint = "Shoppers Plaza Masaki Entrance",
                status = ViewingStatus.GUIDE_ON_THE_WAY,
                guideId = "GDE-104",
                guideName = "Rashid 'Dalali' Mwita",
                guidePhone = "+255 682 555 111",
                feeTzs = 5000,
                guidePayoutTzs = 2500,
                dopPlatformFeeTzs = 2500,
                customerNotes = "Nitafika na mke wangu kuangalia ukubwa wa jiko."
            )
        )
    )
    val viewingBookings: StateFlow<List<ViewingBooking>> = _viewingBookings.asStateFlow()

    // Rental Applications
    private val _rentalApplications = MutableStateFlow(
        listOf(
            RentalApplication(
                id = "DOP-APP-9921",
                propertyId = "DOP-TZA-DAR-000184",
                propertyTitle = "Masaki Sunset Executive 3-Bedroom Apartment",
                applicantId = "USR-TZA-0081",
                applicantName = "Juma Bakari",
                applicantPhone = "+255 754 123 456",
                moveInDate = "01 Oktoba 2026",
                rentalDurationMonths = 6,
                numberOfOccupants = 3,
                occupation = "Senior Systems Engineer at Vodacom Tanzania",
                monthlyIncomeTzs = 4500000,
                applicantNotes = "Nipo tayari kulipa miezi sita mapema pamoja na kodi ya DoP.",
                status = ApplicationStatus.APPROVED
            )
        )
    )
    val rentalApplications: StateFlow<List<RentalApplication>> = _rentalApplications.asStateFlow()

    // Tenancy Agreements
    private val _tenancyAgreements = MutableStateFlow(
        listOf(
            TenancyAgreement(
                agreementCode = "DOP-AGR-4018",
                propertyId = "DOP-TZA-DAR-000184",
                propertyTitle = "Masaki Sunset Executive 3-Bedroom Apartment",
                propertyLocation = "Masaki, Kinondoni, Dar es Salaam",
                ownerId = "OWN-882",
                ownerName = "Mama Halima Mgaza",
                tenantId = "USR-TZA-0081",
                tenantName = "Juma Bakari",
                monthlyRentTzs = 1800000,
                durationMonths = 6,
                depositTzs = 1800000,
                // Critical Business Rule: 50% of 1st Month's Rent ONLY
                dopPlatformFeeTzs = 900000, // 50% of 1,800,000
                totalInitialPayableTzs = (1800000 * 6) + 1800000 + 900000, // Rent + Deposit + DoP Fee
                status = TenancyAgreementStatus.PENDING_CUSTOMER_ACCEPTANCE,
                version = 1,
                termsSummary = "Mkataba rasmi wa kupangisha nyumba chini ya sheria za Jamhuri ya Muungano wa Tanzania kupitia jukwaa la Dalalion Pocket. Kodi ya jukwaa ni asilimia 50 ya mwezi wa kwanza pekee (one-time)."
            )
        )
    )
    val tenancyAgreements: StateFlow<List<TenancyAgreement>> = _tenancyAgreements.asStateFlow()

    fun toggleSave(propertyId: String) {
        val current = _savedPropertyIds.value.toMutableSet()
        if (current.contains(propertyId)) {
            current.remove(propertyId)
        } else {
            current.add(propertyId)
        }
        _savedPropertyIds.value = current
    }

    fun bookViewing(
        property: Property,
        customerId: String,
        customerName: String,
        type: ViewingType,
        date: String,
        timeSlot: String,
        meetingPoint: String,
        notes: String
    ): ViewingBooking {
        val fee = if (type == ViewingType.DOP_ASSISTED_VISIT) 5000L else 0L
        val guidePayout = if (type == ViewingType.DOP_ASSISTED_VISIT) 2500L else 0L
        val dopSplit = if (type == ViewingType.DOP_ASSISTED_VISIT) 2500L else 0L

        val booking = ViewingBooking(
            bookingCode = "DOP-BK-${(1000..9999).random()}",
            propertyId = property.id,
            propertyTitle = property.title,
            propertyLocation = "${property.ward}, ${property.region}",
            customerId = customerId,
            customerName = customerName,
            viewingType = type,
            scheduledDate = date,
            scheduledTimeSlot = timeSlot,
            meetingPoint = meetingPoint.ifBlank { "Nje ya lango kuu / Main Gate" },
            status = if (type == ViewingType.DOP_ASSISTED_VISIT) ViewingStatus.GUIDE_ASSIGNED else ViewingStatus.CONFIRMED,
            guideId = if (type == ViewingType.DOP_ASSISTED_VISIT) "GDE-104" else null,
            guideName = if (type == ViewingType.DOP_ASSISTED_VISIT) "Rashid 'Dalali' Mwita" else null,
            guidePhone = if (type == ViewingType.DOP_ASSISTED_VISIT) "+255 682 555 111" else null,
            feeTzs = fee,
            guidePayoutTzs = guidePayout,
            dopPlatformFeeTzs = dopSplit,
            customerNotes = notes
        )
        _viewingBookings.value = listOf(booking) + _viewingBookings.value

        SyncManager.emitRealtimeEvent(
            channel = "viewings",
            entityId = booking.bookingCode,
            action = "BOOKING_CREATED",
            payload = "Miadi mpya ya ${booking.propertyTitle} imethibitishwa: ${booking.bookingCode}"
        )
        return booking
    }

    fun advanceViewingStatus(bookingCode: String, nextStatus: ViewingStatus) {
        _viewingBookings.value = _viewingBookings.value.map {
            if (it.bookingCode == bookingCode) it.copy(status = nextStatus) else it
        }
        SyncManager.emitRealtimeEvent(
            channel = "viewings",
            entityId = bookingCode,
            action = "STATUS_UPDATED",
            payload = "Hali ya miadi $bookingCode imebadilika kuwa: ${nextStatus.name}"
        )
    }

    fun submitRentalApplication(
        property: Property,
        applicantName: String,
        applicantPhone: String,
        moveInDate: String,
        durationMonths: Int,
        occupants: Int,
        occupation: String,
        incomeTzs: Long,
        notes: String
    ): RentalApplication {
        val app = RentalApplication(
            propertyId = property.id,
            propertyTitle = property.title,
            applicantId = "USR-TZA-0081",
            applicantName = applicantName,
            applicantPhone = applicantPhone,
            moveInDate = moveInDate,
            rentalDurationMonths = durationMonths,
            numberOfOccupants = occupants,
            occupation = occupation,
            monthlyIncomeTzs = incomeTzs,
            applicantNotes = notes,
            status = ApplicationStatus.SUBMITTED
        )
        _rentalApplications.value = listOf(app) + _rentalApplications.value
        SyncManager.emitRealtimeEvent(
            channel = "applications",
            entityId = app.id,
            action = "APPLICATION_SUBMITTED",
            payload = "Ombi jipya la kupanga $applicantName limepokelewa kwa ajili ya ${property.title}"
        )
        return app
    }

    fun reviewApplication(applicationId: String, approve: Boolean) {
        val status = if (approve) ApplicationStatus.APPROVED else ApplicationStatus.DECLINED
        _rentalApplications.value = _rentalApplications.value.map {
            if (it.id == applicationId) it.copy(status = status) else it
        }
        SyncManager.emitRealtimeEvent(
            channel = "applications",
            entityId = applicationId,
            action = if (approve) "APPLICATION_APPROVED" else "APPLICATION_DECLINED",
            payload = "Ombi $applicationId lime${if (approve) "idhinishwa na mwenye nyumba" else "kataliwa"}"
        )
    }

    fun toggleListingPause(propertyId: String) {
        _properties.value = _properties.value.map { prop ->
            if (prop.id == propertyId) {
                val newStatus = if (prop.listingStatus == ListingStatus.PAUSED) ListingStatus.PUBLISHED else ListingStatus.PAUSED
                prop.copy(listingStatus = newStatus)
            } else {
                prop
            }
        }
    }

    fun customerAcceptAgreement(agreementCode: String) {
        _tenancyAgreements.value = _tenancyAgreements.value.map {
            if (it.agreementCode == agreementCode) {
                val nextStatus = if (it.ownerAcceptedAt != null) TenancyAgreementStatus.MUTUALLY_ACCEPTED else TenancyAgreementStatus.CUSTOMER_ACCEPTED
                it.copy(
                    status = nextStatus,
                    customerAcceptedAt = System.currentTimeMillis()
                )
            } else it
        }
        SyncManager.emitRealtimeEvent(
            channel = "agreements",
            entityId = agreementCode,
            action = "CUSTOMER_ACCEPTED",
            payload = "Mpangaji amekubali mkataba $agreementCode kielektroniki."
        )
    }

    fun ownerAcceptAgreement(agreementCode: String) {
        _tenancyAgreements.value = _tenancyAgreements.value.map {
            if (it.agreementCode == agreementCode) {
                val isMutual = it.customerAcceptedAt != null
                it.copy(
                    status = if (isMutual) TenancyAgreementStatus.LOCKED else TenancyAgreementStatus.OWNER_ACCEPTED,
                    ownerAcceptedAt = System.currentTimeMillis(),
                    lockedAt = if (isMutual) System.currentTimeMillis() else null
                )
            } else it
        }
        SyncManager.emitRealtimeEvent(
            channel = "agreements",
            entityId = agreementCode,
            action = "OWNER_ACCEPTED_AND_LOCKED",
            payload = "Mwenye nyumba amekubali mkataba $agreementCode. Mkataba sasa umefungwa (LOCKED)."
        )
    }

    fun addPropertyByOwner(newProperty: Property) {
        _properties.value = listOf(newProperty) + _properties.value
        SyncManager.emitRealtimeEvent(
            channel = "properties",
            entityId = newProperty.id,
            action = "PROPERTY_ADDED",
            payload = "Nyumba mpya imeongezwa: ${newProperty.title} (${newProperty.id})"
        )
    }

    // ------------------------------------------------------------------------
    // MONETIZATION & WALLET TRANSACTIONS ENGINE (IN-APP REVENUE)
    // ------------------------------------------------------------------------
    private val _transactions = MutableStateFlow<List<WalletTransaction>>(
        listOf(
            WalletTransaction(
                id = "TRX-DOP-9912",
                referenceNo = "MPESA-QA99X72K",
                amountTzs = 900000,
                payerName = "Juma Bakari",
                payerPhone = "+255 754 123 456",
                method = PaymentMethod.M_PESA,
                source = RevenueSource.TENANCY_COMMISSION,
                isCreditToPlatform = true,
                status = PaymentStatus.COMPLETED,
                description = "Ada ya Udalali DoP (50% Mwezi wa 1) - Masaki Sunset",
                efdReceiptCode = "TRA-EFD-2026-992144"
            ),
            WalletTransaction(
                id = "TRX-DOP-9913",
                referenceNo = "TIGO-MIX-4412",
                amountTzs = 35000,
                payerName = "Mama Halima Mgaza",
                payerPhone = "+255 713 999 888",
                method = PaymentMethod.TIGO_PESA,
                source = RevenueSource.LISTING_BOOST,
                isCreditToPlatform = true,
                status = PaymentStatus.COMPLETED,
                description = "Malipo ya Gold Spotlight Boost (Siku 30)",
                efdReceiptCode = "TRA-EFD-2026-992145"
            ),
            WalletTransaction(
                id = "TRX-DOP-9914",
                referenceNo = "AIRTEL-MONEY-882",
                amountTzs = 25000,
                payerName = "Eng. Daniel Minja",
                payerPhone = "+255 784 555 333",
                method = PaymentMethod.AIRTEL_MONEY,
                source = RevenueSource.TITLE_DEED_SEARCH,
                isCreditToPlatform = true,
                status = PaymentStatus.COMPLETED,
                description = "Uhakiki wa Hati Miliki Wizara ya Ardhi (Kiwanja 41, Block B)",
                efdReceiptCode = "TRA-EFD-2026-992146"
            ),
            WalletTransaction(
                id = "TRX-DOP-9915",
                referenceNo = "MPESA-QA88X19M",
                amountTzs = 2500,
                payerName = "Juma Bakari",
                payerPhone = "+255 754 123 456",
                method = PaymentMethod.M_PESA,
                source = RevenueSource.VIEWING_FEE,
                isCreditToPlatform = true,
                status = PaymentStatus.COMPLETED,
                description = "Gawio la DoP - Assisted Viewing Masaki",
                efdReceiptCode = "TRA-EFD-2026-992147"
            )
        )
    )
    val transactions: StateFlow<List<WalletTransaction>> = _transactions.asStateFlow()

    // Title Deed Verification Registry
    private val _titleVerifications = MutableStateFlow<List<TitleDeedVerificationOrder>>(
        listOf(
            TitleDeedVerificationOrder(
                orderId = "ARDHI-SRCH-9011",
                propertyId = "DOP-TZA-DAR-000184",
                propertyTitle = "Masaki Sunset Executive 3-Bedroom Apartment",
                applicantName = "Juma Bakari",
                applicantPhone = "+255 754 123 456",
                plotNumber = "Plot No. 412/1",
                blockNumber = "Block C, Masaki Peninsula",
                titleNumber = "Title No. 048291-DAR",
                feeTzs = 25000,
                status = TitleVerificationStatus.VERIFIED_CLEAN,
                registryNotes = "Hati imethibitishwa: Imesajiliwa Wizara ya Ardhi chini ya Halima Mgaza. Haina mgogoro wa kifamilia wala zuio la benki."
            )
        )
    )
    val titleVerifications: StateFlow<List<TitleDeedVerificationOrder>> = _titleVerifications.asStateFlow()

    fun getFinancialSummary(): PlatformFinancialSummary {
        val list = _transactions.value
        val credits = list.filter { it.isCreditToPlatform && it.status == PaymentStatus.COMPLETED }
        val debits = list.filter { !it.isCreditToPlatform && it.status == PaymentStatus.COMPLETED }

        val gross = credits.sumOf { it.amountTzs }
        val withdrawn = debits.sumOf { it.amountTzs }
        val available = gross - withdrawn

        val commission = credits.filter { it.source == RevenueSource.TENANCY_COMMISSION }.sumOf { it.amountTzs }
        val viewing = credits.filter { it.source == RevenueSource.VIEWING_FEE }.sumOf { it.amountTzs }
        val boosts = credits.filter { it.source == RevenueSource.LISTING_BOOST }.sumOf { it.amountTzs }
        val verifications = credits.filter { it.source == RevenueSource.TITLE_DEED_SEARCH }.sumOf { it.amountTzs }

        return PlatformFinancialSummary(
            totalGrossRevenueTzs = gross,
            totalCommissionTzs = commission,
            totalViewingFeesTzs = viewing,
            totalBoostRevenueTzs = boosts,
            totalVerificationRevenueTzs = verifications,
            availableBalanceTzs = available,
            totalWithdrawnTzs = withdrawn,
            pendingEscrowTzs = 12600000L
        )
    }

    fun boostListing(
        propertyId: String,
        tier: ListingBoostTier,
        payerName: String,
        payerPhone: String,
        method: PaymentMethod
    ): WalletTransaction {
        _properties.value = _properties.value.map {
            if (it.id == propertyId) it.copy(isBoosted = true, boostTier = tier) else it
        }

        val refNo = "${method.name.take(4)}-${(10000..99999).random()}K"
        val transaction = WalletTransaction(
            referenceNo = refNo,
            amountTzs = tier.priceTzs,
            payerName = payerName,
            payerPhone = payerPhone,
            method = method,
            source = RevenueSource.LISTING_BOOST,
            isCreditToPlatform = true,
            status = PaymentStatus.COMPLETED,
            description = "Malipo ya ${tier.titleSw} kwa nyumba $propertyId"
        )
        _transactions.value = listOf(transaction) + _transactions.value

        SyncManager.emitRealtimeEvent(
            channel = "revenue",
            entityId = transaction.id,
            action = "BOOST_PURCHASED",
            payload = "Malipo ya Boost TSh ${tier.priceTzs} yamepokelewa kupitia ${method.title}"
        )
        return transaction
    }

    fun orderTitleVerification(
        propertyId: String,
        propertyTitle: String,
        applicantName: String,
        applicantPhone: String,
        plotNo: String,
        blockNo: String,
        titleNo: String,
        method: PaymentMethod
    ): TitleDeedVerificationOrder {
        val order = TitleDeedVerificationOrder(
            propertyId = propertyId,
            propertyTitle = propertyTitle,
            applicantName = applicantName,
            applicantPhone = applicantPhone,
            plotNumber = plotNo.ifBlank { "Plot 12" },
            blockNumber = blockNo.ifBlank { "Block A" },
            titleNumber = titleNo.ifBlank { "HAT-${(10000..99999).random()}" },
            feeTzs = 25000,
            status = TitleVerificationStatus.VERIFIED_CLEAN
        )
        _titleVerifications.value = listOf(order) + _titleVerifications.value

        val refNo = "${method.name.take(4)}-${(10000..99999).random()}V"
        val transaction = WalletTransaction(
            referenceNo = refNo,
            amountTzs = 25000,
            payerName = applicantName,
            payerPhone = applicantPhone,
            method = method,
            source = RevenueSource.TITLE_DEED_SEARCH,
            isCreditToPlatform = true,
            status = PaymentStatus.COMPLETED,
            description = "Uhakiki wa Hati Miliki Wizara ya Ardhi: $propertyTitle"
        )
        _transactions.value = listOf(transaction) + _transactions.value

        SyncManager.emitRealtimeEvent(
            channel = "revenue",
            entityId = transaction.id,
            action = "TITLE_SEARCH_PAID",
            payload = "Uhakiki wa Ardhi kwa ${order.plotNumber} umelipwa TSh 25,000 na kuthibitishwa."
        )
        return order
    }

    fun payTenancyAgreement(
        agreementCode: String,
        payerPhone: String,
        method: PaymentMethod
    ): WalletTransaction? {
        val agr = _tenancyAgreements.value.find { it.agreementCode == agreementCode } ?: return null

        _tenancyAgreements.value = _tenancyAgreements.value.map {
            if (it.agreementCode == agreementCode) {
                it.copy(
                    status = TenancyAgreementStatus.LOCKED,
                    lockedAt = System.currentTimeMillis()
                )
            } else it
        }

        val dopCommission = agr.dopPlatformFeeTzs
        val refNo = "${method.name.take(4)}-${(10000..99999).random()}E"
        val transaction = WalletTransaction(
            referenceNo = refNo,
            amountTzs = dopCommission,
            payerName = agr.tenantName,
            payerPhone = payerPhone,
            method = method,
            source = RevenueSource.TENANCY_COMMISSION,
            isCreditToPlatform = true,
            status = PaymentStatus.COMPLETED,
            description = "Ada ya Udalali DoP (50% Mwezi wa 1) - Mkataba $agreementCode"
        )
        _transactions.value = listOf(transaction) + _transactions.value

        SyncManager.emitRealtimeEvent(
            channel = "agreements",
            entityId = agreementCode,
            action = "AGREEMENT_ESCROW_PAID",
            payload = "Kodi na Ada ya Udalali TSh $dopCommission imelipwa kikamilifu kupitia ${method.title}. Mkataba umefungwa kisheria!"
        )
        return transaction
    }

    fun withdrawFunds(
        amountTzs: Long,
        targetPhone: String,
        method: PaymentMethod
    ): WalletTransaction {
        val refNo = "${method.name.take(4)}-WDR-${(10000..99999).random()}"
        val transaction = WalletTransaction(
            referenceNo = refNo,
            amountTzs = amountTzs,
            payerName = "DoP Platform Owner",
            payerPhone = targetPhone,
            method = method,
            source = RevenueSource.WITHDRAWAL_PAYOUT,
            isCreditToPlatform = false,
            status = PaymentStatus.COMPLETED,
            description = "Utoaji wa Mapato ya DoP kwenda ${method.title} ($targetPhone)"
        )
        _transactions.value = listOf(transaction) + _transactions.value

        SyncManager.emitRealtimeEvent(
            channel = "revenue",
            entityId = transaction.id,
            action = "WITHDRAWAL_COMPLETED",
            payload = "Kiasi cha TSh $amountTzs kimelipwa kwa mafanikio kwenda ${method.title} ($targetPhone)"
        )
        return transaction
    }

    fun recordDirectPayment(
        amountTzs: Long,
        payerPhone: String,
        method: PaymentMethod,
        source: RevenueSource,
        description: String
    ): WalletTransaction {
        val refNo = "${method.name.take(4)}-${(10000..99999).random()}TX"
        val transaction = WalletTransaction(
            referenceNo = refNo,
            amountTzs = amountTzs,
            payerName = "Mtumiaji wa DoP ($payerPhone)",
            payerPhone = payerPhone,
            method = method,
            source = source,
            isCreditToPlatform = true,
            status = PaymentStatus.COMPLETED,
            description = description
        )
        _transactions.value = listOf(transaction) + _transactions.value

        SyncManager.emitRealtimeEvent(
            channel = "revenue",
            entityId = transaction.id,
            action = "PAYMENT_RECEIVED",
            payload = "Malipo ya TSh $amountTzs kupitia ${method.title} yamethibitishwa. Risiti: ${transaction.efdReceiptCode}"
        )
        return transaction
    }
}

