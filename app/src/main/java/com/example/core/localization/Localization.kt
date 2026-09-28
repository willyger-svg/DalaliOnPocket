package com.example.core.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import java.text.NumberFormat
import java.util.Locale

enum class AppLanguage(
    val code: String,
    val nativeName: String,
    val description: String,
    val flag: String
) {
    SWAHILI("sw", "Kiswahili", "Kiswahili Fasaha na Sanifu", "🇹🇿"),
    ENGLISH("en", "English", "Standard Fluent English", "🌍")
}

val LocalAppLanguage = compositionLocalOf { AppLanguage.SWAHILI }

/**
 * Interface defining every user-facing string in Dalalion Pocket.
 * Strictly guarantees ZERO language mixing:
 * - Kiswahili is 100% pure, natural, professional Swahili without English loanwords or parentheses.
 * - English is 100% pure, fluent, professional English.
 */
interface AppStrings {
    // App Brand & Top Bar
    val appTitle: String
    val appTagline: String
    val syncOnline: String
    val syncOffline: String
    val syncStatusConnected: String
    val syncStatusDisconnected: String
    val chatTitle: String
    val notificationsTitle: String

    // Language Settings Modal
    val languageDialogTitle: String
    val languageDialogSubtitle: String
    val languageSwahiliTitle: String
    val languageSwahiliDesc: String
    val languageEnglishTitle: String
    val languageEnglishDesc: String
    val languageActiveBadge: String
    val languageConfirmButton: String
    val languageCloseButton: String

    // Navigation Tabs - Customer
    val navHome: String
    val navExplore: String
    val navShorts: String
    val navVisits: String
    val navAccount: String

    // Navigation Tabs - Owner
    val navOwnerDashboard: String
    val navOwnerProperties: String
    val navOwnerAdd: String
    val navOwnerViewings: String

    // Navigation Tabs - Guide
    val navGuideDashboard: String
    val navGuideJobs: String
    val navGuideSchedule: String
    val navGuideEarnings: String

    // User Roles & Capability Workspace
    val roleCustomerTitle: String
    val roleCustomerDesc: String
    val roleOwnerTitle: String
    val roleOwnerDesc: String
    val roleGuideTitle: String
    val roleGuideDesc: String
    val roleAdminTitle: String
    val roleAdminDesc: String
    val roleSwitchButton: String
    val roleCurrentActive: String
    val roleVerifiedBadge: String
    val roleUnverifiedBadge: String
    val rolePendingApproval: String

    // Home & Discovery
    val heroHeadline: String
    val heroSubhead: String
    val searchPlaceholder: String
    val categoryAll: String
    val categoryHouse: String
    val categoryApartment: String
    val categoryRoom: String
    val categoryCommercial: String
    val categoryLand: String
    val shortsSectionTitle: String
    val shortsViewAll: String
    val expressSectionTitle: String
    val expressSectionSubtitle: String
    val virtualTourBannerTitle: String
    val virtualTourBannerDesc: String
    val virtualTourButton: String
    val featuredSectionTitle: String

    // Pricing & Specs
    val perMonth: String
    val totalSalePrice: String
    val bedroomsLabel: String
    val bathroomsLabel: String
    val areaLabel: String
    val selfVisitTitle: String
    val selfVisitCost: String
    val assistedVisitTitle: String
    val assistedVisitCost: String
    val assistedVisitDesc: String

    // Property Details
    val detailsTitle: String
    val backButton: String
    val saveButton: String
    val savedButton: String
    val shareButton: String
    val propertyDescriptionHeader: String
    val amenitiesHeader: String
    val locationHeader: String
    val ownerCardHeader: String
    val guideCardHeader: String
    val bookViewingAction: String
    val applyToRentAction: String
    val viewAgreementAction: String
    val contactGuideAction: String
    val callAction: String

    // Booking & Rental Application Dialogs
    val bookViewingModalTitle: String
    val selectViewingTypeLabel: String
    val selectDateLabel: String
    val selectTimeSlotLabel: String
    val meetingPointLabel: String
    val bookingNotesLabel: String
    val confirmBookingButton: String
    val rentalApplicationTitle: String
    val moveInDateLabel: String
    val leaseDurationLabel: String
    val occupantsCountLabel: String
    val occupationLabel: String
    val monthlyIncomeLabel: String
    val submitApplicationButton: String

    // Tenancy Agreement
    val tenancyAgreementHeader: String
    val agreementStatusActive: String
    val agreementStatusPending: String
    val agreementStatusLocked: String
    val landlordLabel: String
    val tenantLabel: String
    val monthlyRentLabel: String
    val securityDepositLabel: String
    val acceptAndSignButton: String
    val commissionPaidBadge: String
    val payCommissionButton: String

    // Payment Checkout
    val paymentTitle: String
    val paymentSubtitle: String
    val paymentAmountLabel: String
    val paymentSelectMethod: String
    val paymentPhoneLabel: String
    val paymentPhonePlaceholder: String
    val paymentUssdPromptSent: String
    val paymentUssdInstructions: String
    val payNowButton: String
    val paymentCancelButton: String
    val paymentSuccessTitle: String
    val paymentSuccessDesc: String

    // Owner Dashboard & Actions
    val ownerDashboardTitle: String
    val ownerPropertiesCount: String
    val ownerNewApplications: String
    val ownerActiveLeases: String
    val ownerAddNewProperty: String
    val ownerManageListings: String
    val ownerDraftResumeTitle: String
    val ownerDraftResumeButton: String
    val ownerPostShortButton: String
    val ownerBoostPropertyButton: String

    // Guide Dashboard & Missions
    val guideDashboardTitle: String
    val guideAvailableToggle: String
    val guideAvailableDesc: String
    val guideUnavailableDesc: String
    val guideTodayHeader: String
    val guideNewJobsCount: String
    val guideUpcomingCount: String
    val guideCompletedCount: String
    val guideEarningsTodayHeader: String
    val guidePendingPayout: String
    val guidePerformanceHeader: String
    val guideRatingLabel: String
    val guideResponseLabel: String
    val guideCompletedVisitsLabel: String
    val guideAcceptMissionButton: String
    val guideArrivedButton: String
    val guideStartInspectionButton: String
    val guideCompleteMissionButton: String
    val guideReportIssueButton: String

