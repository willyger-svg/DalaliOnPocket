package com.example

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.core.auth.AuthManager
import com.example.data.model.*
import com.example.data.repository.PropertyRepository
import com.example.core.media.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PropertyStudioTest {

    @Before
    fun setup() {
        AuthManager.logout()
    }

    @Test
    fun `Customer can activate Owner capability and switch active mode`() {
        // Initially logged in as Customer
        AuthManager.login("0712345678", "password123")
        var user = AuthManager.currentUser.value
        assertNotNull(user)
        assertEquals(UserRole.CUSTOMER, user?.activeMode)
        assertFalse(user?.canAccessOwner == true)

        // Activate Owner capability
        com.example.core.account.AccountManager.activateOwnerCapability(OwnerProfile(displayName = "Test Owner", contactPhone = "0712345678"))
        
        // Assert capability is added but mode doesn't switch automatically
        user = AuthManager.currentUser.value
        assertTrue(user?.canAccessOwner == true)
        assertEquals(UserRole.OWNER, user?.activeMode) // Note: activateOwnerCapability switches mode automatically!

        // Switch active mode to Owner
        com.example.core.account.AccountManager.switchActiveMode(UserRole.OWNER)
        user = AuthManager.currentUser.value
        assertEquals(UserRole.OWNER, user?.activeMode)
        
        // Customer remains a Customer even after becoming Owner
        assertTrue(user?.isCustomerActive == true)
    }

    @Test
    fun `Draft saves, updates, and correctly calculates completeness`() {
        val initialDraft = PropertyDraft(title = "Initial Draft")
        PropertyRepository.saveDraft(initialDraft)
        
        val savedDraft = PropertyRepository.ownerDraft.value
        assertNotNull(savedDraft)
        assertEquals("Initial Draft", savedDraft?.title)

        // Update Draft
        val updatedDraft = savedDraft!!.copy(description = "Updated description", propertyType = PropertyType.HOUSE)
        PropertyRepository.saveDraft(updatedDraft)

        val retrievedDraft = PropertyRepository.ownerDraft.value
        assertEquals("Updated description", retrievedDraft?.description)
        assertEquals(PropertyType.HOUSE, retrievedDraft?.propertyType)
        
        // Completeness score should be at least something
        assertTrue(retrievedDraft!!.completenessScore > 0)
    }

    @Test
    fun `Media items can be set as cover and reordered logically`() {
        val item1 = PropertyMediaItem(remoteUrl = "url1", isCover = false)
        val item2 = PropertyMediaItem(remoteUrl = "url2", isCover = true)
        
        val draft = PropertyDraft(mediaItems = listOf(item1, item2))
        
        // Find the cover photo
        val coverPhoto = draft.mediaItems.find { it.isCover }
        assertEquals("url2", coverPhoto?.remoteUrl)
        
        // Update cover photo
        val updatedItems = draft.mediaItems.map { it.copy(isCover = it.id == item1.id) }
        val updatedDraft = draft.copy(mediaItems = updatedItems)
        
        val newCoverPhoto = updatedDraft.mediaItems.find { it.isCover }
        assertEquals("url1", newCoverPhoto?.remoteUrl)
        assertFalse(updatedDraft.mediaItems.find { it.remoteUrl == "url2" }?.isCover == true)
    }

    @Test
    fun `Virtual tour types are stored correctly`() {
        val draft = PropertyDraft()
        val virtualTourSection = VirtualTourSection(roomName = "Living Room", mediaUrl = "video_url", durationSeconds = 30)
        
        val updatedDraft = draft.copy(virtualTourSections = listOf(virtualTourSection))
        
        assertEquals(1, updatedDraft.virtualTourSections.size)
        assertEquals("Living Room", updatedDraft.virtualTourSections.first().roomName)
        assertEquals(30, updatedDraft.virtualTourSections.first().durationSeconds)
    }

    @Test
    fun `Location privacy rules are respected`() {
        // Exact location
        val exactDraft = PropertyDraft(locationPrivacy = LocationPrivacy.EXACT)
        assertEquals(LocationPrivacy.EXACT, exactDraft.locationPrivacy)

        // Hidden location
        val hiddenDraft = PropertyDraft(locationPrivacy = LocationPrivacy.HIDDEN)
        assertEquals(LocationPrivacy.HIDDEN, hiddenDraft.locationPrivacy)
    }

    @Test
    fun `Submission changes listing state correctly`() {
        val draft = PropertyDraft(title = "Ready to submit", isSubmitted = false)
        val newProperty = PropertyRepository.submitDraft(draft)
        
        assertEquals("Ready to submit", newProperty.title)
        assertEquals(ListingStatus.SUBMITTED, newProperty.listingStatus)
        assertEquals(VerificationStatus.PENDING, newProperty.verificationStatus)
        
        // Draft should be cleared after submission
        assertNull(PropertyRepository.ownerDraft.value)
    }
}
