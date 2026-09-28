import sys

with open("app/src/test/java/com/example/GuideDispatchTest.kt", "r") as f:
    content = f.read()

target = """GuideDispatchService.loadMockJobs()
        val jobs = GuideDispatchService.availableJobs.value
        assertTrue(jobs.isNotEmpty())"""

replacement = """GuideDispatchService.loadMockJobs()
        // Wait for coroutines to dispatch and update stateflow if necessary, but here we can just check PropertyRepository
        val jobs = GuideDispatchService.availableJobs.value
        // If empty, let's just assert on the PropertyRepository directly to bypass StateFlow async issues in tests if it's the culprit
        val allBookings = PropertyRepository.viewingBookings.value
        assertTrue(allBookings.isNotEmpty())
        
        val jobFromRepo = allBookings.find { it.bookingCode == job.bookingCode }!!
        assertEquals(ViewingStatus.REQUESTED, jobFromRepo.status)
        
        // Accept Job
        GuideDispatchService.acceptJob(jobFromRepo.bookingCode)"""

# Actually looking closer, the error is at GuideDispatchTest.kt:52, which is:
# fun `Guide Dispatch eligibility logic and job state transitions`() = runBlocking {

# And the failure is: AssertionError at GuideDispatchTest.kt:61  (assertTrue(jobs.isNotEmpty())) wait, the error is at line 52?!
# Let's fix by initializing PropertyRepository first maybe.
replacement2 = """    @Test
    fun `Guide Dispatch eligibility logic and job state transitions`() = runBlocking {
        // Force initialization
        PropertyRepository.properties.value
        
        val mockProperty = PropertyRepository.properties.value.first()"""

content = content.replace("""    @Test
    fun `Guide Dispatch eligibility logic and job state transitions`() = runBlocking {
        // Generate mock jobs
        val mockProperty = PropertyRepository.properties.value.first()""", replacement2)

with open("app/src/test/java/com/example/GuideDispatchTest.kt", "w") as f:
    f.write(content)
