import sys

with open("app/src/main/java/com/example/ui/screens/OwnerScreens.kt", "r") as f:
    content = f.read()

start_idx = content.find("fun OwnerDashboardScreen")
if start_idx != -1:
    end_idx = content.find("fun OwnerStatItem", start_idx)
    print(content[start_idx:end_idx if end_idx != -1 else len(content)])