    // Account & Settings
    val accountTitle: String
    val profileSectionHeader: String
    val rolesSectionHeader: String
    val switchRoleMenuTitle: String
    val switchRoleMenuSubtitle: String
    val capabilitiesMenuTitle: String
    val capabilitiesMenuSubtitle: String
    val verificationMenuTitle: String
    val verificationMenuVerifiedSubtitle: String
    val verificationMenuUnverifiedSubtitle: String
    val liveSyncMenuTitle: String
    val liveSyncActiveSubtitle: String
    val liveSyncOfflineSubtitle: String
    val systemSettingsHeader: String
    val languageMenuTitle: String
    val languageMenuSubtitle: String
    val notificationsMenuTitle: String
    val notificationsMenuSubtitle: String
    val securityMenuTitle: String
    val securityMenuSubtitle: String
    val supportMenuTitle: String
    val supportMenuSubtitle: String
    val aboutMenuTitle: String
    val aboutMenuSubtitle: String
    val termsMenuTitle: String
    val termsMenuSubtitle: String
    val logoutMenuTitle: String
    val logoutDialogTitle: String
    val logoutDialogMessage: String
    val logoutConfirmButton: String
    val logoutCancelButton: String

    // Dialogs: Verification, Support, About, Terms
    val verificationDialogTitle: String
    val verificationDialogDesc: String
    val supportDialogTitle: String
    val supportDialogDesc: String
    val aboutDialogTitle: String
    val aboutDialogDesc: String
    val termsDialogTitle: String
    val dialogCloseButton: String
    val dialogCancelButton: String

    // Auth & Login
    val loginWelcomeTitle: String
    val loginWelcomeSubtitle: String
    val loginPhoneLabel: String
    val loginPhonePlaceholder: String
    val loginPasswordLabel: String
    val loginPasswordPlaceholder: String
    val loginForgotPassword: String
    val loginSubmitButton: String
    val loginNoAccountPrompt: String
    val loginSignUpAction: String
    val loginLanguageSwitcherLabel: String
    val loginTermsNote: String
    val loginStarterAccountsTitle: String
    val loginStarterRoleBadge: String
    val loginSelectAccount: String

    // Forgot Password Dialog
    val forgotPasswordTitle: String
    val forgotPasswordDesc: String
    val forgotPasswordSubmitButton: String
    val forgotPasswordSuccessMsg: String

    // Sign Up
    val signUpTitle: String
    val signUpSubtitle: String
    val signUpBaseAccountBannerTitle: String
    val signUpBaseAccountBannerDesc: String
    val signUpFullNameLabel: String
    val signUpFullNamePlaceholder: String
    val signUpPhoneLabel: String
    val signUpPhonePlaceholder: String
    val signUpEmailLabel: String
    val signUpEmailPlaceholder: String
    val signUpPasswordLabel: String
    val signUpPasswordPlaceholder: String
    val signUpConfirmPasswordLabel: String
    val signUpConfirmPasswordPlaceholder: String
    val signUpTermsAgreement: String
    val signUpSubmitButton: String
    val signUpAlreadyHaveAccount: String
    val signUpSignInAction: String

    // Onboarding
    val onboardingSkip: String
    val onboardingNext: String
    val onboardingGetStarted: String
    val onboardingAlreadyHaveAccount: String
    val onboardingSlide1Title: String
    val onboardingSlide1Desc: String
    val onboardingSlide2Title: String
    val onboardingSlide2Desc: String
    val onboardingSlide3Title: String
    val onboardingSlide3Desc: String
}

/**
 * 100% Pure, Fluent Swahili Implementation.
 * Natural, professional Swahili with zero English mixing.
 */
object SwahiliStrings : AppStrings {
    override val appTitle = "Dalalion Pocket"
    override val appTagline = "Mfumo Rasmi wa Kidijitali wa Mali Tanzania"
    override val syncOnline = "Mtandaoni"
    override val syncOffline = "Nje ya Mtandao"
    override val syncStatusConnected = "Imeunganishwa Moja kwa Moja"
    override val syncStatusDisconnected = "Hali ya Nje ya Mtandao"
    override val chatTitle = "Mawasiliano"
    override val notificationsTitle = "Arifa"

    override val languageDialogTitle = "Chagua Lugha ya Programu"
    override val languageDialogSubtitle = "Badilisha lugha ya matumizi katika programu nzima papo hapo bila kuchanganya maneno."
    override val languageSwahiliTitle = "Kiswahili"
    override val languageSwahiliDesc = "Lugha sanifu na fasaha ya Kiswahili kwa huduma zote"
    override val languageEnglishTitle = "Kiingereza"
    override val languageEnglishDesc = "Lugha rasmi ya Kiingereza kwa huduma zote"
    override val languageActiveBadge = "Inatumika Sasa"
    override val languageConfirmButton = "Hifadhi Lugha"
    override val languageCloseButton = "Funga"

    override val navHome = "Mwanzo"
    override val navExplore = "Tafuta"
    override val navShorts = "Video"
    override val navVisits = "Ziara Zangu"
    override val navAccount = "Akaunti"

    override val navOwnerDashboard = "Dashibodi"
    override val navOwnerProperties = "Mali Zangu"
    override val navOwnerAdd = "Weka Mali"
    override val navOwnerViewings = "Miadi ya Ziara"

    override val navGuideDashboard = "Dashibodi"
    override val navGuideJobs = "Kazi Mpya"
    override val navGuideSchedule = "Ratiba"
    override val navGuideEarnings = "Mapato"

