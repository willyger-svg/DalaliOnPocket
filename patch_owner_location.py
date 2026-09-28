import sys

with open("app/src/main/java/com/example/ui/screens/OwnerPropertyStudio.kt", "r") as f:
    content = f.read()

if "import com.example.core.market.MarketService" not in content:
    content = content.replace("import com.example.core.localization.DoPStrings", "import com.example.core.localization.DoPStrings\nimport com.example.core.market.MarketConfig\nimport com.example.core.market.Region")

target = """                OutlinedTextField(
                    value = draft.region,
                    onValueChange = { onUpdate(draft.copy(region = it)) },
                    modifier = Modifier.fillMaxWidth().testTag("studio_input_region"),
                    shape = RoundedCornerShape(10.dp)
                )"""

replacement = """                OutlinedTextField(
                    value = draft.region.ifEmpty { "Dar es Salaam" },
                    onValueChange = { onUpdate(draft.copy(region = it)) },
                    modifier = Modifier.fillMaxWidth().testTag("studio_input_region"),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }
        
        val activeRegion = draft.region.ifEmpty { "Dar es Salaam" }
        if (!MarketConfig.isMarketActive(activeRegion)) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(color = DopOchreContainer, shape = RoundedCornerShape(8.dp)) {
                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = DopOchre, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("DoP kwa sasa inaanza Dar es Salaam. Tutafungua maeneo mengine hatua kwa hatua.", fontSize = 11.sp, color = DopOchre)
                }"""

# we need to replace carefully to keep brackets intact
import re
# the target block has:
#                 )
#             }
#             Column(modifier = Modifier.weight(1f)) {
#                 Text("Wilaya (District) *", fontWeight = FontWeight.Bold, fontSize = 12.sp)
content = re.sub(r'                OutlinedTextField\(\s+value = draft\.region,\s+onValueChange = \{ onUpdate\(draft\.copy\(region = it\)\) \},\s+modifier = Modifier\.fillMaxWidth\(\)\.testTag\("studio_input_region"\),\s+shape = RoundedCornerShape\(10\.dp\)\s+\)\s+\}\s+Column\(modifier = Modifier\.weight\(1f\)\) \{',
r"""                OutlinedTextField(
                    value = draft.region.ifEmpty { "Dar es Salaam" },
                    onValueChange = { onUpdate(draft.copy(region = it)) },
                    modifier = Modifier.fillMaxWidth().testTag("studio_input_region"),
                    shape = RoundedCornerShape(10.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {""", content)

# now add the warning block below the Row
content = re.sub(r'                    shape = RoundedCornerShape\(10\.dp\)\s+\)\s+\}\s+\}\s+Spacer\(modifier = Modifier\.height\(12\.dp\)\)\s+Text\("Mtaa / Eneo Mahususi \(Ward/Neighborhood\) \*",',
r"""                    shape = RoundedCornerShape(10.dp)
                )
            }
        }
        
        val activeRegion = draft.region.ifEmpty { "Dar es Salaam" }
        if (!MarketConfig.isMarketActive(activeRegion)) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(color = DopOchreContainer, shape = RoundedCornerShape(8.dp)) {
                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = DopOchre, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("DoP kwa sasa inaanza Dar es Salaam. Tutafungua maeneo mengine hatua kwa hatua.", fontSize = 11.sp, color = DopOchre)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        Text("Mtaa / Eneo Mahususi (Ward/Neighborhood) *",""", content)


with open("app/src/main/java/com/example/ui/screens/OwnerPropertyStudio.kt", "w") as f:
    f.write(content)

print("Patched OwnerPropertyStudio")
