package com.example.data.model

import java.util.UUID

// ============================================================
// 1. ROLES, CAPABILITIES & AUTHENTICATION
// ============================================================
enum class UserRole {
    CUSTOMER,
    OWNER,
    GUIDE,
    ADMIN
}

/**
 * Account-level status independent of individual capabilities
 */
enum class AccountStatus {
    ACTIVE,
    SUSPENDED,
    LOCKED,
    DISABLED
}

/**
 * Owner capability lifecycle
 */
enum class OwnerCapabilityStatus {
    NOT_STARTED,
    ONBOARDING,
    ACTIVE,
    SUSPENDED
}

/**
 * Guide operational capability lifecycle
 */
enum class GuideCapabilityStatus {
    NOT_APPLIED,
    DRAFT,
    SUBMITTED,
    UNDER_REVIEW,
    NEEDS_MORE_INFORMATION,
    INTERVIEW_REQUIRED,
    TRAINING_REQUIRED,
    TRAINING_COMPLETED,
    APPROVED,
    ACTIVE,
    SUSPENDED,
    REJECTED,
    DEACTIVATED
}

enum class GuideLevel(val label: String) {
    ROOKIE("Rookie"),
    VERIFIED("Verified"),
    PRO("Pro"),
    ELITE("Elite")
}

enum class GuideJobStatus {
    OFFERED,
    ACCEPTED,
    ON_THE_WAY,
    ARRIVED,
    VIEWING_STARTED,
    VIEWING_COMPLETED,
    DECLINED,
    EXPIRED,
    CANCELLED,
    RESCHEDULED,
    NO_SHOW
}

enum class GuideEarningStatus {
    PENDING,
    APPROVED,
    PAID,
    CANCELLED,
    DISPUTED
}

enum class OwnerType(val titleSw: String, val titleEn: String) {
    INDIVIDUAL("Mmiliki Binafsi", "Individual Owner"),
    BUSINESS("Kampuni ya Mali / Biashara", "Real Estate Company / Business"),
    PROPERTY_MANAGER("Meneja wa Majengo", "Property Manager"),
    AUTHORIZED_REPRESENTATIVE("Mwakilishi Aliyeidhinishwa", "Authorized Representative")
}

data class CustomerProfile(
    val preferredLocations: List<String> = listOf("Mikocheni", "Masaki", "Sinza", "Kinondoni"),
    val budgetMaxTzs: Long = 1_200_000L,
    val preferredCategory: PropertyCategory = PropertyCategory.RESIDENTIAL
)

data class OwnerProfile(
    val ownerType: OwnerType = OwnerType.INDIVIDUAL,
    val displayName: String = "",
    val businessName: String = "",
    val taxIdOrNin: String = "",
    val operatingRegions: List<String> = listOf("Dar es Salaam"),
    val operatingDistricts: List<String> = listOf("Kinondoni", "Ilala"),
    val primaryReason: String = "Upangaji wa Nyumba",
    val verificationStatus: VerificationStatus = VerificationStatus.VERIFIED,
    val contactPhone: String = "",
    val payoutMethod: String = "M-Pesa (Vodacom Tanzania)",
    val payoutNumber: String = ""
)

data class GuideApplication(
    val fullName: String,
    val phone: String,
    val email: String = "",
    val nationalIdNin: String = "",
    val operatingZones: List<String> = emptyList(),
    val experienceYears: Int = 2,
    val transportMode: String = "Pikipiki / Bodaboda",
    val languages: List<String> = listOf("Kiswahili", "English"),
    val availability: String = "Muda Wote (Full Time)",
    val emergencyContactName: String = "",
    val emergencyContactPhone: String = "",
    val acceptedCodeOfConduct: Boolean = true,
    val submittedAt: Long = System.currentTimeMillis(),
    val status: GuideCapabilityStatus = GuideCapabilityStatus.SUBMITTED,
    val reviewNotes: String? = null
)

data class GuideProfile(
    val level: GuideLevel = GuideLevel.ROOKIE,
    val isAvailable: Boolean = false,
    val rating: Float = 0f,
    val completedVisits: Int = 0
)