    override val roleCustomerTitle = "Mteja Mpangaji au Mnunuzi"
    override val roleCustomerDesc = "Kutafuta nyumba, kuomba ziara za ukaguzi na kusaini mikataba ya upangaji."
    override val roleOwnerTitle = "Mwenye Nyumba au Mali"
    override val roleOwnerDesc = "Kusimamia nyumba zilizopo sokoni, kuweka video fupi na kupokea maombi ya wateja."
    override val roleGuideTitle = "Mwongoza Ziara Rasmi"
    override val roleGuideDesc = "Kuongoza wateja kwenye ziara za nyumba na kuingiza kipato cha uhakika."
    override val roleAdminTitle = "Msimamizi wa Mfumo"
    override val roleAdminDesc = "Usimamizi mkuu na uendeshaji wa mfumo wa Dalalion Pocket."
    override val roleSwitchButton = "Badilisha Nafasi ya Kazi"
    override val roleCurrentActive = "Nafasi Inayotumika Sasa"
    override val roleVerifiedBadge = "Imethibitishwa"
    override val roleUnverifiedBadge = "Haijathibitishwa"
    override val rolePendingApproval = "Inasubiri Idhini"

    override val heroHeadline = "Pata Makazi Yako ya Ndoto"
    override val heroSubhead = "Soko la kwanza la kidijitali la nyumba, vyumba, viwanja na maeneo ya biashara nchini Tanzania."
    override val searchPlaceholder = "Tafuta eneo, kata, jiji au aina ya nyumba..."
    override val categoryAll = "Zote"
    override val categoryHouse = "Nyumba"
    override val categoryApartment = "Apartimenti"
    override val categoryRoom = "Vyumba"
    override val categoryCommercial = "Biashara"
    override val categoryLand = "Viwanja"
    override val shortsSectionTitle = "Klipu za Video za Ndani ya Nyumba"
    override val shortsViewAll = "Tazama Zote"
    override val expressSectionTitle = "Ziara za Haraka za Moja kwa Moja"
    override val expressSectionSubtitle = "Nyumba zilizokaguliwa na zilizo tayari kuonwa muda huu"
    override val virtualTourBannerTitle = "ZIARA YA PICHA ZA 360°"
    override val virtualTourBannerDesc = "Tembelea na kagua kila chumba kidijitali bila kusafiri"
    override val virtualTourButton = "Kagua kwa 360°"
    override val featuredSectionTitle = "Mali Maalumu Zilizopendekezwa"

    override val perMonth = "/mwezi"
    override val totalSalePrice = "Bei ya Jumla ya Mauzo"
    override val bedroomsLabel = "Vyumba vya Kulala"
    override val bathroomsLabel = "Vyoo na Mabafu"
    override val areaLabel = "Ukubwa wa Eneo"
    override val selfVisitTitle = "Kutembelea Mwenyewe"
    override val selfVisitCost = "TSh 0 (Bure Kabisa)"
    override val assistedVisitTitle = "Ziara ya Kuongozwa na Dalali Rasmi"
    override val assistedVisitCost = "TSh 5,000"
    override val assistedVisitDesc = "Dalali mwongozaji anapokea TSh 2,500 na mfumo unapokea TSh 2,500"

    override val detailsTitle = "Taarifa za Mali"
    override val backButton = "Rudi"
    override val saveButton = "Hifadhi"
    override val savedButton = "Imehifadhiwa"
    override val shareButton = "Sambaza"
    override val propertyDescriptionHeader = "Maelezo ya Kina ya Mali"
    override val amenitiesHeader = "Vifaa na Huduma za Msingi"
    override val locationHeader = "Mahali Ilipo na Ramani"
    override val ownerCardHeader = "Mmiliki wa Mali"
    override val guideCardHeader = "Mwongoza Ziara Aliyepangiwa"
    override val bookViewingAction = "Weka Miadi ya Ziara"
    override val applyToRentAction = "Tuma Ombi la Kupanga"
    override val viewAgreementAction = "Kagua Mkataba wa Upangaji"
    override val contactGuideAction = "Mwandikie Mwongozaji"
    override val callAction = "Piga Simu"

    override val bookViewingModalTitle = "Weka Miadi ya Ziara Eneo la Nyumba"
    override val selectViewingTypeLabel = "Aina ya Ziara ya Ukaguzi"
    override val selectDateLabel = "Tarehe ya Ziara"
    override val selectTimeSlotLabel = "Saa ya Ziara"
    override val meetingPointLabel = "Kituo cha Makutano"
    override val bookingNotesLabel = "Maelezo ya Ziada (Hiari)"
    override val confirmBookingButton = "Thibitisha Miadi ya Ziara"
    override val rentalApplicationTitle = "Ombi Rasmi la Upangaji"
    override val moveInDateLabel = "Tarehe ya Kuanza Kuishi"
    override val leaseDurationLabel = "Muda wa Mkataba (Miezi)"
    override val occupantsCountLabel = "Idadi ya Wakazi"
    override val occupationLabel = "Kazi au Shughuli Yako"
    override val monthlyIncomeLabel = "Kipato cha Mwezi (TSh)"
    override val submitApplicationButton = "Wasilisha Ombi la Upangaji"

    override val tenancyAgreementHeader = "Mkataba Rasmi wa Upangaji"
    override val agreementStatusActive = "Mkataba Unafanya Kazi"
    override val agreementStatusPending = "Inasubiri Saini Zote"
    override val agreementStatusLocked = "Mkataba Umefungwa Kikamilifu"
    override val landlordLabel = "Mwenye Nyumba (Mpangishaji)"
    override val tenantLabel = "Mpangaji"
    override val monthlyRentLabel = "Kodi ya Mwezi"
    override val securityDepositLabel = "Amana ya Dhamana"
    override val acceptAndSignButton = "Kubali na Saini Mkataba"
    override val commissionPaidBadge = "Ada ya Huduma Imelipwa"
    override val payCommissionButton = "Lipa Ada ya Huduma ya Mkataba"

    override val paymentTitle = "Kamilisha Malipo Salama"
    override val paymentSubtitle = "Malipo ya moja kwa moja kupitia mtandao wa simu"
    override val paymentAmountLabel = "Kiasi Kinacholipwa"
    override val paymentSelectMethod = "Chagua Mtandao au Benki ya Kulipia:"
    override val paymentPhoneLabel = "Namba ya Simu ya Malipo"
    override val paymentPhonePlaceholder = "Mfano: 0754 123 456 au +255..."
    override val paymentUssdPromptSent = "Ombi la Malipo Limetumwa Kwenye Simu!"
    override val paymentUssdInstructions = "Tafadhali weka nenosiri la siri kwenye simu yako ili kuidhinisha muamala huu kwenda Dalalion Pocket."
    override val payNowButton = "Lipa Sasa Moja kwa Moja"
    override val paymentCancelButton = "Ghairi"
    override val paymentSuccessTitle = "Malipo Yamekamilika Kikamilifu"
    override val paymentSuccessDesc = "Muamala wako umethibitishwa na risiti imehifadhiwa kwenye akaunti yako."

