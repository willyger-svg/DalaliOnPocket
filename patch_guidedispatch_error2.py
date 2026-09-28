import sys

with open("app/src/test/java/com/example/GuideDispatchTest.kt", "r") as f:
    content = f.read()

replacement = """    @Test
    fun `Guide Dispatch eligibility logic and job state transitions`() = runBlocking {
        // Generate mock jobs using an existing property from repo since constructor is complex
        var mockProperty = PropertyRepository.properties.value.firstOrNull()
        if (mockProperty == null) {
            // Need a valid dummy
            mockProperty = Property(
                id = "dummy", 
                title = "Dummy", 
                description = "", 
                priceTzs = 0L, 
                ward = "", 
                district = "",
                region = "", 
                transactionType = TransactionType.RENT,
                propertyType = PropertyType.HOUSE, 
                ownerId = "", 
                bedrooms = 0, 
                bathrooms = 0, 
                amenities = emptyList()
            )
        }
"""

import re
content = re.sub(r'    @Test\s+fun `Guide Dispatch eligibility logic and job state transitions`\(\) = runBlocking \{\s+// Generate mock jobs using a dummy property just in case repo is empty\s+val mockProperty = Property\(id = "dummy", title = "Dummy", description = "", price = 0\.0, ward = "", region = "", type = PropertyType\.HOUSE, ownerId = "", bedrooms = 0, bathrooms = 0, amenities = emptyList\(\)\)', replacement, content)

with open("app/src/test/java/com/example/GuideDispatchTest.kt", "w") as f:
    f.write(content)
print("Patched GuideDispatchTest constructor")
