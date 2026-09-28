import sys

with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

import re

target = """                                OwnerTab.DASHBOARD, OwnerTab.PROPERTIES, OwnerTab.VIEWINGS, OwnerTab.ADD_PROPERTY -> {
                                    OwnerDashboardScreen(
                                        lang = appLanguage,
                                        onOpenWizard = { currentScreen = Screen.OWNER_WIZARD },
                                        onViewAgreement = { agr ->
                                            selectedAgreement = agr
                                            currentScreen = Screen.TENANCY_AGREEMENT
                                        }
                                    )
                                }"""

replacement = """                                OwnerTab.DASHBOARD -> {
                                    OwnerDashboardScreen(
                                        lang = appLanguage,
                                        onOpenWizard = { currentScreen = Screen.OWNER_WIZARD },
                                        onViewAgreement = { agr ->
                                            selectedAgreement = agr
                                            currentScreen = Screen.TENANCY_AGREEMENT
                                        }
                                    )
                                }
                                OwnerTab.PROPERTIES -> {
                                    com.example.ui.screens.OwnerPropertiesScreen(
                                        lang = appLanguage,
                                        onViewProperty = { prop ->
                                            selectedProperty = prop
                                            currentScreen = Screen.PROPERTY_DETAILS
                                        },
                                        onAddProperty = { currentScreen = Screen.OWNER_WIZARD }
                                    )
                                }
                                OwnerTab.ADD_PROPERTY -> {
                                    com.example.ui.screens.OwnerPropertyWizardScreen(
                                        onDismiss = { currentOwnerTab = OwnerTab.PROPERTIES },
                                        onFinished = { currentOwnerTab = OwnerTab.PROPERTIES }
                                    )
                                }
                                OwnerTab.VIEWINGS -> {
                                    com.example.ui.screens.OwnerViewingsScreen(
                                        lang = appLanguage,
                                        onViewRequest = { }
                                    )
                                }"""

content = content.replace(target, replacement)

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.write(content)

print("Patched MainActivity.kt Owner navigation")