data class UserSession(
    val id: String = "USR-TZA-0091",
    val name: String = "Juma Bakari",
    val email: String = "juma.bakari@dalalionpocket.tz",
    val phone: String = "+255 754 123 456",
    val accountStatus: AccountStatus = AccountStatus.ACTIVE,

    // Capability statuses
    val isCustomerActive: Boolean = true,
    val ownerCapability: OwnerCapabilityStatus = OwnerCapabilityStatus.NOT_STARTED,
    val guideCapability: GuideCapabilityStatus = GuideCapabilityStatus.NOT_APPLIED,

    // Active mode in UI
    val activeMode: UserRole = UserRole.CUSTOMER,

    // Specific profiles
    val customerProfile: CustomerProfile = CustomerProfile(),
    val ownerProfile: OwnerProfile? = null,
    val guideApplication: GuideApplication? = null,
    val guideProfile: GuideProfile? = null,

    // Deprecated/compat property (maps to activeMode)
    val role: UserRole = activeMode,
    val isVerified: Boolean = true,
    val avatarUrl: String = "",
    val accessToken: String = "dop_oauth2_jwt_access_token_tz_secure",
    val refreshToken: String = "dop_oauth2_jwt_refresh_token_tz_secure"
) {
    val canAccessCustomer: Boolean get() = isCustomerActive && accountStatus == AccountStatus.ACTIVE
    val canAccessOwner: Boolean get() = ownerCapability == OwnerCapabilityStatus.ACTIVE && accountStatus == AccountStatus.ACTIVE
    val canAccessGuide: Boolean get() = guideCapability == GuideCapabilityStatus.APPROVED && accountStatus == AccountStatus.ACTIVE

    val availableModes: List<UserRole> get() = buildList {
        if (canAccessCustomer) add(UserRole.CUSTOMER)
        if (canAccessOwner) add(UserRole.OWNER)
        if (canAccessGuide) add(UserRole.GUIDE)
    }
}

/**
 * Server-authoritative single-use handoff session for Admin Web Portal.
 * Never stores credentials on the mobile client.
 */
data class AdminHandoffSession(
    val oneTimeCode: String,
    val adminEmail: String,
    val adminName: String,
    val generatedAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + 60_000L, // 60 seconds single-use validity
    val handoffUrl: String,
    val isConsumed: Boolean = false
)

// ============================================================
// 2. PROPERTY DOMAIN & CLASSIFICATION
// ============================================================
enum class PropertyCategory {
    RESIDENTIAL,
    COMMERCIAL,
    LAND
}

enum class PropertyType(val category: PropertyCategory, val titleEn: String, val titleSw: String) {
    HOUSE(PropertyCategory.RESIDENTIAL, "House", "Nyumba"),
    APARTMENT(PropertyCategory.RESIDENTIAL, "Apartment", "Ghorofa / Apartimenti"),
    ROOM(PropertyCategory.RESIDENTIAL, "Single Room", "Chumba"),
    HOSTEL(PropertyCategory.RESIDENTIAL, "Hostel", "Hosteli"),
    SHOP(PropertyCategory.COMMERCIAL, "Shop / Retail", "Duka / Fremu"),
    OFFICE(PropertyCategory.COMMERCIAL, "Office Space", "Ofisi"),
    WAREHOUSE(PropertyCategory.COMMERCIAL, "Warehouse", "Stoo / Godown"),
    COMMERCIAL_BUILDING(PropertyCategory.COMMERCIAL, "Commercial Building", "Jengo la Biashara"),
    RESIDENTIAL_LAND(PropertyCategory.LAND, "Residential Plot", "Kiwanja cha Makazi"),
    COMMERCIAL_LAND(PropertyCategory.LAND, "Commercial Plot", "Kiwanja cha Biashara"),
    AGRICULTURAL_LAND(PropertyCategory.LAND, "Agricultural Land", "Shamba"),
    INDUSTRIAL_LAND(PropertyCategory.LAND, "Industrial Land", "Ardhi ya Viwanda")
}

