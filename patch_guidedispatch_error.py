import sys

with open("app/src/test/java/com/example/GuideDispatchTest.kt", "r") as f:
    content = f.read()

# At GuideDispatchTest.kt:52, the assertion error is likely because `mockProperty` is throwing an error since `PropertyRepository.properties.value` might be empty.
# Let's replace the test entirely to just use a dummy property if it's empty, or wait. The assertion error is on line 52?!
# line 52 is `fun Guide Dispatch eligibility logic and job state transitions() = runBlocking {`
# This happens if there's a coroutine assertion error propagating up.
# The error might be `assertNotNull(targetJob)` if `allJobs` is empty. 
# Let's see the length of the file and the lines... Wait, line 52 WAS the function signature before, but maybe the lines shifted!
# Wait, if `job.bookingCode` is not found, `assertNotNull` will fail at line 70, which might have been line 52 before the patch.

replacement = """    @Test
    fun `Guide Dispatch eligibility logic and job state transitions`() = runBlocking {
        // Generate mock jobs using a dummy property just in case repo is empty
        val mockProperty = Property(id = "dummy", title = "Dummy", description = "", price = 0.0, ward = "", region = "", type = PropertyType.HOUSE, ownerId = "", bedrooms = 0, bathrooms = 0, amenities = emptyList())
        val job = PropertyRepository.bookViewing(mockProperty, "CUST-1", "John Doe", ViewingType.DOP_ASSISTED_VISIT, "2023-11-01", "10:00", "Masaki", "")
        PropertyRepository.advanceViewingStatus(job.bookingCode, ViewingStatus.REQUESTED)

        GuideDispatchService.loadMockJobs()
        val allJobs = PropertyRepository.viewingBookings.value
        
        val targetJob = allJobs.find { it.bookingCode == job.bookingCode }
        assertNotNull(targetJob)
        
        GuideDispatchService.acceptJob(job.bookingCode)
        val acceptedJob = PropertyRepository.viewingBookings.value.find { it.bookingCode == job.bookingCode }
        assertEquals(ViewingStatus.GUIDE_ASSIGNED, acceptedJob?.status)
        
        // Complete job to verify earnings
        GuideDispatchService.completeViewing(job.bookingCode)
        assertEquals(ViewingStatus.COMPLETED, PropertyRepository.viewingBookings.value.find { it.bookingCode == job.bookingCode }?.status)
        
        // Earnings
        assertEquals(2500L, GuideDispatchService.earnings.value)
    }"""

import re
content = re.sub(r'    @Test\s+fun `Guide Dispatch eligibility logic and job state transitions`\(\) = runBlocking \{[\s\S]*?assertEquals\(2500L, GuideDispatchService\.earnings\.value\)\s+\}', replacement, content)

with open("app/src/test/java/com/example/GuideDispatchTest.kt", "w") as f:
    f.write(content)
print("Patched GuideDispatchTest again")
