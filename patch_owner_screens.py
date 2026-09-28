import sys

with open("app/src/main/java/com/example/ui/screens/OwnerScreens.kt", "r") as f:
    content = f.read()

# We need to split OwnerDashboardScreen into OwnerDashboardScreen (stats only) and OwnerPropertiesScreen (properties list).
# Since the prompt says "Do NOT rebuild the whole application", maybe I can just add `OwnerPropertiesScreen` and `OwnerViewingsScreen` and leave `OwnerDashboardScreen` as an overview, or just reuse components.

import re

new_screens = """
@Composable
fun OwnerPropertiesScreen(
    lang: AppLanguage,
    onViewProperty: (Property) -> Unit,
    onAddProperty: () -> Unit
) {
    val properties by PropertyRepository.properties.collectAsState()
    val currentUser by AuthManager.currentUser.collectAsState()
    
    val myProperties = properties.filter { it.ownerId == currentUser?.id }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(DopNeutralPearl).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Mali Zangu (My Properties)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DopNavyPrimary)
                Button(onClick = onAddProperty, colors = ButtonDefaults.buttonColors(containerColor = DopNavyPrimary)) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add", fontSize = 12.sp)
                }
            }
        }
        
        if (myProperties.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Text("Anza kwa kuongeza mali yako ya kwanza.", color = DopTextSecondary)
                }
            }
        } else {
            items(myProperties) { prop ->
                DopBentoCard(onClick = { onViewProperty(prop) }) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(prop.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(DoPStrings.tzs(prop.priceTzs), fontSize = 12.sp, color = DopNavyPrimary)
                    }
                    Text("${prop.ward}, ${prop.district}", fontSize = 11.sp, color = DopTextSecondary)
                }
            }
        }
    }
}

@Composable
fun OwnerViewingsScreen(
    lang: AppLanguage,
    onViewRequest: (ViewingBooking) -> Unit
) {
    val bookings by PropertyRepository.viewingBookings.collectAsState()
    val properties by PropertyRepository.properties.collectAsState()
    val currentUser by AuthManager.currentUser.collectAsState()
    
    val myPropertyIds = properties.filter { it.ownerId == currentUser?.id }.map { it.id }
    val myBookings = bookings.filter { it.propertyId in myPropertyIds }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(DopNeutralPearl).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Maombi ya Kutazama (Viewings)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DopNavyPrimary)
        }
        
        if (myBookings.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Text("Hakuna maombi ya kutazama mali zako bado.", color = DopTextSecondary)
                }
            }
        } else {
            items(myBookings) { booking ->
                DopBentoCard(onClick = { onViewRequest(booking) }) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(booking.customerName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        DopBadge(booking.status.name, Icons.Default.Event, DopOchre, DopOchreContainer)
                    }
                    Text(booking.propertyTitle, fontSize = 12.sp, color = DopTextSecondary)
                    Text("Tarehe: ${booking.scheduledDate} ${booking.scheduledTimeSlot}", fontSize = 11.sp)
                }
            }
        }
    }
}
"""

if "fun OwnerPropertiesScreen" not in content:
    content += new_screens

with open("app/src/main/java/com/example/ui/screens/OwnerScreens.kt", "w") as f:
    f.write(content)

print("Patched OwnerScreens.kt")
