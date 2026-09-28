import sys

with open("app/src/main/java/com/example/ui/screens/GuideScreens.kt", "r") as f:
    content = f.read()

target = """import androidx.compose.material3.*"""
replacement = """import androidx.compose.material3.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
"""

if target in content:
    content = content.replace(target, replacement, 1)
    content = content.replace("Icons.AutoMirrored.Filled.ArrowBack", "Icons.Default.ArrowBack")
    with open("app/src/main/java/com/example/ui/screens/GuideScreens.kt", "w") as f:
        f.write(content)
    print("Patched imports")
else:
    print("Target not found")
