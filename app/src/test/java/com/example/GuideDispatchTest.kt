package com.example

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.core.auth.AuthManager
import com.example.core.guide.GuideDispatchService
import com.example.data.model.*
import com.example.data.repository.PropertyRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GuideDispatchTest {

    @Before
    fun setup() {
        AuthManager.logout()
    }

    @Test
    fun `Customer can apply to become Guide and Admin can approve`() {
        AuthManager.login("0712345678", "password123")
        var user = AuthManager.currentUser.value
        assertNotNull(user)
        assertEquals(GuideCapabilityStatus.NOT_APPLIED, user?.guideCapability)

        val application = GuideApplication(
            fullName = "Test Guide",
            phone = "0712345678"
        )
        com.example.core.account.AccountManager.submitGuideApplication(application)
        
        user = AuthManager.currentUser.value
        assertEquals(GuideCapabilityStatus.SUBMITTED, user?.guideCapability)
        assertNotNull(user?.guideApplication)

        // Admin approves
        user?.id?.let { com.example.core.account.AccountManager.adminApproveGuide(it) }
        
        user = AuthManager.currentUser.value
        assertEquals(GuideCapabilityStatus.APPROVED, user?.guideCapability)
        assertTrue(user?.canAccessGuide == true)
        
        // Ensure GuideProfile was created
        assertNotNull(user?.guideProfile)
        assertEquals(GuideLevel.ROOKIE, user?.guideProfile?.level)
    }

    @Test
    fun `Guide Dispatch eligibility logic and job state transitions`() = runBlocking {
        // Just mock the minimum to test GuideDispatchService
        val job = ViewingBooking(
            bookingCode = "DOP-TEST-1234",
            propertyId = "prop1",
            propertyTitle = "Test Property",
            propertyLocation = "Masaki, Dar es Salaam",
            customerId = "CUST-1",
            customerName = "John Doe",
            viewingType = ViewingType.DOP_ASSISTED_VISIT,
            scheduledDate = "2023-11-01",
            scheduledTimeSlot = "10:00",
            meetingPoint = "Masaki",
            status = ViewingStatus.REQUESTED
        )
        
        // Push directly to repo
        val repoField = PropertyRepository::class.java.getDeclaredField("_viewingBookings")
        repoField.isAccessible = true
        val stateFlow = repoField.get(PropertyRepository) as kotlinx.coroutines.flow.MutableStateFlow<List<ViewingBooking>>
        stateFlow.value = listOf(job)
        
        GuideDispatchService.loadMockJobs()
        
        // Force the accept
        GuideDispatchService.acceptJob(job.bookingCode)
        
        // Force complete
        GuideDispatchService.completeViewing(job.bookingCode)
        
        // Verify Earnings (Assisted viewing fee split logic - Guide gets 2500)
        assertEquals(2500L, GuideDispatchService.earnings.value)
    }
}
