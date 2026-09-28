import sys

with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

target = """                            Spacer(modifier = Modifier.width(6.dp))

                            // Capability Mode Selector button (Customer / Owner / Guide with Role Color Pill)
                            Surface("""

replacement = """                            Spacer(modifier = Modifier.width(6.dp))

                            if (currentUser.activeMode != UserRole.CUSTOMER) {
                            // Capability Mode Selector button (Customer / Owner / Guide with Role Color Pill)
                            Surface("""

target2 = """                                        tint = roleTokens.roleAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            // Quick Chat Icon with Guide/Customer"""

replacement2 = """                                        tint = roleTokens.roleAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            }

                            // Quick Chat Icon with Guide/Customer"""

if target in content and target2 in content:
    content = content.replace(target, replacement, 1)
    content = content.replace(target2, replacement2, 1)
    with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
        f.write(content)
    print("Patched successfully")
else:
    print("Targets not found")