    override val ownerDashboardTitle = "Dashibodi ya Mwenye Mali"
    override val ownerPropertiesCount = "Mali Zako"
    override val ownerNewApplications = "Maombi Mapya"
    override val ownerActiveLeases = "Mikataba Inayoendelea"
    override val ownerAddNewProperty = "Weka Mali Mpya"
    override val ownerManageListings = "Simamia Mali Zote"
    override val ownerDraftResumeTitle = "Kuna Mali Ambayo Haijakamilika Kuingizwa"
    override val ownerDraftResumeButton = "Endelea Kujaza"
    override val ownerPostShortButton = "Weka Video Fupi ya Nyumba"
    override val ownerBoostPropertyButton = "Tangaza Mali Juu Zaidi"

    override val guideDashboardTitle = "Dashibodi ya Dalali Mwongozaji"
    override val guideAvailableToggle = "Upatikanaji wa Kazi"
    override val guideAvailableDesc = "Uko tayari kupokea kazi mpya za kuongoza ziara."
    override val guideUnavailableDesc = "Hutapokea kazi mpya hadi utakapowasha upatikanaji."
    override val guideTodayHeader = "SHUGHULI ZA LEO"
    override val guideNewJobsCount = "Kazi Mpya"
    override val guideUpcomingCount = "Zinazokuja"
    override val guideCompletedCount = "Zilizokamilika"
    override val guideEarningsTodayHeader = "Mapato ya Ziara za Leo"
    override val guidePendingPayout = "Yanasubiri Kuhamishwa"
    override val guidePerformanceHeader = "KIPIMO CHA UFANISI"
    override val guideRatingLabel = "Kiwango cha Nyota"
    override val guideResponseLabel = "Kasi ya Majibu"
    override val guideCompletedVisitsLabel = "Ziara Zilizofanyika"
    override val guideAcceptMissionButton = "Kubali Kazi Hii"
    override val guideArrivedButton = "Nimefika Eneo la Nyumba"
    override val guideStartInspectionButton = "Anza Ukaguzi na Mteja"
    override val guideCompleteMissionButton = "Kamilisha Ziara na Pokea Mapato"
    override val guideReportIssueButton = "Ripoti Changamoto ya Usalama"

    override val accountTitle = "Akaunti na Mipangilio"
    override val profileSectionHeader = "Wasifu wa Mtumiaji"
    override val rolesSectionHeader = "Nafasi na Majukumu ya Akaunti"
    override val switchRoleMenuTitle = "Badilisha Nafasi ya Akaunti"
    override val switchRoleMenuSubtitle = "Badilisha kati ya Mteja, Mwenye Nyumba, au Dalali"
    override val capabilitiesMenuTitle = "Majukumu na Uwezo Maalumu"
    override val capabilitiesMenuSubtitle = "Usimamizi wa usajili wa umiliki wa mali na udereva wa ziara"
    override val verificationMenuTitle = "Uhakiki wa Kitambulisho cha Taifa"
    override val verificationMenuVerifiedSubtitle = "Akaunti imethibitishwa kikamilifu kupitia NIDA"
    override val verificationMenuUnverifiedSubtitle = "Haijathibitishwa bado • Bofya kukamilisha"
    override val liveSyncMenuTitle = "Muunganisho wa Moja kwa Moja wa Wingu"
    override val liveSyncActiveSubtitle = "Imeunganishwa na mfumo mkuu mtandaoni"
    override val liveSyncOfflineSubtitle = "Inafanya kazi nje ya mtandao"
    override val systemSettingsHeader = "Mipangilio ya Mfumo"
    override val languageMenuTitle = "Lugha ya Programu"
    override val languageMenuSubtitle = "Kiswahili Fasaha"
    override val notificationsMenuTitle = "Arifa na Vikumbusho"
    override val notificationsMenuSubtitle = "Arifa za ziara, maombi ya kupanga na malipo"
    override val securityMenuTitle = "Usalama na Nenosiri"
    override val securityMenuSubtitle = "Usimamizi wa nenosiri na uthibitishaji wa akaunti"
    override val supportMenuTitle = "Msaada na Huduma kwa Wateja"
    override val supportMenuSubtitle = "Piga simu, WhatsApp au tuma barua pepe kwa kituo cha huduma"
    override val aboutMenuTitle = "Kuhusu Dalalion Pocket"
    override val aboutMenuSubtitle = "Toleo 1.0.4 • Hati Miliki © 2026 Dalalion Pocket Tanzania"
    override val termsMenuTitle = "Vigezo na Sera za Matumizi"
    override val termsMenuSubtitle = "Sera ya faragha na sheria za huduma za upangaji"
    override val logoutMenuTitle = "Ondoka Kwenye Akaunti"
    override val logoutDialogTitle = "Je, Unataka Kuondoka Kwenye Akaunti?"
    override val logoutDialogMessage = "Ukishatoka, utahitaji kuingiza tena namba ya simu na nenosiri lako ili kuingia."
    override val logoutConfirmButton = "Ndio, Ondoka Sasa"
    override val logoutCancelButton = "Ghairi"

