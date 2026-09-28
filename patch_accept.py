import sys

with open("app/src/main/java/com/example/core/guide/GuideDispatchService.kt", "r") as f:
    content = f.read()

target = """    suspend fun acceptJob(bookingCode: String) {
        delay(500)"""

replacement = """    suspend fun acceptJob(bookingCode: String) {
        val user = AuthManager.currentUser.value ?: return
        if (user.guideCapability == com.example.data.model.GuideCapabilityStatus.SUSPENDED) {
            return
        }
        delay(500)"""

if target in content:
    content = content.replace(target, replacement, 1)
    with open("app/src/main/java/com/example/core/guide/GuideDispatchService.kt", "w") as f:
        f.write(content)
    print("Patched acceptJob")
else:
    print("Target not found")
