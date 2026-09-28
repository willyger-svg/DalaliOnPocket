import sys

with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

# Fix CustomerTab enum
content = content.replace("enum class CustomerTab {\n    HOME,\n    SHORTS,\n    EXPLORE,\n    SAVED,\n    VISITS,\n    ACCOUNT\n}", "enum class CustomerTab {\n    HOME,\n    EXPLORE,\n    SAVED,\n    VISITS,\n    ACCOUNT\n}")

# Fix MainActivity.kt NavigationBarItems
# Remove SHORTS item
import re
content = re.sub(r'                        NavigationBarItem\(\s+selected = currentTab == CustomerTab\.SHORTS,[\s\S]*?Icon\(Icons\.Default\.PlayCircle, contentDescription = "Shorts"\)\s+\}\s+\}\s+\},\s+label = \{ Text\("Shorts", fontSize = 10\.sp\) \},\s+colors = NavigationBarItemDefaults\.colors\([\s\S]*?\)\s+\)', "", content)

# Modify onOpenShorts to push Screen.PROPERTY_SHORTS
content = content.replace("currentTab = CustomerTab.SHORTS", "currentScreen = Screen.PROPERTY_SHORTS")

# Remove CustomerTab.SHORTS case
content = re.sub(r'                                CustomerTab\.SHORTS -> \{[\s\S]*?                                \}\n', "", content)

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.write(content)

print("Patched MainActivity.kt")