    override val verificationDialogTitle = "Uhakiki wa Kitambulisho cha NIDA"
    override val verificationDialogDesc = "Akaunti hii imehakikiwa kisheria kulingana na miongozo ya Jamhuri ya Muungano wa Tanzania kwa usalama wa mikataba na miamala."
    override val supportDialogTitle = "Huduma kwa Wateja na Msaada"
    override val supportDialogDesc = "Kituo chetu cha huduma kipo wazi saa 24 kukusaidia kwa maswali ya ziara, upangishaji na malipo."
    override val aboutDialogTitle = "Kuhusu Dalalion Pocket Tanzania"
    override val aboutDialogDesc = "Dalalion Pocket ni mfumo jumuishi wa kwanza wa kidijitali nchini Tanzania unaowaunganisha wapangaji, wamiliki wa nyumba na madalali waaminifu bila usumbufu."
    override val termsDialogTitle = "Vigezo na Sheria za Huduma"
    override val dialogCloseButton = "Funga"
    override val dialogCancelButton = "Ghairi"

    override val loginWelcomeTitle = "Karibu Dalalion Pocket"
    override val loginWelcomeSubtitle = "Weka namba yako ya simu na nenosiri ili kuingia kwenye akaunti yako."
    override val loginPhoneLabel = "Namba ya Simu"
    override val loginPhonePlaceholder = "07xx xxx xxx"
    override val loginPasswordLabel = "Nenosiri"
    override val loginPasswordPlaceholder = "Weka nenosiri lako"
    override val loginForgotPassword = "Umesahau Nenosiri?"
    override val loginSubmitButton = "Ingia Sasa"
    override val loginNoAccountPrompt = "Huna akaunti bado?"
    override val loginSignUpAction = "Jisajili Hapa"
    override val loginLanguageSwitcherLabel = "Kiswahili"
    override val loginTermsNote = "Kwa kuingia, unakubali Vigezo vya Huduma na Sera ya Faragha ya Dalalion Pocket Tanzania."
    override val loginStarterAccountsTitle = "Akaunti Rasmi za Majaribio:"
    override val loginStarterRoleBadge = "Nafasi 3"
    override val loginSelectAccount = "Chagua"

    // Forgot Password Dialog
    override val forgotPasswordTitle = "Urejeshaji wa Nenosiri"
    override val forgotPasswordDesc = "Weka namba yako ya simu iliyosajiliwa. Utapokea ujumbe mfupi (SMS) wenye nambari ya siri ya muda ya kurejesha akaunti yako."
    override val forgotPasswordSubmitButton = "Tuma Nambari ya Siri"
    override val forgotPasswordSuccessMsg = "Ujumbe wa kurejesha nenosiri umetumwa kikamilifu!"

    // Sign Up
    override val signUpTitle = "Fungua Akaunti Yako Bure"
    override val signUpSubtitle = "Ingia kama Mteja kuanza kutafuta na kupanga nyumba zilizohakikiwa nchini Tanzania."
    override val signUpBaseAccountBannerTitle = "Akaunti ya Msingi: Mteja"
    override val signUpBaseAccountBannerDesc = "Utaingia moja kwa moja. Unaweza pia kuongeza nyumba zako au kuomba kuwa Dalali wa DoP ndani ya akaunti hii hii."
    override val signUpFullNameLabel = "Jina Kamili"
    override val signUpFullNamePlaceholder = "Mfano: Asha Juma Mndeme"
    override val signUpPhoneLabel = "Namba ya Simu"
    override val signUpPhonePlaceholder = "07xx xxx xxx"
    override val signUpEmailLabel = "Barua Pepe (Siyo Lazima)"
    override val signUpEmailPlaceholder = "jina@mfano.tz"
    override val signUpPasswordLabel = "Nenosiri"
    override val signUpPasswordPlaceholder = "Weka nenosiri thabiti"
    override val signUpConfirmPasswordLabel = "Thibitisha Nenosiri"
    override val signUpConfirmPasswordPlaceholder = "Rudia nenosiri lako"
    override val signUpTermsAgreement = "Ninakubali Vigezo vya Huduma na Sera ya Faragha ya Dalalion Pocket Tanzania."
    override val signUpSubmitButton = "Kamilisha Usajili"
    override val signUpAlreadyHaveAccount = "Tayari una akaunti?"
    override val signUpSignInAction = "Ingia Hapa"

    // Onboarding
    override val onboardingSkip = "Ruka"
    override val onboardingNext = "Endelea"
    override val onboardingGetStarted = "Anza Sasa"
    override val onboardingAlreadyHaveAccount = "Tayari nina akaunti • Ingia"
    override val onboardingSlide1Title = "Tafuta Nyumba na Viwanja"
    override val onboardingSlide1Desc = "Hakuna tena madalali wa vishoka. Vinjari maelfu ya nyumba zilizohakikiwa, ziara za moja kwa moja na picha za uhalisia."
    override val onboardingSlide2Title = "Weka Mali Yako kwa Urahisi"
    override val onboardingSlide2Desc = "Je, una nyumba, fremu au kiwanja? Washa uwezo wa umiliki kwenye akaunti yako wakati wowote na ufikie wapangaji wa uhakika."
    override val onboardingSlide3Title = "Kuwa Dalali Rasmi wa DoP"
    override val onboardingSlide3Desc = "Jiunge na mtandao rasmi wa mawakala wa uwandani waliothibitishwa. Ongoza ziara za wateja na upate mapato ya kila safari."
}

/**
 * 100% Pure, Fluent English Implementation.
 * Natural, professional English with zero Swahili loanwords or parentheses.
 */
object EnglishStrings : AppStrings {
    override val appTitle = "Dalalion Pocket"
    override val appTagline = "Tanzania Premier Digital Real Estate Ecosystem"
    override val syncOnline = "Online"
    override val syncOffline = "Offline"
    override val syncStatusConnected = "Live Cloud Synchronized"
    override val syncStatusDisconnected = "Offline Mode Active"
    override val chatTitle = "Messages"
    override val notificationsTitle = "Notifications"

    override val languageDialogTitle = "Select Application Language"
    override val languageDialogSubtitle = "Switch the application language instantly across all screens with clean, consistent phrasing."
    override val languageSwahiliTitle = "Kiswahili"
    override val languageSwahiliDesc = "Fluent and standard Swahili for all application services"
    override val languageEnglishTitle = "English"
    override val languageEnglishDesc = "Standard professional English across all services"
    override val languageActiveBadge = "Currently Active"
    override val languageConfirmButton = "Save Language"
    override val languageCloseButton = "Close"

