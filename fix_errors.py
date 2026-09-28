import sys

with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

content = content.replace("&& currentTab != CustomerTab.SHORTS", "")

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/example/ui/screens/GuideScreens.kt", "r") as f:
    guide = f.read()

if "import android.widget.Toast" not in guide:
    guide = guide.replace("import androidx.compose.ui.Modifier", "import androidx.compose.ui.Modifier\nimport android.widget.Toast")
if "import androidx.compose.ui.platform.LocalContext" not in guide:
    guide = guide.replace("import androidx.compose.ui.Modifier", "import androidx.compose.ui.Modifier\nimport androidx.compose.ui.platform.LocalContext")

with open("app/src/main/java/com/example/ui/screens/GuideScreens.kt", "w") as f:
    f.write(guide)

print("Fixed errors")
