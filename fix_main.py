import sys

with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

import re

# Fix CustomerTab.SHORTS case leftover
content = re.sub(r'                                        \}\n                                    \)\n                                \}\n                                CustomerTab\.EXPLORE', '                                CustomerTab.EXPLORE', content)

# Remove the Shorts NavigationBarItem
item_start = content.find("selected = currentTab == CustomerTab.SHORTS")
if item_start != -1:
    # backtrack to NavigationBarItem
    nav_start = content.rfind("NavigationBarItem(", 0, item_start)
    # find the next NavigationBarItem
    next_nav = content.find("NavigationBarItem(", item_start)
    
    if nav_start != -1 and next_nav != -1:
        content = content[:nav_start] + content[next_nav:]

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.write(content)

print("Fixed MainActivity.kt syntax")