enum class TransactionType(val titleEn: String, val titleSw: String) {
    RENT("Rent", "Kupanga"),
    SALE("Sale", "Kuuza"),
    LEASE("Lease", "Kukodisha")
}

// ============================================================
// 3. STATUS SEPARATION (CRITICAL MANDATE)
// ============================================================
enum class ListingStatus {
    DRAFT,
    IN_PROGRESS,
    READY_FOR_REVIEW,
    SUBMITTED,
    UNDER_REVIEW,
    APPROVED,
    PUBLISHED,
    PAUSED,
    REJECTED,
    SUSPENDED,
    ARCHIVED
}

enum class VerificationStatus {
    NOT_STARTED,
    PENDING,
    IN_REVIEW,
    PARTIALLY_VERIFIED,
    VERIFIED,
    NEEDS_MORE_INFORMATION,
    REJECTED,
    SUSPENDED
}

enum class AvailabilityStatus {
    AVAILABLE,
    RESERVED,
    TRANSACTION_PENDING,
    RENTED,
    SOLD,
    LEASED,
    UNAVAILABLE
}

enum class LocationPrivacy {
    EXACT,        // Full street and coordinate pin visible
    APPROXIMATE,  // Obfuscated within 500m-1km zone
    HIDDEN        // District level only until assisted visit
}

// ============================================================
// 4. PROPERTY SPECIFICATION & ENTITY
// ============================================================
enum class PropertyMediaType {
    PHOTO,
    STANDARD_VIDEO,
    PROPERTY_SHORT,
    VIRTUAL_TOUR,
    PANORAMIC_360
}

enum class MediaProcessingStatus {
    LOCAL,
    UPLOADING,
    UPLOADED,
    PROCESSING,
    READY,
    FAILED,
    REJECTED
}

data class PropertyMediaItem(
    val id: String = UUID.randomUUID().toString(),
    val type: PropertyMediaType = PropertyMediaType.PHOTO,
    val localUri: String? = null,
    val remoteUrl: String,
    val thumbnailUrl: String = remoteUrl,
    val caption: String = "",
    val isCover: Boolean = false,
    val durationSeconds: Int = 0,
    val sizeMb: Float = 1.8f,
    val status: MediaProcessingStatus = MediaProcessingStatus.READY,
    val qualityWarning: String? = null
)

data class VirtualTourSection(
    val id: String = UUID.randomUUID().toString(),
    val roomName: String,
    val mediaUrl: String,
    val durationSeconds: Int = 12,
    val notes: String = "",
    val isCompleted: Boolean = true
)

data class PropertyVerificationDocument(
    val id: String = UUID.randomUUID().toString(),
    val documentType: String, // e.g. "Title Deed / Hati ya Ardhi", "Local Gov Letter", "NIDA ID"
    val documentName: String,
    val fileUri: String,
    val isVerified: Boolean = false,
    val uploadedAt: Long = System.currentTimeMillis()
)

data class VirtualScene(
    val id: String,
    val roomName: String,
    val description: String,
    val panoramaColorHex: Long = 0xFF0A192F,
    val hotspotTargetSceneId: String? = null
)

