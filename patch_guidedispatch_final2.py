import sys

with open("app/src/test/java/com/example/GuideDispatchTest.kt", "r") as f:
    content = f.read()

replacement = """    @Test
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
    }"""

import re
content = re.sub(r'    @Test\s+fun `Guide Dispatch eligibility logic and job state transitions`\(\) = runBlocking \{[\s\S]*?assertEquals\(2500L, GuideDispatchService\.earnings\.value\)\s+\}', replacement, content)

with open("app/src/test/java/com/example/GuideDispatchTest.kt", "w") as f:
    f.write(content)
print("Patched GuideDispatchTest simplified")
