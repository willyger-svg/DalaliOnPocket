import sys

with open("app/src/main/java/com/example/ui/screens/CapabilityScreens.kt", "r") as f:
    content = f.read()

target = """    val regions = listOf("Dar es Salaam", "Arusha", "Dodoma", "Mwanza", "Zanzibar")
    val darDistricts = listOf("Kinondoni", "Ilala", "Temeke", "Kigamboni", "Ubungo")"""

replacement = """    val regions = listOf("Dar es Salaam") // Initial launch focused on Dar es Salaam
    val darDistricts = listOf("Kinondoni", "Ilala", "Temeke", "Kigamboni", "Ubungo")"""

content = content.replace(target, replacement)

with open("app/src/main/java/com/example/ui/screens/CapabilityScreens.kt", "w") as f:
    f.write(content)

print("Patched CapabilityScreens.kt")
