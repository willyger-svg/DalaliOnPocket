import sys

with open("app/src/main/java/com/example/ui/screens/CustomerScreens.kt", "r") as f:
    content = f.read()

import re

# Update imports
if "com.example.core.market.MarketService" not in content:
    content = content.replace("import com.example.core.auth.AuthManager", "import com.example.core.auth.AuthManager\nimport com.example.core.market.MarketService\nimport com.example.core.market.Region")

# Update CustomerExploreScreen
target = """    val regions = listOf("Zote", "Dar es Salaam", "Arusha", "Dodoma", "Kigamboni")

    val filteredList = properties.filter { prop ->
        (selectedRegion == "Zote" || prop.region.contains(selectedRegion, ignoreCase = true) || prop.district.contains(selectedRegion, ignoreCase = true)) &&
        (!showOnlyExpress || prop.isDopExpress)
    }"""

replacement = """    val currentMarket by MarketService.currentMarket.collectAsState()
    
    // Support filters for relevant Dar areas (Districts)
    val regions = if (currentMarket == Region.DAR_ES_SALAAM) {
        listOf("Zote", "Kinondoni", "Ilala", "Temeke", "Ubungo", "Kigamboni")
    } else {
        listOf("Zote")
    }

    val filteredList = properties.filter { prop ->
        // Only show properties in the current active market
        val inMarket = prop.region.contains(currentMarket.displayName, ignoreCase = true) || prop.district.contains(currentMarket.displayName, ignoreCase = true) || currentMarket == Region.DAR_ES_SALAAM && prop.region.contains("Dar", ignoreCase = true)
        
        val matchesFilter = selectedRegion == "Zote" || prop.district.contains(selectedRegion, ignoreCase = true) || prop.ward.contains(selectedRegion, ignoreCase = true)
        
        inMarket && matchesFilter && (!showOnlyExpress || prop.isDopExpress)
    }"""

content = content.replace(target, replacement)

# Add empty state if not there
empty_state_target = """        if (filteredList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Hakuna mali zilizopatikana kwa vigezo hivi.", color = DopTextSecondary)
            }
        } else {"""

empty_state_replacement = """        if (filteredList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Hakuna mali zilizopatikana katika eneo hili kwa sasa.", color = DopTextSecondary)
            }
        } else {"""

content = content.replace(empty_state_target, empty_state_replacement)

with open("app/src/main/java/com/example/ui/screens/CustomerScreens.kt", "w") as f:
    f.write(content)

print("Patched CustomerExploreScreen")