    override val navHome = "Home"
    override val navExplore = "Explore"
    override val navShorts = "Shorts"
    override val navVisits = "My Visits"
    override val navAccount = "Account"

    override val navOwnerDashboard = "Dashboard"
    override val navOwnerProperties = "Properties"
    override val navOwnerAdd = "Add Property"
    override val navOwnerViewings = "Viewings"

    override val navGuideDashboard = "Dashboard"
    override val navGuideJobs = "Job Board"
    override val navGuideSchedule = "Schedule"
    override val navGuideEarnings = "Earnings"

    override val roleCustomerTitle = "Tenant or Buyer"
    override val roleCustomerDesc = "Search properties, schedule physical viewings, and execute secure lease agreements."
    override val roleOwnerTitle = "Property Owner or Landlord"
    override val roleOwnerDesc = "Manage property listings, upload walkthrough video clips, and review tenant applications."
    override val roleGuideTitle = "Licensed Field Guide"
    override val roleGuideDesc = "Conduct guided property tours for prospective tenants and earn verified dispatch income."
    override val roleAdminTitle = "System Administrator"
    override val roleAdminDesc = "Full platform governance and verification oversight for Dalalion Pocket."
    override val roleSwitchButton = "Switch Active Workspace"
    override val roleCurrentActive = "Currently Active Workspace"
    override val roleVerifiedBadge = "Verified"
    override val roleUnverifiedBadge = "Unverified"
    override val rolePendingApproval = "Pending Approval"

    override val heroHeadline = "Find Your Dream Property"
    override val heroSubhead = "Tanzania's premier digital operating ecosystem for houses, apartments, commercial spaces, and land."
    override val searchPlaceholder = "Search by ward, district, city, or property type..."
    override val categoryAll = "All"
    override val categoryHouse = "Houses"
    override val categoryApartment = "Apartments"
    override val categoryRoom = "Rooms"
    override val categoryCommercial = "Commercial"
    override val categoryLand = "Plots & Land"
    override val shortsSectionTitle = "Walkthrough Video Shorts"
    override val shortsViewAll = "View All"
    override val expressSectionTitle = "Instant Guided Tours"
    override val expressSectionSubtitle = "Verified properties available for immediate on-site inspection"
    override val virtualTourBannerTitle = "360° VIRTUAL TOUR"
    override val virtualTourBannerDesc = "Inspect room dimensions and finishes digitally prior to physical travel"
    override val virtualTourButton = "Explore 360°"
    override val featuredSectionTitle = "Featured Properties"

    override val perMonth = "/month"
    override val totalSalePrice = "Total Purchase Price"
    override val bedroomsLabel = "Bedrooms"
    override val bathroomsLabel = "Bathrooms"
    override val areaLabel = "Total Area"
    override val selfVisitTitle = "Self-Guided Visit"
    override val selfVisitCost = "TSh 0 (Completely Free)"
    override val assistedVisitTitle = "DoP Guide Assisted Viewing"
    override val assistedVisitCost = "TSh 5,000"
    override val assistedVisitDesc = "Licensed field guide receives TSh 2,500 and platform coordination receives TSh 2,500"

    override val detailsTitle = "Property Details"
    override val backButton = "Back"
    override val saveButton = "Save"
    override val savedButton = "Saved"
    override val shareButton = "Share"
    override val propertyDescriptionHeader = "Property Description"
    override val amenitiesHeader = "Key Amenities & Features"
    override val locationHeader = "Location & Neighborhood"
    override val ownerCardHeader = "Property Owner"
    override val guideCardHeader = "Assigned Field Guide"
    override val bookViewingAction = "Book Property Viewing"
    override val applyToRentAction = "Submit Rental Application"
    override val viewAgreementAction = "Review Lease Agreement"
    override val contactGuideAction = "Message Field Guide"
    override val callAction = "Call Now"

    override val bookViewingModalTitle = "Schedule an On-Site Property Viewing"
    override val selectViewingTypeLabel = "Select Inspection Type"
    override val selectDateLabel = "Viewing Date"
    override val selectTimeSlotLabel = "Preferred Time Window"
    override val meetingPointLabel = "Meeting Point Landmark"
    override val bookingNotesLabel = "Additional Instructions (Optional)"
    override val confirmBookingButton = "Confirm Viewing Appointment"
    override val rentalApplicationTitle = "Formal Tenancy Application"
    override val moveInDateLabel = "Anticipated Move-In Date"
    override val leaseDurationLabel = "Lease Duration (Months)"
    override val occupantsCountLabel = "Number of Occupants"
    override val occupationLabel = "Profession or Business"
    override val monthlyIncomeLabel = "Monthly Income (TSh)"
    override val submitApplicationButton = "Submit Tenancy Application"

    override val tenancyAgreementHeader = "Official Tenancy Agreement"
    override val agreementStatusActive = "Agreement Active"
    override val agreementStatusPending = "Awaiting All Signatures"
    override val agreementStatusLocked = "Fully Executed & Locked"
    override val landlordLabel = "Landlord (Property Owner)"
    override val tenantLabel = "Tenant"
    override val monthlyRentLabel = "Monthly Rent"
    override val securityDepositLabel = "Security Deposit"
    override val acceptAndSignButton = "Accept & Sign Agreement"
    override val commissionPaidBadge = "Service Fee Settled"
    override val payCommissionButton = "Pay Agreement Service Fee"

    override val paymentTitle = "Complete Secure Payment"
    override val paymentSubtitle = "Direct mobile money gateway settlement"
    override val paymentAmountLabel = "Payable Amount"
    override val paymentSelectMethod = "Select Mobile Money Network or Bank:"
    override val paymentPhoneLabel = "Payment Mobile Number"
    override val paymentPhonePlaceholder = "e.g. 0754 123 456 or +255..."
    override val paymentUssdPromptSent = "USSD Payment Prompt Dispatched!"
    override val paymentUssdInstructions = "Please enter your mobile money PIN on your handset to approve this transaction to Dalalion Pocket."
    override val payNowButton = "Pay Now via Direct Gateway"
    override val paymentCancelButton = "Cancel"
    override val paymentSuccessTitle = "Payment Successfully Completed"
    override val paymentSuccessDesc = "Your transaction has been verified and the receipt is permanently stored in your account history."