data class Property(
    val id: String, // Authoritative ID: DOP-TZA-DAR-000184
    val title: String,
    val description: String,
    val priceTzs: Long,
    val transactionType: TransactionType,
    val propertyType: PropertyType,
    val region: String, // e.g. Dar es Salaam
    val district: String, // e.g. Kinondoni
    val ward: String, // e.g. Masaki, Mikocheni
    val locationPrivacy: LocationPrivacy = LocationPrivacy.APPROXIMATE,
    val bedrooms: Int = 0,
    val bathrooms: Int = 0,
    val areaSqm: Int = 0,
    val isDopExpress: Boolean = false,
    val hasVirtualTour: Boolean = false,
    val hasLiveViewing: Boolean = false,
    val listingStatus: ListingStatus = ListingStatus.PUBLISHED,
    val verificationStatus: VerificationStatus = VerificationStatus.VERIFIED,
    val availabilityStatus: AvailabilityStatus = AvailabilityStatus.AVAILABLE,
    val verificationBadges: List<String> = listOf("Identity Reviewed", "Property Reviewed", "Location Checked"),
    val amenities: List<String> = listOf("Luku Meter", "DAWASA Water", "Fenced Compound", "Parking"),
    val images: List<String> = listOf(),
    val mediaItems: List<PropertyMediaItem> = emptyList(),
    val virtualTourSections: List<VirtualTourSection> = emptyList(),
    val verificationDocuments: List<PropertyVerificationDocument> = emptyList(),
    val videoTourUrl: String? = null,
    val virtualScenes: List<VirtualScene> = emptyList(),
    val ownerId: String = "OWN-882",
    val ownerName: String = "Mama Halima Mgaza",
    val ownerPhone: String = "+255 713 *** ***", // Masked
    val depositMonths: Int = 1,
    val boostTier: ListingBoostTier = ListingBoostTier.STANDARD,
    val isBoosted: Boolean = false,
    val privateCaretakerName: String? = null,
    val privateCaretakerPhone: String? = null,
    val privateAccessInstructions: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Draft state for the Owner Property Creation Studio.
 * Supports background autosave, recovery, and step progress.
 */
data class PropertyDraft(
    val draftId: String = "DRAFT-${UUID.randomUUID().toString().take(6).uppercase()}",
    val ownerId: String = "OWN-TZA-0882",
    val lastSavedTimestamp: Long = System.currentTimeMillis(),
    val currentStep: Int = 1,
    val totalSteps: Int = 13,
    val category: PropertyCategory = PropertyCategory.RESIDENTIAL,
    val propertyType: PropertyType = PropertyType.APARTMENT,
    val transactionType: TransactionType = TransactionType.RENT,
    val title: String = "",
    val tagline: String = "",
    val description: String = "",
    val propertyCondition: String = "Good Condition",
    val furnishedStatus: String = "Unfurnished",
    val areaSqm: Int = 120,
    val floorOrUnits: String = "",
    // Rooms & Features (Dynamic)
    val bedrooms: Int = 2,
    val bathrooms: Int = 2,
    val livingRooms: Int = 1,
    val kitchens: Int = 1,
    val parkingSpaces: Int = 1,
    // Land specifics
    val roadAccess: String = "Tarmac Road",
    val topography: String = "Flat",
    val soilType: String = "Loam",
    val isFenced: Boolean = true,
    // Amenities
    val amenities: List<String> = listOf("LUKU / Umeme wa Uhuru", "Maji ya DAWASA", "Uzio / Fencing"),
    val customAmenities: List<String> = emptyList(),
    // Location & Privacy
    val region: String = "Dar es Salaam",
    val district: String = "Kinondoni",
    val ward: String = "Masaki",
    val street: String = "",
    val landmark: String = "",
    val locationPrivacy: LocationPrivacy = LocationPrivacy.APPROXIMATE,
    // Media items
    val mediaItems: List<PropertyMediaItem> = emptyList(),
    val virtualTourSections: List<VirtualTourSection> = emptyList(),
    // Verification documents
    val verificationDocuments: List<PropertyVerificationDocument> = emptyList(),
    // Pricing
    val priceTzs: Long = 1000000L,
    val depositMonths: Int = 1,
    // Availability
    val availabilityStatus: AvailabilityStatus = AvailabilityStatus.AVAILABLE,
    val availableFromDate: String = "Sasa Hivi / Immediate",
    // Viewing settings & Private access
    val selfVisitEnabled: Boolean = true,
    val assistedVisitEnabled: Boolean = true,
    val viewingDaysHours: String = "Mon-Sat 9:00 AM - 5:00 PM",
    val caretakerName: String = "",
    val caretakerPhone: String = "",
    val privateAccessNotes: String = "",
    // Virtual viewing settings
    val virtualViewEnabled: Boolean = true,
    val isDopExpress: Boolean = true,
    val isSubmitted: Boolean = false
) {
    val completenessScore: Int
        get() {
            var score = 15
            if (title.isNotBlank()) score += 15
            if (description.isNotBlank()) score += 10
            if (priceTzs > 0) score += 15
            if (mediaItems.isNotEmpty()) score += 20
            if (ward.isNotBlank() && district.isNotBlank()) score += 15
            if (verificationDocuments.isNotEmpty()) score += 10
            return score.coerceAtMost(100)
        }
}

// ============================================================
// 4B. LISTING BOOST TIERS (MONETIZATION FOR LANDLORDS / DALALIS)
// ============================================================
enum class ListingBoostTier(val titleSw: String, val priceTzs: Long, val durationDays: Int, val multiplierBadge: String) {
    STANDARD("Bure / Standard", 0L, 0, "1x"),
    SILVER("Silver Boost (Juu ya Kata)", 15000L, 14, "3x Views"),
    GOLD("Gold Spotlight (Juu ya Mkoa)", 35000L, 30, "7x Views"),
    DIAMOND("Diamond Enterprise (Homepage VIP)", 75000L, 60, "15x VIP")
}

// ============================================================
// 5. VIEWING SYSTEM & STATE MACHINE
// ============================================================
enum class ViewingType {
    SELF_VISIT,         // TSh 0 (Free)
    DOP_ASSISTED_VISIT, // TSh 5,000 (TSh 2,500 Guide + TSh 2,500 DoP)
    LIVE_VIRTUAL_VIEW   // Online guided walkthrough
}

enum class ViewingStatus {
    REQUESTED,
    PENDING_OWNER_CONFIRMATION,
    CONFIRMED,
    GUIDE_SEARCHING,
    GUIDE_ASSIGNED,
    GUIDE_ACCEPTED,
    GUIDE_ON_THE_WAY,
    GUIDE_ARRIVED,
    VIEWING_IN_PROGRESS,
    COMPLETED,
    RESCHEDULED,
    CANCELLED,
    DECLINED,
    EXPIRED
}

data class ViewingBooking(
    val bookingCode: String, // DOP-BK-9182
    val propertyId: String,
    val propertyTitle: String,
    val propertyLocation: String,
    val customerId: String,
    val customerName: String,
    val customerPhone: String = "+255 712 345 678",
    val viewingType: ViewingType,
    val scheduledDate: String,
    val scheduledTimeSlot: String,
    val meetingPoint: String,
    val status: ViewingStatus,
    val guideId: String? = null,
    val guideName: String? = null,
    val guidePhone: String? = null,
    val feeTzs: Long = 0,
    val guidePayoutTzs: Long = 0,
    val dopPlatformFeeTzs: Long = 0,
    val customerNotes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

// ============================================================
// 6. RENTAL APPLICATION
// ============================================================
enum class ApplicationStatus {
    SUBMITTED,
    UNDER_REVIEW,
    APPROVED,
    DECLINED,
    WITHDRAWN,
    CONVERTED_TO_TRANSACTION
}

data class RentalApplication(
    val id: String = "DOP-APP-${UUID.randomUUID().toString().take(6).uppercase()}",
    val propertyId: String,
    val propertyTitle: String,
    val applicantId: String,
    val applicantName: String,
    val applicantPhone: String,
    val moveInDate: String,
    val rentalDurationMonths: Int = 6,
    val numberOfOccupants: Int = 2,
    val occupation: String,
    val monthlyIncomeTzs: Long = 0,
    val applicantNotes: String = "",
    val status: ApplicationStatus = ApplicationStatus.SUBMITTED,
    val submittedAt: Long = System.currentTimeMillis()
)

// ============================================================
// 7. TENANCY AGREEMENT & MUTUAL ACCEPTANCE
// ============================================================
enum class TenancyAgreementStatus {
    DRAFT,
    PREPARED,
    PENDING_CUSTOMER_ACCEPTANCE,
    CUSTOMER_ACCEPTED,
    PENDING_OWNER_ACCEPTANCE,
    OWNER_ACCEPTED,
    MUTUALLY_ACCEPTED,
    LOCKED,
    CANCELLED,
    EXPIRED
}

data class TenancyAgreement(
    val agreementCode: String = "DOP-AGR-5021",
    val propertyId: String,
    val propertyTitle: String,
    val propertyLocation: String,
    val ownerId: String,
    val ownerName: String,
    val tenantId: String,
    val tenantName: String,
    val monthlyRentTzs: Long,
    val durationMonths: Int,
    val depositTzs: Long,
    val dopPlatformFeeTzs: Long, // 50% of first month's rent ONLY
    val totalInitialPayableTzs: Long,
    val status: TenancyAgreementStatus = TenancyAgreementStatus.PENDING_CUSTOMER_ACCEPTANCE,
    val version: Int = 1,
    val customerAcceptedAt: Long? = null,
    val ownerAcceptedAt: Long? = null,
    val lockedAt: Long? = null,
    val termsSummary: String = "Standard DoP Tenancy Protocol under Tanzanian Law. Rent payable strictly to verified escrow/landlord bank account."
)

// ============================================================
// 8. REALTIME NOTIFICATION & SYNC EVENT
// ============================================================
data class DopNotification(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val relatedEntityId: String? = null
)

// ============================================================
// 9. REVENUE, MONETIZATION & MOBILE MONEY INTEGRATION
// ============================================================
enum class RevenueSource(val labelSw: String, val labelEn: String) {
    TENANCY_COMMISSION("Ada ya Mkataba (50% Mwezi wa 1)", "Tenancy Commission (50% 1st Month)"),
    VIEWING_FEE("Ada ya Ziara ya DoP (TSh 2,500)", "Viewing Dispatch Royalty (TSh 2,500)"),
    LISTING_BOOST("Kutangaza Tangazo (Listing Boost)", "Listing Promotion Boost"),
    TITLE_DEED_SEARCH("Uhakiki wa Hati Miliki (Ardhi)", "Title Deed Verification Search"),
    WITHDRAWAL_PAYOUT("Kutoa Fedha Kwenda M-Pesa", "Withdrawal to M-Pesa Payout")
}

enum class PaymentMethod(val title: String, val operatorName: String, val prefixHint: String) {
    M_PESA("Vodacom M-Pesa", "Vodacom Tanzania", "074x / 075x / 076x"),
    TIGO_PESA("Tigo Pesa (Mix)", "Yas / Tigo Tanzania", "071x / 065x / 067x"),
    AIRTEL_MONEY("Airtel Money", "Airtel Tanzania", "078x / 068x / 069x"),
    HALOPESA("Halopesa", "Halotel Tanzania", "062x / 061x"),
    CRDB_BANK("CRDB Bank (SimBanking)", "CRDB Bank Plc", "Akaunti / Card"),
    NMB_BANK("NMB Bank (NMB Mkononi)", "NMB Bank Plc", "Akaunti / Card")
}

enum class PaymentStatus {
    PENDING_USSD_PUSH,
    COMPLETED,
    FAILED,
    REVERSED
}

data class WalletTransaction(
    val id: String = "TRX-${UUID.randomUUID().toString().take(8).uppercase()}",
    val referenceNo: String, // e.g. MPESA-QZ88912X
    val amountTzs: Long,
    val payerName: String,
    val payerPhone: String,
    val method: PaymentMethod,
    val source: RevenueSource,
    val isCreditToPlatform: Boolean, // true = Inflow to DoP revenue, false = Payout/Outflow
    val status: PaymentStatus = PaymentStatus.COMPLETED,
    val description: String,
    val efdReceiptCode: String = "TRA-EFD-${(100000..999999).random()}",
    val timestamp: Long = System.currentTimeMillis()
)

enum class TitleVerificationStatus {
    SUBMITTED,
    IN_SEARCH_MINISTRY,
    VERIFIED_CLEAN,
    ENCUMBERED_OR_DISPUTED,
    REJECTED
}

data class TitleDeedVerificationOrder(
    val orderId: String = "ARDHI-SRCH-${(1000..9999).random()}",
    val propertyId: String,
    val propertyTitle: String,
    val applicantName: String,
    val applicantPhone: String,
    val plotNumber: String,
    val blockNumber: String,
    val titleNumber: String,
    val feeTzs: Long = 25000,
    val status: TitleVerificationStatus = TitleVerificationStatus.VERIFIED_CLEAN,
    val registryNotes: String = "Hati imethibitishwa rasmi na Wizara ya Ardhi, Nyumba na Maendeleo ya Makazi. Haina mgogoro wala dhamana ya benki.",
    val submittedAt: Long = System.currentTimeMillis()
)

data class PlatformFinancialSummary(
    val totalGrossRevenueTzs: Long,
    val totalCommissionTzs: Long,
    val totalViewingFeesTzs: Long,
    val totalBoostRevenueTzs: Long,
    val totalVerificationRevenueTzs: Long,
    val availableBalanceTzs: Long,
    val totalWithdrawnTzs: Long,
    val pendingEscrowTzs: Long
)

// ============================================================
// 10. PROPERTY SHORTS (TIKTOK / YOUTUBE SHORTS REELS SYSTEM)
// ============================================================
data class ShortComment(
    val id: String = "CMT-${UUID.randomUUID().toString().take(6).uppercase()}",
    val userName: String,
    val userRole: String,
    val comment: String,
    val timeAgo: String = "Hivi punde"
)

data class PropertyShort(
    val id: String = "SHT-${UUID.randomUUID().toString().take(6).uppercase()}",
    val propertyId: String,
    val propertyTitle: String,
    val ownerName: String,
    val ownerPhone: String,
    val ownerAvatarUrl: String = "",
    val location: String,
    val priceDisplay: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val description: String,
    val roomHighlights: List<String> = listOf("Sebule Kubwa", "Tiles Mpya", "Maji DAWASA"),
    val tags: List<String> = listOf("#DarEsSalaam", "#NyumbaZaKupanga", "#DoPShorts"),
    val likesCount: Int = 120,
    val isLiked: Boolean = false,
    val viewsCount: Int = 1450,
    val commentsCount: Int = 14,
    val comments: List<ShortComment> = emptyList(),
    val createdAt: String = "Saa chache zilizopita"
)

// ============================================================
// 11. GUIDE & CUSTOMER DIRECT CHAT SYSTEM
// ============================================================
enum class ChatAttachmentType {
    NONE,
    LOCATION_PIN,
    PHOTO_PREVIEW,
    QUICK_NOTE
}

data class GuideChatMessage(
    val id: String = "MSG-${UUID.randomUUID().toString().take(6).uppercase()}",
    val bookingCode: String,
    val propertyTitle: String,
    val senderRole: UserRole,
    val senderName: String,
    val messageText: String,
    val timestamp: String,
    val isRead: Boolean = true,
    val attachmentType: ChatAttachmentType = ChatAttachmentType.NONE,
    val attachmentData: String? = null
)



data class GuideEarning(
    val id: String = UUID.randomUUID().toString(),
    val jobId: String,
    val amountTzs: Long = 2500L,
    val date: Long = System.currentTimeMillis(),
    val status: GuideEarningStatus = GuideEarningStatus.PENDING
)

data class GuideIncident(
    val id: String = UUID.randomUUID().toString(),
    val jobId: String,
    val category: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "UNDER_REVIEW"
)

enum class LiveViewingStatus {
    REQUESTED,
    GUIDE_ASSIGNED,
    SCHEDULED,
    READY,
    LIVE,
    ENDED,
    CANCELLED
}

data class LiveViewingSession(
    val id: String = UUID.randomUUID().toString(),
    val propertyId: String,
    val guideId: String,
    val customerId: String,
    val status: LiveViewingStatus = LiveViewingStatus.REQUESTED,
    val scheduledTime: Long = System.currentTimeMillis()
)