    override val ownerDashboardTitle = "Property Owner Dashboard"
    override val ownerPropertiesCount = "Your Properties"
    override val ownerNewApplications = "New Applications"
    override val ownerActiveLeases = "Active Tenancies"
    override val ownerAddNewProperty = "Add New Listing"
    override val ownerManageListings = "Manage All Properties"
    override val ownerDraftResumeTitle = "You Have an Incomplete Property Listing Draft"
    override val ownerDraftResumeButton = "Resume Drafting"
    override val ownerPostShortButton = "Post Property Video Clip"
    override val ownerBoostPropertyButton = "Promote Listing to Top"

    override val guideDashboardTitle = "Field Guide Dashboard"
    override val guideAvailableToggle = "Mission Availability"
    override val guideAvailableDesc = "You are currently open to receive new tour dispatch assignments."
    override val guideUnavailableDesc = "You will not receive new assignments until you switch availability on."
    override val guideTodayHeader = "TODAY'S MISSIONS"
    override val guideNewJobsCount = "New Jobs"
    override val guideUpcomingCount = "Upcoming"
    override val guideCompletedCount = "Completed"
    override val guideEarningsTodayHeader = "Today's Viewing Earnings"
    override val guidePendingPayout = "Pending Settlement"
    override val guidePerformanceHeader = "PERFORMANCE METRICS"
    override val guideRatingLabel = "Star Rating"
    override val guideResponseLabel = "Response Rate"
    override val guideCompletedVisitsLabel = "Completed Visits"
    override val guideAcceptMissionButton = "Accept This Mission"
    override val guideArrivedButton = "Arrived at Property"
    override val guideStartInspectionButton = "Begin Tour with Tenant"
    override val guideCompleteMissionButton = "Complete Viewing & Receive Payout"
    override val guideReportIssueButton = "Report Safety Concern"

    override val accountTitle = "Account & Settings"
    override val profileSectionHeader = "User Profile"
    override val rolesSectionHeader = "Account Roles & Workspaces"
    override val switchRoleMenuTitle = "Switch Active Workspace"
    override val switchRoleMenuSubtitle = "Toggle between Tenant, Property Owner, or Field Guide"
    override val capabilitiesMenuTitle = "Capabilities & Permissions"
    override val capabilitiesMenuSubtitle = "Manage owner credentials and field guide license authorizations"
    override val verificationMenuTitle = "National Identity Verification"
    override val verificationMenuVerifiedSubtitle = "Account fully verified via official NIDA database"
    override val verificationMenuUnverifiedSubtitle = "Unverified • Tap to submit identity verification"
    override val liveSyncMenuTitle = "Live Cloud Synchronization"
    override val liveSyncActiveSubtitle = "Fully connected to real-time sync server"
    override val liveSyncOfflineSubtitle = "Operating in offline local cache mode"
    override val systemSettingsHeader = "System Settings"
    override val languageMenuTitle = "App Language"
    override val languageMenuSubtitle = "Standard English"
    override val notificationsMenuTitle = "Notifications & Reminders"
    override val notificationsMenuSubtitle = "Alerts for property viewings, rental applications, and payments"
    override val securityMenuTitle = "Security & Passwords"
    override val securityMenuSubtitle = "Password management, two-factor authentication, and privacy"
    override val supportMenuTitle = "Help & Customer Care"
    override val supportMenuSubtitle = "Call, WhatsApp, or email our 24/7 client response center"
    override val aboutMenuTitle = "About Dalalion Pocket"
    override val aboutMenuSubtitle = "Version 1.0.4 • Copyright © 2026 Dalalion Pocket Tanzania"
    override val termsMenuTitle = "Terms of Service & Privacy Policy"
    override val termsMenuSubtitle = "Official guidelines governing property transactions and digital leases"
    override val logoutMenuTitle = "Log Out of Account"
    override val logoutDialogTitle = "Log Out of Your Account?"
    override val logoutDialogMessage = "You will need to sign in again with your phone number and password to access your account."
    override val logoutConfirmButton = "Yes, Log Out Now"
    override val logoutCancelButton = "Cancel"

    override val verificationDialogTitle = "National ID (NIDA) Verification"
    override val verificationDialogDesc = "This account is legally verified in full compliance with United Republic of Tanzania statutory requirements for digital contracts."
    override val supportDialogTitle = "Customer Support & Help Desk"
    override val supportDialogDesc = "Our 24/7 customer experience center is standing by to assist with viewings, agreements, and escrow transactions."
    override val aboutDialogTitle = "About Dalalion Pocket Tanzania"
    override val aboutDialogDesc = "Dalalion Pocket is Tanzania's first integrated digital property operating ecosystem, uniting tenants, certified landlords, and licensed field guides."
    override val termsDialogTitle = "Terms of Service & Privacy"
    override val dialogCloseButton = "Close"
    override val dialogCancelButton = "Cancel"

    override val loginWelcomeTitle = "Welcome to Dalalion Pocket"
    override val loginWelcomeSubtitle = "Enter your mobile phone number and password to access your account."
    override val loginPhoneLabel = "Phone Number"
    override val loginPhonePlaceholder = "07xx xxx xxx"
    override val loginPasswordLabel = "Password"
    override val loginPasswordPlaceholder = "Enter your password"
    override val loginForgotPassword = "Forgot Password?"
    override val loginSubmitButton = "Sign In"
    override val loginNoAccountPrompt = "Don't have an account yet?"
    override val loginSignUpAction = "Sign Up Here"
    override val loginLanguageSwitcherLabel = "English"
    override val loginTermsNote = "By signing in, you agree to the Terms of Service and Privacy Policy of Dalalion Pocket Tanzania."
    override val loginStarterAccountsTitle = "Official Starter Demo Accounts:"
    override val loginStarterRoleBadge = "3 Roles"
    override val loginSelectAccount = "Select"

    // Forgot Password Dialog
    override val forgotPasswordTitle = "Password Recovery"
    override val forgotPasswordDesc = "Enter your registered mobile phone number. You will receive an SMS containing a temporary security code to reset your account."
    override val forgotPasswordSubmitButton = "Send Security Code"
    override val forgotPasswordSuccessMsg = "Password reset instructions have been sent successfully!"

    // Sign Up
    override val signUpTitle = "Create Your Free Account"
    override val signUpSubtitle = "Sign up as a Customer to discover and rent verified properties across Tanzania."
    override val signUpBaseAccountBannerTitle = "Base Account: Customer"
    override val signUpBaseAccountBannerDesc = "You can immediately explore properties. You can also list properties or apply to become a DoP Guide anytime within this account."
    override val signUpFullNameLabel = "Full Name"
    override val signUpFullNamePlaceholder = "e.g. Asha Juma Mndeme"
    override val signUpPhoneLabel = "Phone Number"
    override val signUpPhonePlaceholder = "07xx xxx xxx"
    override val signUpEmailLabel = "Email Address (Optional)"
    override val signUpEmailPlaceholder = "name@example.tz"
    override val signUpPasswordLabel = "Password"
    override val signUpPasswordPlaceholder = "Enter a secure password"
    override val signUpConfirmPasswordLabel = "Confirm Password"
    override val signUpConfirmPasswordPlaceholder = "Repeat your password"
    override val signUpTermsAgreement = "I agree to the Terms of Service and Privacy Policy of Dalalion Pocket Tanzania."
    override val signUpSubmitButton = "Complete Registration"
    override val signUpAlreadyHaveAccount = "Already have an account?"
    override val signUpSignInAction = "Sign In Here"

    // Onboarding
    override val onboardingSkip = "Skip"
    override val onboardingNext = "Continue"
    override val onboardingGetStarted = "Get Started"
    override val onboardingAlreadyHaveAccount = "Already have an account • Sign In"
    override val onboardingSlide1Title = "Discover Homes & Plots"
    override val onboardingSlide1Desc = "No uncertified middlemen. Browse thousands of verified listings, interactive walk-throughs, and authentic photo tours."
    override val onboardingSlide2Title = "List Your Property Effortlessly"
    override val onboardingSlide2Desc = "Own a house, commercial space, or plot? Activate landlord mode anytime to reach verified, pre-screened tenants."
    override val onboardingSlide3Title = "Become an Official DoP Guide"
    override val onboardingSlide3Desc = "Join the certified network of licensed field agents. Guide in-person viewings and earn steady commissions."
}

val LocalAppStrings = compositionLocalOf<AppStrings> { SwahiliStrings }

/**
 * Returns the corresponding AppStrings provider for the given language.
 */
fun strings(lang: AppLanguage): AppStrings = when (lang) {
    AppLanguage.SWAHILI -> SwahiliStrings
    AppLanguage.ENGLISH -> EnglishStrings
}

/**
 * Legacy DoPStrings compatibility object that delegates directly to strings(lang)
 * ensuring no existing call breaks while eradicating any language mixing.
 */
object DoPStrings {
    fun get(lang: AppLanguage): AppStrings = strings(lang)

    // Top Bar & Hero
    fun appTitle(lang: AppLanguage) = strings(lang).appTitle
    fun appTagline(lang: AppLanguage) = strings(lang).appTagline
    fun heroHeadline(lang: AppLanguage) = strings(lang).heroHeadline
    fun heroSubhead(lang: AppLanguage) = strings(lang).heroSubhead
    fun searchPlaceholder(lang: AppLanguage) = strings(lang).searchPlaceholder

    // Categories
    fun all(lang: AppLanguage) = strings(lang).categoryAll
    fun house(lang: AppLanguage) = strings(lang).categoryHouse
    fun apartment(lang: AppLanguage) = strings(lang).categoryApartment
    fun room(lang: AppLanguage) = strings(lang).categoryRoom
    fun commercial(lang: AppLanguage) = strings(lang).categoryCommercial
    fun land(lang: AppLanguage) = strings(lang).categoryLand

    // Badges & Features
    fun dopExpress(lang: AppLanguage) = strings(lang).expressSectionTitle
    fun dopExpressDesc(lang: AppLanguage) = strings(lang).expressSectionSubtitle
    fun virtualTour(lang: AppLanguage) = strings(lang).virtualTourBannerTitle
    fun liveViewing(lang: AppLanguage) = strings(lang).expressSectionTitle
    fun verified(lang: AppLanguage) = strings(lang).roleVerifiedBadge

    // Bottom Nav Tabs
    fun navHome(lang: AppLanguage) = strings(lang).navHome
    fun navExplore(lang: AppLanguage) = strings(lang).navExplore
    fun navShorts(lang: AppLanguage) = strings(lang).navShorts
    fun navVisits(lang: AppLanguage) = strings(lang).navVisits
    fun navAccount(lang: AppLanguage) = strings(lang).navAccount

    // Pricing & Currency
    fun perMonth(lang: AppLanguage) = strings(lang).perMonth
    fun oneOffSale(lang: AppLanguage) = strings(lang).totalSalePrice
    fun tzs(amount: Long): String {
        return "TSh " + NumberFormat.getNumberInstance(Locale.US).format(amount)
    }

    // Viewing types
    fun selfVisitTitle(lang: AppLanguage) = strings(lang).selfVisitTitle
    fun selfVisitCost(lang: AppLanguage) = strings(lang).selfVisitCost
    fun assistedVisitTitle(lang: AppLanguage) = strings(lang).assistedVisitTitle
    fun assistedVisitCost(lang: AppLanguage) = strings(lang).assistedVisitCost
    fun assistedVisitSplit(lang: AppLanguage) = strings(lang).assistedVisitDesc

    // Action Buttons
    fun bookViewing(lang: AppLanguage) = strings(lang).bookViewingAction
    fun applyToRent(lang: AppLanguage) = strings(lang).applyToRentAction
    fun viewVirtually(lang: AppLanguage) = strings(lang).virtualTourButton
    fun acceptAgreement(lang: AppLanguage) = strings(lang).acceptAndSignButton
    fun switchRole(lang: AppLanguage) = strings(lang).roleSwitchButton
}
